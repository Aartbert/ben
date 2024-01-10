package nl.han.threephasecommit;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.extern.java.Log;
import nl.han.client.Network;
import nl.han.messages.ThreePhaseCommitMessage;
import nl.han.shared.Peer;
import nl.han.shared.Proposal;
import nl.han.shared.State;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.LinkedList;
import java.util.Queue;
import java.util.logging.Level;

/**
 * The {@code ThreePhaseCommitHandler} class manages the Three-Phase Commit protocol in a distributed system.
 * It handles the initiation of the protocol, processing of proposals, and communication with peers during each phase.
 * The steps of the Three-Phase Commit protocol include:
 * 1. Informing the leader about wanting to change the state.
 * 2. Leader sends a pre-commit message to all peers; if all peers agree, the leader sends a proposal message to all peers.
 * 3. Leader sends a proposal message to all peers; if all peers agree, the leader sends a finalize-commit message to all peers.
 * 4. Leader sends a finalize-commit message to all peers; if all peers agree, the leader sends a commit message to all peers.
 * <p>
 * Example usage:
 * ```java
 * ThreePhaseCommitHandler tpcHandler = new ThreePhaseCommitHandler(client);
 * tpcHandler.informLeader(self, leader, "Update game state");
 * ```
 *
 * @author Dylan Buil
 * @see <a href="https://confluenceasd.aimsites.nl/x/qwDjGQ">Testplan netwerk</a>
 * This class and its functions are covered in the end-to-end test NET12.
 */
@Log
@Singleton
public class ThreePhaseCommitHandler {
    private final Queue<Proposal> queue = new LinkedList<>();
    private boolean transactionLock = false;
    @Inject
    private Network network;

    private static final int TIMEOUT = 3000;

    /**
     * Informs the leader about wanting to change the state.
     * Initiates the first phase of the Three-Phase Commit protocol.
     *
     * @param self          The peer initiating the state change.
     * @param updateMessage The message indicating the desired state change.
     *                      TODO: Sending proposal to leader failed, what to do now? Did the leader fail? Try to reconnect? Start election? (for construction)
     * @author Dylan Buil, Laurens van Brecht
     */
    public void informLeader(Peer self, String updateMessage) {
        if (network.getCurrentLobby().getHost() == null) {
            log.info("No leader found");
            return;
        }


        ThreePhaseCommitMessage requestMessage = new ThreePhaseCommitMessage();
        requestMessage.setOperationType(OperationType.INITIALIZATION);
        requestMessage.setProposal(new Proposal(self, new State(updateMessage)));
        requestMessage.setSender(self);

        try (Socket socket = new Socket(network.getCurrentLobby().getHost().getIpAddress(), network.getCurrentLobby().getHost().getPort()); ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream())) {
            outputStream.writeObject(requestMessage);
            outputStream.flush();
        } catch (IOException e) {
            log.log(Level.SEVERE, "Failed to send proposal to leader", e);
        }
    }

    /**
     * Handles incoming Three-Phase Commit messages and processes them based on their operation type.
     *
     * @param message            The incoming Three-Phase Commit message.
     * @param senderOutputStream The output stream to respond to the sender.
     * @throws IOException If an I/O error occurs during message handling.
     *                     TODO: for the 'PROPOSAL' and 'FINALIZE_COMMIT' cases, check if the client agrees with the proposal and finalize commit and set the state in gamecore
     * @author Dylan Buil
     */
    public void handleIncomingMessage(ThreePhaseCommitMessage message, ObjectOutputStream senderOutputStream) {
        try {
            switch (message.getOperationType()) {
                case INITIALIZATION:
                    senderOutputStream.close();
                    queue.add(message.getProposal());
                    processProposal();
                    break;
                case CAN_COMMIT:
                    sendResponse(senderOutputStream, OperationType.ACK);
                    break;
                case PROPOSAL:
                    // TODO: refer to javadoc
                    sendResponse(senderOutputStream, OperationType.ACK);
                    break;
                case FINALIZE_COMMIT:
                    // TODO: refer to javadoc
                    sendResponse(senderOutputStream, OperationType.ACK);
                    network.getSelfPeer().setState(message.getProposal().getState());
                    log.info("State changed to: " + message.getProposal().getState());
                    break;
                default:
                    break;
            }
        } catch (IOException e) {
            log.log(Level.SEVERE, "Failed to send response to leader", e);
        }
    }

    /**
     * Sends a response to the sender with the specified operation type.
     *
     * @param senderOutputStream The output stream to respond to the sender.
     * @param operationType      The operation type to include in the response.
     *                           TODO: Sending ACK failed, what to do now? Did the leader fail? (for construction)
     * @author Dylan Buil
     */
    private void sendResponse(ObjectOutputStream senderOutputStream, OperationType operationType) {
        try {
            ThreePhaseCommitMessage response = new ThreePhaseCommitMessage();
            response.setOperationType(operationType);
            response.setSender(network.getSelfPeer());

            senderOutputStream.writeObject(response);
            senderOutputStream.flush();
            senderOutputStream.close();
        } catch (IOException e) {
            log.log(Level.SEVERE, "Failed to send ACK to sender", e);
        }
    }

    /**
     * Processes the first proposal in the queue and initiates the Three-Phase Commit protocol.
     *
     * @author Laurens van Brecht, Dylan Buil
     * TODO: Inform the peer that the sent proposal failed, so they can rollback their local changes (for construction)
     */
    public void processProposal() {
        // Only one proposal can be processed at a time
        // Then start the three phase commit protocol
        if (queue.isEmpty() || transactionLock) {
            return;
        }

        // Start the three phase commit protocol
        initiateThreePhaseCommit(queue.poll());
        // TODO: If the three phase commit protocol failed, what to do now? (for construction)

        // Process the next proposal in the queue
        processNextProposalInQueue();
    }

    /**
     * Processes the next proposal in the queue, if available.
     *
     * @author Laurens van Brecht, Dylan Buil
     */
    public void processNextProposalInQueue() {
        if (!queue.isEmpty()) {
            Proposal nextProposal = queue.poll();
            boolean success = initiateThreePhaseCommit(nextProposal);

            if (success) {
                log.info("Successfully processed the next proposal in the queue.");
            } else {
                log.info("Failed to process the next proposal in the queue.");
            }
        } else {
            log.info("No proposals left in the queue.");
        }
    }


    /**
     * Initiates the Three-Phase Commit protocol for the specified proposal.
     *
     * @param proposal The proposal to be processed.
     * @return {@code true} if the Three-Phase Commit protocol is successfully completed; {@code false} otherwise.
     * @author Dylan Buil, Laurens van Brecht
     */
    private boolean initiateThreePhaseCommit(Proposal proposal) {
        try {
            transactionLock = true;

            if (!initiateCanCommitPhase()) {
                return false;
            }

            if (!initiateProposalPhase(proposal)) {
                return false;
            }

            return initiateDoCommitPhase(proposal);
        } finally {
            transactionLock = false;
        }
    }

    /**
     * Initiates the Can-Commit phase of the Three-Phase Commit protocol.
     *
     * @return {@code true} if all participants agree; {@code false} otherwise.
     * @author Dylan Buil
     */
    private boolean initiateCanCommitPhase() {
        return initiatePhase(OperationType.CAN_COMMIT, null);
    }

    /**
     * Initiates the Proposal phase of the Three-Phase Commit protocol.
     *
     * @param proposal The proposal to be sent during the Proposal phase.
     * @return {@code true} if all participants agree; {@code false} otherwise.
     * @author Dylan Buil
     */
    private boolean initiateProposalPhase(Proposal proposal) {
        return initiatePhase(OperationType.PROPOSAL, proposal);
    }

    /**
     * Initiates the Do-Commit phase of the Three-Phase Commit protocol.
     *
     * @param proposal The proposal to be processed during the Do-Commit phase.
     * @return {@code true} if all participants agree; {@code false} otherwise.
     * @author Dylan Buil
     */
    private boolean initiateDoCommitPhase(Proposal proposal) {
        return initiatePhase(OperationType.FINALIZE_COMMIT, proposal);
    }

    /**
     * Initiates a specific phase of the Three-Phase Commit protocol.
     *
     * @param operationType The type of operation for the phase.
     * @param proposal      The proposal to be processed during the phase.
     * @return {@code true} if all participants agree; {@code false} otherwise.
     * @author Dylan Buil
     */
    private boolean initiatePhase(OperationType operationType, Proposal proposal) {
        boolean allParticipantsAgree = true;
        for (Peer participant : network.getCurrentLobby().getPeers()) {
            ThreePhaseCommitMessage message = new ThreePhaseCommitMessage(network.getSelfPeer(), operationType, proposal);
            boolean participantAgrees = sendAskForAgreement(participant, message);

            if (!participantAgrees) {
                allParticipantsAgree = false;
                break;
            }
        }

        return allParticipantsAgree;
    }

    /**
     * Sends an ask-for-agreement message to a participant in the Three-Phase Commit protocol.
     *
     * @param participant The participant to send the message to.
     * @param message     The Three-Phase Commit message to be sent.
     * @return {@code true} if the participant agrees; {@code false} otherwise.
     * TODO: Peer did not respond, what to do now? Try to reconnect? (for construction)
     * @author Dylan Buil
     */
    private boolean sendAskForAgreement(Peer participant, ThreePhaseCommitMessage message) {
        // If the participant is the client itself, return true; we don't need to send a message to ourselves; we can just use the gamecore logic locally
        if (participant.equals(network.getSelfPeer())) {
            return true;
        }

        try (Socket socket = new Socket(participant.getIpAddress(), participant.getPort()); ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream()); ObjectInputStream inputStream = new ObjectInputStream(socket.getInputStream())) {
            outputStream.writeObject(message);
            outputStream.flush();

            socket.setSoTimeout(TIMEOUT);
            ThreePhaseCommitMessage response = (ThreePhaseCommitMessage) inputStream.readObject();

            return response.getOperationType() == OperationType.ACK;
        } catch (ClassNotFoundException | IOException e) {
            log.log(Level.SEVERE, "Failed to send ask-for-agreement message to participant", e);
            return false;
        }
    }
}
