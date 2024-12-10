package pe.edu.utp.pf_api.util;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Properties;

public class LogFile {
    private static final String LOG_FILE;
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    static {
        String logFile = "/var/log/wildfly/estacionamiento_API.log"; // valor por defecto
        try {
            Properties props = new Properties();
            props.load(LogFile.class.getClassLoader().getResourceAsStream("app.properties"));
            String configuredLogFile = props.getProperty("errorLog");
            if (configuredLogFile != null && !configuredLogFile.trim().isEmpty()) {
                logFile = configuredLogFile;
            }
        } catch (Exception e) {
            System.err.println("Error loading log file configuration: " + e.getMessage());
        }
        LOG_FILE = logFile;
    }

    public static void error(String message) {
        write("ERROR", message);
    }

    public static void info(String message) {
        write("INFO", message);
    }

    private static synchronized void write(String level, String message) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(LOG_FILE, true))) {
            String timestamp = LocalDateTime.now().format(formatter);
            writer.println(String.format("[%s] %s: %s", timestamp, level, message));
        } catch (IOException e) {
            System.err.println("Error writing to log file: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
