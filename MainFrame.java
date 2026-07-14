package motorph;

import javax.swing.*;
import java.util.List;


 // Assembles tabs based on the logged-in user's role:
 // ADMIN: all tabs + audit trail + reports
 // HR: employee and attendance tabs only
 // PAYROLL: payroll and summary tabs only
 // FINANCE: audit trail + view-only payroll reports
 
public class MainFrame extends JFrame {

    public MainFrame(List<EmployeeData> employees,
            List<AttendanceData> attendanceRecords,
            UserData loggedInUser,
            Runnable onLogout) {

        super("MotorPH Employee App — " + loggedInUser.role
                + " (" + loggedInUser.username + ")");

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1100, 650);
        setLocationRelativeTo(null);

        JTabbedPane tabs = new JTabbedPane();
        String role = loggedInUser.role;

        Runnable onDataChanged = () -> {
            EmployeeFileIO.saveEmployees(AppConfig.EMPLOYEE_FILE, employees);
            int index = tabs.indexOfTab("View Employees");
            if (index >= 0) {
                tabs.setComponentAt(index, ViewEmployeesPanel.build(employees));
            }
        };

        //  HR + ADMIN tabs 
        if (role.equals("ADMIN") || role.equals("HR")) {
            tabs.addTab("View Employees", ViewEmployeesPanel.build(employees));
            tabs.addTab("Add Employee", AddEmployeePanel.build(employees,
                    () -> {
                        AuditLogger.log(loggedInUser.username, role,
                                "ADD EMPLOYEE", "New employee added.");
                        onDataChanged.run();
                    }));
            tabs.addTab("Edit Employee", EditEmployeePanel.build(employees,
                    () -> {
                        AuditLogger.log(loggedInUser.username, role,
                                "EDIT EMPLOYEE", "Employee record updated.");
                        onDataChanged.run();
                    }));
            tabs.addTab("Delete Employee", DeleteEmployeePanel.build(this, employees,
                    () -> {
                        AuditLogger.log(loggedInUser.username, role,
                                "DELETE EMPLOYEE", "Employee record deleted.");
                        onDataChanged.run();
                    }));
            tabs.addTab("Attendance Log",
                    AttendanceLogPanel.build(employees, attendanceRecords));
            tabs.addTab("Record Attendance",
                    RecordAttendancePanel.build(employees, attendanceRecords,
                            loggedInUser));
        }

        //  PAYROLL + ADMIN + FINANCE (view-only) tabs 
        if (role.equals("ADMIN") || role.equals("PAYROLL") || role.equals("FINANCE")) {
            tabs.addTab("Salary Record",
                    SalaryRecordPanel.build(employees, attendanceRecords));
            tabs.addTab("Payroll (Semi-Monthly)",
                    PayrollPanel.build(employees, attendanceRecords, loggedInUser));
            tabs.addTab("Monthly Summary",
                    MonthlySummaryPanel.build(employees, attendanceRecords,
                            loggedInUser));
        }

        //  FINANCE + ADMIN: Audit Trail 
        if (role.equals("ADMIN") || role.equals("FINANCE")) {
            tabs.addTab("Audit Trail", AuditTrailPanel.build());
        }

        // Menu bar: Options  (Logout, all roles) + Reports (ADMIN/PAYROLL) ─
        JMenuBar menuBar = new JMenuBar();

        JMenu sessionMenu = new JMenu("Options");
        JMenuItem logoutItem = new JMenuItem("Logout");
        logoutItem.addActionListener(e -> {
            int choice = JOptionPane.showConfirmDialog(this,
                    "Are you sure you want to log out?",
                    "Confirm Logout", JOptionPane.YES_NO_OPTION);
            if (choice == JOptionPane.YES_OPTION) {
                AuditLogger.log(loggedInUser.username, role,
                        "LOGOUT",
                        loggedInUser.username + " (" + role + ") logged out.");
                dispose();
                onLogout.run();
            }
        });
        sessionMenu.add(logoutItem);
        menuBar.add(sessionMenu);

        if (role.equals("ADMIN") || role.equals("PAYROLL") || role.equals("FINANCE")) {
            JMenu reportsMenu = new JMenu("Reports");
            JMenuItem summaryItem = new JMenuItem("Generate Payroll Summary");
            summaryItem.addActionListener(e -> {
                AuditLogger.log(loggedInUser.username, role,
                        "PAYROLL SUMMARY",
                        "Generated company-wide payroll summary.");
                PayrollSummaryModule.showSummaryDialog(
                        this, employees, attendanceRecords);
            });
            reportsMenu.add(summaryItem);
            menuBar.add(reportsMenu);
        }

        setJMenuBar(menuBar);

        add(tabs);
    }
}