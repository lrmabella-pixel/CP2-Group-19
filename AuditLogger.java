package motorph;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;


  // Static functions only. Logs all system actions to an in-memory

public final class AuditLogger {

    private AuditLogger() {
    }

    private static final String AUDIT_FILE = "audit_log.csv";
    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    // In-memory log for display in the GUI table
    private static final List<AuditLog> logs = new ArrayList<>();

    
     // Logs one action — adds to in-memory list and appends to CSV.
      // Called from anywhere in the app after a significant action.
     
    public static void log(String username, String role,
            String action, String details) {
        String timestamp = LocalDateTime.now().format(FORMATTER);
        AuditLog entry = new AuditLog(timestamp, username, role, action, details);
        logs.add(entry);
        appendToCSV(entry);
    }

    //  Returns a copy of all log entries for display. 
    public static List<AuditLog> getLogs() {
        return new ArrayList<>(logs);
    }

    // Clears the in-memory log (does not delete the CSV).
    public static void clearLogs() {
        logs.clear();
    }

    // Appends one log entry to audit_log.csv.
    
    private static void appendToCSV(AuditLog entry) {
        java.io.File file = new java.io.File(AUDIT_FILE);
        boolean needsHeader = !file.exists();

        try (BufferedWriter writer = new BufferedWriter(
                new FileWriter(AUDIT_FILE, true))) {
            if (needsHeader) {
                writer.write("Timestamp,Username,Role,Action,Details\n");
            }
            writer.write(String.format("%s,%s,%s,%s,\"%s\"%n",
                    entry.timestamp, entry.username, entry.role,
                    entry.action, entry.details));
        } catch (IOException ex) {
            Logger.getLogger(AuditLogger.class.getName())
                    .log(Level.SEVERE, "Error writing audit log", ex);
        }
    }
}