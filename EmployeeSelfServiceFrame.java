package motorph;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

 // Self-service portal for employees to view their own profile,
 //  attendance records, and payslip. Employees log in with their
 // Employee ID only — they can only see their own data.
 
public class EmployeeSelfServiceFrame extends JFrame {

    public EmployeeSelfServiceFrame(EmployeeData employee,
            List<AttendanceData> attendanceRecords,
            Runnable onBackToLogin) {

        super("MotorPH Self-Service Portal "
                + employee.firstName + " " + employee.lastName);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(850, 600);
        setLocationRelativeTo(null);

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab(" My Profile", buildProfilePanel(employee));
        tabs.addTab(" My Attendance", buildAttendancePanel(employee, attendanceRecords));
        tabs.addTab(" My Payslip", buildPayslipPanel(employee, attendanceRecords));

        // Option menu: Back to Login 
        JMenuBar menuBar = new JMenuBar();
        JMenu sessionMenu = new JMenu("OPtions");
        JMenuItem backItem = new JMenuItem("Back to Login");
        backItem.addActionListener(e -> {
            AuditLogger.log("EMP#" + employee.employeeId, "EMPLOYEE",
                    "SELF-SERVICE LOGOUT",
                    employee.firstName + " " + employee.lastName
                    + " left the self-service portal.");
            dispose();
            onBackToLogin.run();
        });
        sessionMenu.add(backItem);
        menuBar.add(sessionMenu);
        setJMenuBar(menuBar);

        add(tabs);
    }
// Displayed in the Self Service Portal ( Employees Interface) 
    // My Profile
    private static JPanel buildProfilePanel(EmployeeData emp) {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        panel.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        // Header
        JPanel header = new JPanel(new GridLayout(2, 1));
        header.setBackground(new Color(26, 26, 64));
        header.setBorder(BorderFactory.createEmptyBorder(12, 16, 12, 16));
        JLabel nameLabel = new JLabel(emp.firstName + " " + emp.lastName);
        nameLabel.setForeground(Color.WHITE);
        nameLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        JLabel posLabel = new JLabel(emp.position + "  |  " + emp.status);
        posLabel.setForeground(new Color(180, 180, 200));
        posLabel.setFont(new Font("SansSerif", Font.PLAIN, 13));
        header.add(nameLabel);
        header.add(posLabel);

        // Profile details table
        String[] columns = {"Field", "Value"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable table = new JTable(model);
        table.setRowHeight(26);
        table.getColumnModel().getColumn(0).setPreferredWidth(200);
        table.getColumnModel().getColumn(1).setPreferredWidth(400);

        model.addRow(new Object[]{"Employee ID", emp.employeeId});
        model.addRow(new Object[]{"Full Name", emp.firstName + " " + emp.lastName});
        model.addRow(new Object[]{"Birthday", emp.birthday});
        model.addRow(new Object[]{"Address", emp.address});
        model.addRow(new Object[]{"Phone Number", emp.phoneNumber});
        model.addRow(new Object[]{"Position", emp.position});
        model.addRow(new Object[]{"Status", emp.status});
        model.addRow(new Object[]{"Immediate Supervisor", emp.immediateSupervisor});
        model.addRow(new Object[]{"Basic Salary", String.format("₱%,.2f", emp.basicSalary)});
        model.addRow(new Object[]{"Hourly Rate", String.format("₱%,.2f", emp.hourlyRate)});
        model.addRow(new Object[]{"Rice Subsidy", String.format("₱%,.2f", emp.riceSubsidy)});
        model.addRow(new Object[]{"Phone Allowance", String.format("₱%,.2f", emp.phoneAllowance)});
        model.addRow(new Object[]{"Clothing Allowance", String.format("₱%,.2f", emp.clothingAllowance)});
        model.addRow(new Object[]{"SSS Number", emp.sssNumber});
        model.addRow(new Object[]{"PhilHealth Number", emp.philHealthNumber});
        model.addRow(new Object[]{"TIN Number", emp.tinNumber});
        model.addRow(new Object[]{"Pag-IBIG Number", emp.pagIbigNumber});

        panel.add(header, BorderLayout.NORTH);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        return panel;
    }

    //  My Attendance 
    private static JPanel buildAttendancePanel(EmployeeData emp,
            List<AttendanceData> attendanceRecords) {

        JPanel panel = new JPanel(new BorderLayout(8, 8));
        panel.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        JPanel topRow = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JComboBox<String> monthBox = new JComboBox<>(AppConfig.MONTH_NAMES);
        JTextField yearField = new JTextField("2024", 6);
        JButton viewBtn = new JButton("View Attendance");

        topRow.add(new JLabel("Month:"));
        topRow.add(monthBox);
        topRow.add(new JLabel("Year:"));
        topRow.add(yearField);
        topRow.add(viewBtn);

        String[] columns = {"Date", "Clock In", "Clock Out",
            "Regular Hrs", "OT Hrs", "Holiday Hrs", "Total Hrs"};
        DefaultTableModel logModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable logTable = new JTable(logModel);
        JLabel totalsLabel = new JLabel(" ");

        viewBtn.addActionListener(e -> {
            logModel.setRowCount(0);
            totalsLabel.setText(" ");

            int month = monthBox.getSelectedIndex() + 1;
            int year;
            try {
                year = Integer.parseInt(yearField.getText().trim());
            } catch (NumberFormatException ex) {
                totalsLabel.setText("Enter a valid year.");
                return;
            }

            List<AttendanceData> records = AttendanceOperations.getRecordsForMonth(
                    attendanceRecords, emp.employeeId, month, year);

            if (records.isEmpty()) {
                totalsLabel.setText("No attendance records for "
                        + AppConfig.MONTH_NAMES[month - 1] + " " + year + ".");
                return;
            }
            for (AttendanceData r : records) {
                logModel.addRow(new Object[]{
                    r.date, r.clockIn, r.clockOut,
                    String.format("%.2f", r.hoursRegular),
                    String.format("%.2f", r.hoursOvertime),
                    String.format("%.2f", r.hoursHoliday),
                    String.format("%.2f", r.totalHours)
                });
            }
            double[] sums = AttendanceOperations.sumHours(records);
            totalsLabel.setText(String.format(
                    "Total: %.2f regular + %.2f OT + %.2f holiday = %.2f hrs",
                    sums[0], sums[1], sums[3], sums[2]));
        });

        panel.add(topRow, BorderLayout.NORTH);
        panel.add(new JScrollPane(logTable), BorderLayout.CENTER);
        panel.add(totalsLabel, BorderLayout.SOUTH);
        return panel;
    }

    //  My Payslip 
    private static JPanel buildPayslipPanel(EmployeeData emp,
            List<AttendanceData> attendanceRecords) {

        JPanel panel = new JPanel(new BorderLayout(8, 8));
        panel.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        JPanel topRow = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JComboBox<String> coverageBox = new JComboBox<>(new String[]{
            "1st Half (1st - 15th)", "2nd Half (16th - End of Month)"
        });
        JComboBox<String> monthBox = new JComboBox<>(AppConfig.MONTH_NAMES);
        JTextField yearField = new JTextField("2024", 6);
        JButton viewBtn = new JButton("View Payslip");

        topRow.add(new JLabel("Pay Coverage:"));
        topRow.add(coverageBox);
        topRow.add(new JLabel("Month:"));
        topRow.add(monthBox);
        topRow.add(new JLabel("Year:"));
        topRow.add(yearField);
        topRow.add(viewBtn);

        JTextArea resultArea = new JTextArea(20, 50);
        resultArea.setEditable(false);
        resultArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 13));

        viewBtn.addActionListener(e -> {
            int month = monthBox.getSelectedIndex() + 1;
            int year;
            try {
                year = Integer.parseInt(yearField.getText().trim());
            } catch (NumberFormatException ex) {
                resultArea.setText("Enter a valid year.");
                return;
            }

            int coverageIndex = coverageBox.getSelectedIndex();
            int startDay = (coverageIndex == 0) ? 1 : 16;
            int endDay = (coverageIndex == 0) ? 15 : daysInMonth(month, year);

            List<AttendanceData> records = AttendanceOperations.getRecordsForDayRange(
                    attendanceRecords, emp.employeeId, month, year, startDay, endDay);

            if (records.isEmpty()) {
                resultArea.setText("No attendance records found for this period.");
                return;
            }

            PayrollResult result = PayrollCalculator
                    .computeSemiMonthlyPayrollFromAttendance(emp, records);

            String coverageLabel = AppConfig.MONTH_NAMES[month - 1]
                    + (coverageIndex == 0 ? " 1 - 15, " : " 16 - "
                    + endDay + ", ") + year;

            resultArea.setText("Pay Coverage: " + coverageLabel + "\n\n"
                    + PayrollReportFormatter.format(emp, result, true));
        });

        panel.add(topRow, BorderLayout.NORTH);
        panel.add(new JScrollPane(resultArea), BorderLayout.CENTER);
        return panel;
    }

    private static int daysInMonth(int month, int year) {
        switch (month) {
            case 1: case 3: case 5: case 7: case 8: case 10: case 12:
                return 31;
            case 4: case 6: case 9: case 11:
                return 30;
            case 2:
                return ((year % 4 == 0 && year % 100 != 0)
                        || (year % 400 == 0)) ? 29 : 28;
            default:
                return 30;
        }
    }
}