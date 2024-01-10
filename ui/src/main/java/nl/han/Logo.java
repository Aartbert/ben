package nl.han;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.logging.Level;
import java.util.stream.Collectors;

import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.gui2.Label;
import lombok.extern.java.Log;

/**
 * The logo is a label that displays the ASCII logo of the game.
 * It is created by reading a text file containing the logo.
 * 
 * @author Lars Meijerink, Vasil Verdouw
 */
@Log
public class Logo extends Label {

    /**
     * Creates a new Label with the logo as text
     * 
     * @author Lars Meijerink
     */
    public Logo() {
        super("");
        createLogoComponent();
    }

    /**
     * Creates the logo
     * 
     * @author Lars Meijerink
     */
    private void createLogoComponent() {
        String fileName = "Logo/CoC_Logo.txt";
        InputStream inputStream = Logo.class.getClassLoader().getResourceAsStream(fileName);
        if (inputStream != null) {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, Charset.defaultCharset()))) {
                setBackgroundColor(TextColor.ANSI.BLACK);
                setForegroundColor(TextColor.ANSI.WHITE);
                setText(reader.lines().collect(Collectors.joining(System.lineSeparator())));
            } catch (IOException e) {
                throw new RuntimeException("Error while reading file", e);
            }
        } else {
            throw new RuntimeException("File not found: " + fileName);
        }
    }
}
