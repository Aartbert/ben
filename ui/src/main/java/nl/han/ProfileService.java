package nl.han;

import java.io.*;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.stream.Collectors;

import com.googlecode.lanterna.TextColor;
import nl.han.enums.ProfileType;
import nl.han.exceptions.FileReadingException;
import nl.han.interfaces.IProfile;

/**
 * The ProfileService class is responsible for loading and retrieving profiles based on the value of the 'profile' variable.
 */
public class ProfileService implements IProfile {

    /**
     * Loads the appropriate profile based on the value of the 'profile' variable.
     * The profile can be NEUTRAL, OFFENSIVE, or DEFENSIVE.
     */
    @Override
    public String loadProfile(ProfileType profile) {
        return switch (profile) {
            case NEUTRAL -> retrieveFileContent("profile/neutral.txt");
            case OFFENSIVE -> retrieveFileContent("profile/offensive.txt");
            case DEFENSIVE -> retrieveFileContent("profile/defensive.txt");
            default -> throw new IllegalStateException("Illegal profile");
        };
    }

    /**
     * Reads the contents of a file and returns it as a string.
     *
     * @param fileName the name of the file to be read
     * @return the contents of the file as a string
     */
    private String retrieveFileContent(String fileName) {

        InputStream inputStream = ProfileService.class.getClassLoader().getResourceAsStream(fileName);
        if (inputStream != null) {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, Charset.defaultCharset()))) {
                return reader.lines().collect(Collectors.joining(System.lineSeparator()));
            } catch (IOException e) {
                throw new RuntimeException("Error while reading file", e);
            }
        } else {
            throw new RuntimeException("File not found: " + fileName);
        }
    }
}
