package motorph;


 // Plain data holder for one audit log entry.
 // Records who did what action and when.
 
public class AuditLog {
    public String timestamp;
    public String username;
    public String role;
    public String action;
    public String details;

    public AuditLog() {
    }

    public AuditLog(String timestamp, String username, String role,
            String action, String details) {
        this.timestamp = timestamp;
        this.username = username;
        this.role = role;
        this.action = action;
        this.details = details;
    }
}