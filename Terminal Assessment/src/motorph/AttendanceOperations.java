package motorph;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public final class AttendanceOperations {

    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("H:mm");

    private AttendanceOperations() {
    }

    public static AttendanceData buildRecord(int employeeId, String date, String clockIn, String clockOut) {
        LocalTime in = LocalTime.parse(clockIn, TIME_FORMAT);
        LocalTime out = LocalTime.parse(clockOut, TIME_FORMAT);

        double totalHours = java.time.Duration.between(in, out).toMinutes() / 60.0;
        if (totalHours < 0) totalHours = 0;

        double regular = Math.min(totalHours, 8.0);
        double overtime = Math.max(0.0, totalHours - 8.0);

        AttendanceData record = new AttendanceData();
        record.employeeId = employeeId;
        record.date = date;
        record.clockIn = clockIn;
        record.clockOut = clockOut;
        record.hoursRegular = regular;
        record.hoursOvertime = overtime;
        record.totalHours = totalHours;
        return record;
    }

    public static List<AttendanceData> getRecordsForEmployee(List<AttendanceData> records, int employeeId) {
        List<AttendanceData> result = new ArrayList<>();
        for (AttendanceData r : records) {
            if (r.employeeId == employeeId) {
                result.add(r);
            }
        }
        return result;
    }

    public static List<AttendanceData> getRecordsForMonth(List<AttendanceData> records, int employeeId, int month, int year) {
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

    public static List<AttendanceData> getRecordsForDayRange(List<AttendanceData> records, int employeeId, int month, int year, int startDay, int endDay) {
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

    public static double[] sumHours(List<AttendanceData> records) {
        double regular = 0, overtime = 0, total = 0;
        for (AttendanceData r : records) {
            regular += r.hoursRegular;
            overtime += r.hoursOvertime;
            total += r.totalHours;
        }
        return new double[]{regular, overtime, total};
    }
}