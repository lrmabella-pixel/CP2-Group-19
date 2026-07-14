package motorph;

import javax.swing.SwingUtilities;
import java.awt.Window;
import java.util.List;


 // Entry point. Loads data, shows login dialog, then opens MainFrame
 //  with tabs filtered by the logged-in user's role.
 
 // After Logout, showLoginThenMain() is called again so the app returns
 // to the login screen instead of exiting.
 
public class Main {
    public static void main(String[] args) {
        List<EmployeeData> employees = EmployeeFileIO.loadEmployees(
                AppConfig.EMPLOYEE_FILE);
        List<AttendanceData> attendanceRecords = EmployeeFileIO.loadAttendance(
                AppConfig.ATTENDANCE_FILE);
        List<UserData> users = UserFileIO.loadUsers(AppConfig.USERS_FILE);

        SwingUtilities.invokeLater(() ->
                showLoginThenMain(users, employees, attendanceRecords));
    }

    static void showLoginThenMain(List<UserData> users,
            List<EmployeeData> employees,
            List<AttendanceData> attendanceRecords) {

        UserData loggedInUser = LoginPanel.showLoginDialog(
                users, employees, attendanceRecords);

        if (loggedInUser == null) {
            // No user logged in. This happens either because the login
            // dialog was closed/cancelled, or because Employee Self-Service
            // was chosen (which opens its own window). Only quit the app
            // if nothing else is left open.
            boolean anotherWindowOpen = false;
            for (Window w : Window.getWindows()) {
                if (w.isDisplayable() && w.isVisible()) {
                    anotherWindowOpen = true;
                    break;
                }
            }
            if (!anotherWindowOpen) {
                System.exit(0);
            }
            return;
        }

        MainFrame frame = new MainFrame(employees, attendanceRecords,
                loggedInUser,
                () -> showLoginThenMain(users, employees, attendanceRecords));
        frame.setVisible(true);
    }
}