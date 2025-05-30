package utils.logger;

import java.io.IOException;
import java.util.logging.*;

public class MyLogger {
    private static Logger logger;

    static {
        try {
            logger = Logger.getLogger(MyLogger.class.getName());
            FileHandler fileHandler = new FileHandler("trace.log", false);
            fileHandler.setFormatter(new MyFormatter());
            logger.addHandler(fileHandler);
            logger.setLevel(Level.ALL);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void log(String message) {
        logger.info(message);
    }

    public static void wrn(String message) {
        logger.warning(message);
    }

}