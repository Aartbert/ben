package nl.han;

import lombok.Getter;
import lombok.Setter;
import nl.han.shared.datastructures.creature.Bot;
import nl.han.shared.datastructures.creature.Creature;
import nl.han.shared.datastructures.creature.Player;
import nl.han.shared.datastructures.game.Game;
import nl.han.shared.datastructures.world.Coordinate;
import nl.han.shared.enums.BotType;

import javax.sound.sampled.*;
import java.io.File;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Random;
import java.util.Scanner;

import static nl.han.shared.datastructures.world.Chunk.CHUNK_HEIGHT;
import static nl.han.shared.datastructures.world.Chunk.CHUNK_WIDTH;

public class AudioManager {
    @Setter
    private Game game;
    private Random random;
    @Setter
    private Player currentPlayer;
    private AudioFormat format;
    private DataLine.Info info;
    private TargetDataLine audioLine;
    private DataLine.Info sourceInfo;
    private SourceDataLine sourceLine;
    @Getter
    private boolean micActive = false;

    public AudioManager() {
        try {
            random = new Random();
            format = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, 44100, 16, 2, 4, 44100, false);;
            info = new DataLine.Info(TargetDataLine.class, format);
            audioLine = (TargetDataLine) AudioSystem.getLine(info);
            sourceInfo = new DataLine.Info(SourceDataLine.class, format);
            sourceLine = (SourceDataLine) AudioSystem.getLine(sourceInfo);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updateAudio(long tickCount) {
        game.getCreatures().stream()
                .filter(creature -> creature.getChunk().equals(currentPlayer.getChunk()) && !creature.equals(currentPlayer))
                .forEach(creature -> {
                    updateAudioPosition(creature);
                    if (creature instanceof Bot monster) attemptAudioPlayback(monster, tickCount);
                });
    }

    public void playAudioAsCreature(Creature creature, AudioInputStream audio, long tickCount) {
        try {
            creature.setLastAudioPlayback(tickCount);
            creature.setActiveAudioPlayback(true);
            Clip clip = creature.getClip();

            clip.open(audio);
            if (creature.equals(currentPlayer)) {
                FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
                gainControl.setValue(gainControl.getMinimum());
            }
            else updateAudioPosition(creature);
            clip.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void attemptAudioPlayback(Bot monster, long tickCount) {
        try {
            BotType botType = monster.getBotType();
            if (random.nextInt(101) <= botType.getAudioChance() && monster.getLastAudioPlayback() <= tickCount - botType.getAudioCooldown()) {
                InputStream inputStream = AudioManager.class.getClassLoader().getResourceAsStream(botType.getAudioPath());
                AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(inputStream);
                playAudioAsCreature(monster, audioInputStream, tickCount);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void startAudioRecording() {
        try {
            if (!AudioSystem.isLineSupported(info)) System.err.println("Line not supported");

            audioLine.open();
            audioLine.start();

            AudioInputStream audioInputStream = new AudioInputStream(audioLine);
            File audioFile = new File("record.wav");

            Thread thread = new Thread(() -> {
                try {
                    AudioSystem.write(audioInputStream, AudioFileFormat.Type.WAVE, audioFile);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });

            thread.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void stopAudioRecording() {
        audioLine.stop();
        audioLine.close();
    }

    public void openMic() {
        try {
            audioLine.open(format);
            micActive = true;
            sourceLine.open(format);

            audioLine.start();
            sourceLine.start();

            Thread thread = new Thread(() -> {
                byte[] buffer = new byte[1024];
                int bytesRead;

                while (micActive) {
                    bytesRead = audioLine.read(buffer, 0, buffer.length);
                    sourceLine.write(buffer, 0, bytesRead);
                }
            });

            thread.start();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void closeMic() {
        audioLine.stop();
        micActive = false;
        audioLine.close();
        sourceLine.stop();
        sourceLine.close();
    }

    public static void main(String[] args) {
        try {
            AudioFormat audioFormat = new AudioFormat(44100, 16, 2, true, true);
            DataLine.Info targetInfo = new DataLine.Info(TargetDataLine.class, audioFormat);
            TargetDataLine targetLine = (TargetDataLine) AudioSystem.getLine(targetInfo);

            DataLine.Info sourceInfo = new DataLine.Info(SourceDataLine.class, audioFormat);
            SourceDataLine sourceLine = (SourceDataLine) AudioSystem.getLine(sourceInfo);

            targetLine.open(audioFormat);
            sourceLine.open(audioFormat);

            targetLine.start();
            sourceLine.start();

            Thread thread = new Thread(() -> {
                byte[] buffer = new byte[1024];
                int bytesRead;

                while (true) {
                    bytesRead = targetLine.read(buffer, 0, buffer.length);
                    sourceLine.write(buffer, 0, bytesRead);
                }
            });

            thread.start();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main2(String[] args) {
        try {
            AudioFormat format = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, 44100, 16, 2, 4, 44100, false);
            DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);
            if (!AudioSystem.isLineSupported(info)) System.err.println("Line not supported");

            TargetDataLine targetDataLine = (TargetDataLine) AudioSystem.getLine(info);
            targetDataLine.open();

            System.out.println("Started recording...");
            targetDataLine.start();

            Thread thread = new Thread(() -> {
                try {
                    AudioInputStream audioInputStream = new AudioInputStream(targetDataLine);
                    File audioFile = new File("record.wav");
                    AudioSystem.write(audioInputStream, AudioFileFormat.Type.WAVE, audioFile);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });

            thread.start();
            Thread.sleep(5000);
            targetDataLine.stop();
            targetDataLine.close();
            System.out.println("Ended sound test");

            Clip clip = AudioSystem.getClip();
            clip.addLineListener(event -> {
                if (event.getType() == LineEvent.Type.STOP) {
                    clip.close();
                }
            });
            clip.open(AudioSystem.getAudioInputStream(new File("record.wav")));
            Scanner scanner = new Scanner(System.in);
            clip.start();
            scanner.nextLine();

       } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateAudioPosition(Creature creature) {
        Clip clip = creature.getClip();
        if (clip.isOpen()) {
            FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
            gainControl.setValue(calculateVolume(creature));

            FloatControl balanceControl = (FloatControl) clip.getControl(FloatControl.Type.BALANCE);
            balanceControl.setValue(calculateBalance(creature));
        }
    }

    private float calculateVolume(Creature creature) {
        double distance = currentPlayer.getCoordinate().calculateDistance(creature.getCoordinate());
        double minDistance = 0;
        double maxDistance = new Coordinate(0, 0).calculateDistance(new Coordinate(CHUNK_WIDTH - 1, CHUNK_HEIGHT - 1));
        float volume = (float) mapValue(distance, minDistance, maxDistance, 5, -30);
        return volume;
    }

    private float calculateBalance(Creature creature) {
        int relativeX = creature.getCoordinate().x() - currentPlayer.getCoordinate().x();
        int relativeY = creature.getCoordinate().y() - currentPlayer.getCoordinate().y();
        double panning = mapValue(calculateAngleToCurrentPlayer(relativeX, relativeY), 0, 90, 1, 0);
        if (relativeX < 0) return (float) -panning;
        if (relativeX > 0) return (float) panning;
        else return 0;
    }

    private float calculateElevation(Creature creature) {
        int relativeX = creature.getCoordinate().x() - currentPlayer.getCoordinate().x();
        int relativeY = creature.getCoordinate().y() - currentPlayer.getCoordinate().y();
        double panning = mapValue(calculateAngleToCurrentPlayer(relativeX, relativeY), 0, 90, 0, 1);
        if (relativeY < 0) return (float) -panning;
        if (relativeY > 0) return (float) panning;
        else return 0;
    }

    private float calculateAngleToCurrentPlayer(int relativeX, int relativeY) {
        double angleTheta = Math.atan2(relativeY, relativeX);
        double angleDegrees = Math.toDegrees(angleTheta);

        double normalizedAngle = (angleDegrees + 360) % 360;
        float angle = (float) normalizedAngle;
        if (angle >= 180) angle -= 180;
        if (angle > 90) angle = 180 - angle;
        return angle;
    }


    public static double mapValue(double value, double originalMin, double originalMax, double newMin, double newMax) {
        if (value < originalMin || value > originalMax) throw new IllegalArgumentException("Value is outside the provided range.");

        double normalizedValue = (value - originalMin) / (originalMax - originalMin);
        double mappedValue = (normalizedValue * (newMax - newMin)) + newMin;

        return mappedValue;
    }

    public AudioInputStream getMostRecentRecording() {
        try {
            return AudioSystem.getAudioInputStream(new File("record.wav"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.err.println("No recent recording available.");
        return null;

    }

    public boolean isRecording() {
        return audioLine.isActive();
    }
}