package bg.sofia.uni.fmi.mjt.cryptocurrency.server;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.logging.FileHandler;
import java.util.logging.Logger;

public class ConfiguredLoggerCreator {

    public static Logger newLogger(String name, String filename) {
        try {
            Logger logger = Logger.getLogger(name);
            logger.addHandler(new FileHandler(filename, true));
            logger.setUseParentHandlers(false);
            return logger;
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to open the log file", e);
        } catch (SecurityException e) {
            throw new RuntimeException("No security permissions", e);
        }
    }

}
