package motorph;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;


public final class SalaryRecordPanel {

    private SalaryRecordPanel() {
    }

    private static final String[] COLUMNS = {"Field", "Value"};

    public static JPanel build(List<EmployeeData> employees, List<AttendanceData> attendanceRecords) {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        panel.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        JPanel topRow = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JTextField idField = new JTextField(8);
        JComboBox<String> monthBox = new JComboBox<>(AppConfig.MONTH_NAMES);
        JTextField yearField = new JTextField("2024", 6);
        JButton viewButton = new JButton("View Salary Record");

        topRow.add(new JLabel("Employee ID:"));
        topRow.add(idField);
        topRow.add(new JLabel("Month:"));
        topRow.add(monthBox);
        topRow.add(new JLabel("Year:"));
        topRow.add(yearField);
        topRow.add(viewButton);

        DefaultTableModel tableModel = new DefaultTableModel(COLUMNS, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };
        JTable table = new JTable(tableModel);
        table.setRowHeight(26);
        table.getColumnModel().getColumn(0).setPreferredWidth(180);

        JLabel statusLabel = new JLabel(" ");

        viewButton.addActionListener(e -> {
            tableModel.setRowCount(0);
            statusLabel.setText(" ");

            int empId;
            try {
                empId = Integer.parseInt(idField.getText().trim());
            } catch (NumberFormatException ex) {
                statusLabel.setForeground(Color.RED);
                statusLabel.setText("Enter a valid numeric Employee ID.");
                return;
            }
            EmployeeData emp = EmployeeOperations.findById(employees, empId);
            if (emp == null) {
                statusLabel.setForeground(Color.RED);
                statusLabel.setText("Employee not found.");
                return;
            }
            int month = monthBox.getSelectedIndex() + 1;
            int year;
            try {
                year = Integer.parseInt(yearField.getText().trim());
            } catch (NumberFormatException ex) {
                statusLabel.setForeground(Color.RED);
                statusLabel.setText("Enter a valid year.");
                return;
            }

            List<AttendanceData> records = AttendanceOperations.getRecordsForMonth(attendanceRecords, empId, month, year);
            if (records.isEmpty()) {
                statusLabel.setForeground(Color.RED);
                statusLabel.setText("No attendance records for " + AppConfig.MONTH_NAMES[month - 1] + " " + year + ".");
                return;
            }

            PayrollResult result = PayrollCalculator.computeMonthlyPayroll(emp, records);

            tableModel.addRow(new Object[]{"Employee Number", String.valueOf(emp.employeeId)});
            tableModel.addRow(new Object[]{"Name", emp.firstName + " " + emp.lastName});
            tableModel.addRow(new Object[]{"Hourly Rate", String.format("₱%,.2f", emp.hourlyRate)});
            tableModel.addRow(new Object[]{"Hours Worked", String.format("%.2f hrs", result.totalHours)});
            tableModel.addRow(new Object[]{"  - Regular Hours", String.format("%.2f hrs", result.regularHours)});
            tableModel.addRow(new Object[]{"  - Overtime Hours", String.format("%.2f hrs", result.overtimeHours)});
            tableModel.addRow(new Object[]{"Gross Pay", String.format("₱%,.2f", result.basicPay)});
            tableModel.addRow(new Object[]{"  - SSS", String.format("(₱%,.2f)", result.sss.amount)});
            tableModel.addRow(new Object[]{"  - PhilHealth", String.format("(₱%,.2f)", result.philHealth.amount)});
            tableModel.addRow(new Object[]{"  - Pag-IBIG", String.format("(₱%,.2f)", result.pagIbig.amount)});
            tableModel.addRow(new Object[]{"  - Withholding Tax", String.format("(₱%,.2f)", result.tax.amount)});
            tableModel.addRow(new Object[]{"Total Deductions", String.format("(₱%,.2f)", result.totalDeductions)});
            tableModel.addRow(new Object[]{"Benefits Added", String.format("₱%,.2f", result.totalBenefits)});
            tableModel.addRow(new Object[]{"NET PAY", String.format("₱%,.2f", result.netPay)});

            statusLabel.setForeground(new Color(22, 101, 52));
            statusLabel.setText("Salary record loaded for " + AppConfig.MONTH_NAMES[month - 1] + " " + year + ".");
        });

        panel.add(topRow, BorderLayout.NORTH);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        panel.add(statusLabel, BorderLayout.SOUTH);
        return panel;
    }
}
