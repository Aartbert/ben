package nl.han.log;

import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogRecord;

public class COCFormatter extends Formatter {
    public static final String RED = "\u001B[91m";
    public static final String YELLOW = "\u001B[93m";
    public static final String BLUE = "\u001B[94m";
    public static final String WHITE = "\u001B[97m";


    private String getSimpleClassName(String className) {
        String[] split = className.split("\\.");
        return split[split.length - 1];
    }

    @Override
    public String format(LogRecord record) {
        String simpleClassName = getSimpleClassName(record.getSourceClassName());
        String color = switch (record.getLevel().getName()){
            case "WARNING" -> YELLOW;
            case "SEVERE" -> RED;
            default -> WHITE;
        };

        return getLevelString(record.getLevel()) + color
                + simpleClassName + "." + record.getSourceMethodName() + ": "
                + record.getMessage() + "\n";
    }

    private String getLevelString(Level level){
        String color = switch (level.getName()){
            case "WARNING" -> YELLOW;
            case "SEVERE" -> RED;
            default -> BLUE;
        };

        return WHITE + "[" + color + level.getName() + WHITE + "] ";
    }
}
