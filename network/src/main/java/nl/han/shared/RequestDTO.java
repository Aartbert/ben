package nl.han.shared;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * The {@code RequestDTO} class represents a Data Transfer Object (DTO) used for transferring information
 * related to a client's request in a distributed system. It includes fields such as the client's username,
 * game name, and IP address.
 * <p>
 * This class is designed to be a simple container for data, providing getter and setter methods for accessing
 * and modifying the values of its fields. It is commonly used to facilitate the exchange of information between
 * different components of a system.
 * <p>
 * The class includes a default constructor for creating instances without specific values and getter and setter
 * methods for each field to enable manipulation of the DTO's data.
 * <p>
 * Example usage:
 * ```java
 * RequestDTO request = new RequestDTO();
 * request.setUserName("JohnDoe");
 * request.setGameName("Chess");
 * request.setIpAddress("192.168.1.1");
 * ```
 *
 * @author Dylan Buil
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RequestDTO {
    private String userName;
    private String gameName;
    private String ipAddress;
}
