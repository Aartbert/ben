package nl.han.screens.createlobby;

import com.googlecode.lanterna.gui2.TextBox;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;

import static com.googlecode.lanterna.gui2.Interactable.Result.HANDLED;

public class LongTextBox extends TextBox {
    public LongTextBox(String initialText) {
        super(initialText);
    }

    @Override
    public synchronized Result handleKeyStroke(KeyStroke keyStroke) {
        KeyType keyType = keyStroke.getKeyType();
        Character character = keyStroke.getCharacter();

        if (keyType.equals(KeyType.Character) && isCaretAtStart() && character.equals('-') && !getText().startsWith("-")) {
            return super.handleKeyStroke(keyStroke);
        }

        if (!keyType.equals(KeyType.Character)) {
            return super.handleKeyStroke(keyStroke);
        }

        if (Character.isDigit(character) && !isInputAtMaxLength()) {
            return super.handleKeyStroke(keyStroke);
        }

        return HANDLED;
    }

    public boolean isCaretAtStart() {
        return getCaretPosition().getColumn() == 0;
    }

    public boolean isInputAtMaxLength() {
        String text = getText();

        return text.startsWith("-") && text.length() >= String.valueOf(Long.MIN_VALUE).length()
                || !text.startsWith("-") && text.length() >= String.valueOf(Long.MAX_VALUE).length();
    }
}

