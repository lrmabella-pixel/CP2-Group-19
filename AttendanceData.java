package motorph;


 // Plain data holder for one attendance log entry.
 // hoursHoliday tracks hours worked on Philippine Regular Holidays
 // which are paid at double rate (200%).

public class AttendanceData {
    public int employeeId;
    public String date;
    public String clockIn;
    public String clockOut;
    public double hoursRegular;
    public double hoursOvertime;
    public double hoursHoliday;
    public double totalHours;

    public AttendanceData() {
    }
}