package swees.utils;

import java.util.logging.Formatter;
import java.util.logging.LogRecord;

public class SimpleFormatter extends Formatter {
    @Override
    public String format(LogRecord record) {
        String fullClassName = record.getSourceClassName();
        String simpleClassName = fullClassName.substring(fullClassName.lastIndexOf('.') + 1);

        return String.format(
            "[%s] %s: %s%n",
            simpleClassName,
            record.getLevel().getName(),
            record.getMessage()
        );
    }
}