package utils.logger;

import java.io.IOException;
import java.util.logging.*;

public class LoggingConfig {
    private static Logger logger;

    static {
        try {
            logger = Logger.getLogger(LoggingConfig.class.getName());
            FileHandler fileHandler = new FileHandler("logging.log", false);
            fileHandler.setFormatter(new SimpleFormatter());
            logger.addHandler(fileHandler);
            logger.setLevel(Level.ALL);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Logger getLogger() {
        return logger;
    }
}