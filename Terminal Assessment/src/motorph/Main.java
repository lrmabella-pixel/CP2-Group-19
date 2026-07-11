package motorph;

import javax.swing.SwingUtilities;
import java.util.List;


  // Entry point only. (Loads Data)

public class Main {
    public static void main(String[] args) {
        List<EmployeeData> employees = EmployeeFileIO.loadEmployees(AppConfig.EMPLOYEE_FILE);
        List<AttendanceData> attendanceRecords = EmployeeFileIO.loadAttendance(AppConfig.ATTENDANCE_FILE);

        SwingUtilities.invokeLater(() -> {
            MainFrame frame = new MainFrame(employees, attendanceRecords);
            frame.setVisible(true);
        });
    }
}