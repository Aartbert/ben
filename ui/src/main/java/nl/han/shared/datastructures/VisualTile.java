package nl.han.shared.datastructures;

import java.awt.*;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
/**
 * A visual tile to represent a game tile on the screen.
 */
public class VisualTile {
    private Color characterColor;
    private Color backgroundColor;
    private char character;
}
