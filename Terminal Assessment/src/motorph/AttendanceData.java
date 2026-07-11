package motorph;


public class AttendanceData {
    public int employeeId;
    public String date;       // MM/dd/yyyy
    public String clockIn;    // H:mm
    public String clockOut;   // H:mm
    public double hoursRegular;
    public double hoursOvertime;
    public double totalHours;

    public AttendanceData() {
    }
}