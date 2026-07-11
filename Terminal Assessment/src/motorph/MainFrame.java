package motorph;

import javax.swing.*;
import java.util.List;
import javax.swing.BorderFactory;

public class MainFrame extends JFrame {

    public MainFrame(List<EmployeeData> employees, List<AttendanceData> attendanceRecords) {
        super("Welcome to MotorPH Employee App");

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1100, 650);
        setLocationRelativeTo(null);

       JTabbedPane tabs = new JTabbedPane();

// Feature 5 Generate Payroll Summary button in the menu bar
JMenuBar menuBar = new JMenuBar();
JMenu reportsMenu = new JMenu("Reports");
JMenuItem summaryItem = new JMenuItem("Generate Payroll Summary");
summaryItem.addActionListener(e -> {
    PayrollSummaryModule.showSummaryDialog(this, employees, attendanceRecords);
});
reportsMenu.add(summaryItem);
menuBar.add(reportsMenu);
setJMenuBar(menuBar);

tabs.addTab("View Employees", ViewEmployeesPanel.build(employees));

        Runnable onDataChanged = () -> {
            EmployeeFileIO.saveEmployees(AppConfig.EMPLOYEE_FILE, employees);
            int index = tabs.indexOfTab("View Employees");
            tabs.setComponentAt(index, ViewEmployeesPanel.build(employees));
        };

        tabs.addTab("Add Employee", AddEmployeePanel.build(employees, onDataChanged));
        tabs.addTab("Edit Employee", EditEmployeePanel.build(employees, onDataChanged));
        tabs.addTab("Delete Employee", DeleteEmployeePanel.build(this, employees, onDataChanged));
        tabs.addTab("Attendance Log", AttendanceLogPanel.build(employees, attendanceRecords));
        tabs.addTab("Record Attendance", RecordAttendancePanel.build(employees, attendanceRecords));
        tabs.addTab("Salary Record", SalaryRecordPanel.build(employees, attendanceRecords));
tabs.addTab("Payroll (Semi-Monthly)", PayrollPanel.build(employees, attendanceRecords));
tabs.addTab("Monthly Summary", MonthlySummaryPanel.build(employees, attendanceRecords));
        add(tabs);
    }
}