package nl.han.shared;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

/**
 * The {@code State} class represents the state of a game or a specific entity in a distributed system.
 * It includes a state value that describes the current state, and it implements the {@code Serializable}
 * interface, allowing instances to be serialized for network transmission or persistent storage.
 * <p>
 * This class is annotated with Lombok annotations for automatic generation of getter and setter methods.
 * It includes a constructor to initialize the state value and overrides the {@code toString} method to provide
 * a string representation of the state for debugging and logging purposes.
 * <p>
 * The {@code equals} method is overridden to compare two State objects based on their state values for equality.
 * <p>
 * Example usage:
 * ```java
 * State gameState = new State("InProgress");
 * ```
 *
 * @author Laurens van Brecht
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class State implements Serializable {
    private String stateValue;

    /**
     * Returns a string representation of the State object.
     *
     * @return A string representing the state's details.
     * @author Laurens van Brecht
     */
    @Override
    public String toString() {
        return "State{" + "stateValue='" + stateValue + '\'' + '}';
    }
}
