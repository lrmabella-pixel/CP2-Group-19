package motorph;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;


// Static functions for AttendanceData — builds and queries records,
 // detects Philippine Regular Holidays, and marks holiday hours (double pay).


public final class AttendanceOperations {

    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("H:mm");

    private AttendanceOperations() {
    }

    
     // Builds one AttendanceData record from raw clock-in/clock-out strings.
      // If the date is a Philippine Regular Holiday, ALL hours worked are
      // counted as holiday hours (double pay). Otherwise splits into
     // regular (first 8 hrs) and overtime (beyond 8 hrs).
     
    public static AttendanceData buildRecord(int employeeId, String date,
            String clockIn, String clockOut) {

        LocalTime in = LocalTime.parse(clockIn, TIME_FORMAT);
        LocalTime out = LocalTime.parse(clockOut, TIME_FORMAT);

        double totalHours = java.time.Duration.between(in, out).toMinutes() / 60.0;
        if (totalHours < 0) totalHours = 0;

        AttendanceData record = new AttendanceData();
        record.employeeId = employeeId;
        record.date = date;
        record.clockIn = clockIn;
        record.clockOut = clockOut;
        record.totalHours = totalHours;

        if (PhilippineHolidays.isRegularHoliday(date)) {
    record.hoursRegular = 0;
    record.hoursOvertime = 0;
    record.hoursHoliday = totalHours;
} else {
    record.hoursRegular = Math.min(totalHours, 8.0);
    record.hoursOvertime = Math.max(0.0, totalHours - 8.0);
    record.hoursHoliday = 0;
}
        return record;
    }

    //  Filters a list of records down to one employee.
    public static List<AttendanceData> getRecordsForEmployee(
            List<AttendanceData> records, int employeeId) {
        List<AttendanceData> result = new ArrayList<>();
        for (AttendanceData r : records) {
            if (r.employeeId == employeeId) {
                result.add(r);
            }
        }
        return result;
    }

    // Filters a list of records down to one employee + one calendar month/year. 
    public static List<AttendanceData> getRecordsForMonth(
            List<AttendanceData> records, int employeeId, int month, int year) {
        List<AttendanceData> result = new ArrayList<>();
        for (AttendanceData r : records) {
            if (r.employeeId != employeeId) continue;
            String[] parts = r.date.split("/");
            int m = Integer.parseInt(parts[0]);
            int y = Integer.parseInt(parts[2]);
            if (m == month && y == year) {
                result.add(r);
            }
        }
        return result;
    }

    // Filters records to one employee + one specific day range within a month/year. 
    public static List<AttendanceData> getRecordsForDayRange(
            List<AttendanceData> records, int employeeId,
            int month, int year, int startDay, int endDay) {
        List<AttendanceData> result = new ArrayList<>();
        for (AttendanceData r : records) {
            if (r.employeeId != employeeId) continue;
            String[] parts = r.date.split("/");
            int m = Integer.parseInt(parts[0]);
            int d = Integer.parseInt(parts[1]);
            int y = Integer.parseInt(parts[2]);
            if (m == month && y == year && d >= startDay && d <= endDay) {
                result.add(r);
            }
        }
        return result;
    }

    
     // Sums regular/overtime/holiday/total hours across a list of records.
     // Returns double[] { regular, overtime, total, holiday }
     
    public static double[] sumHours(List<AttendanceData> records) {
        double regular = 0, overtime = 0, total = 0, holiday = 0;
        for (AttendanceData r : records) {
            regular += r.hoursRegular;
            overtime += r.hoursOvertime;
            total += r.totalHours;
            holiday += r.hoursHoliday;
        }
        return new double[]{regular, overtime, total, holiday};
    }
}