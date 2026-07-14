package motorph;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;


 // Module that builds the Attendance tab in the GUI
 // providing a read-only view of employee logs and computed hours.
 

public final class AttendanceLogPanel {

    private AttendanceLogPanel() {
    }

    private static final String[] COLUMNS = {
        "Date", "Clock In", "Clock Out", "Regular Hrs", "OT Hrs", "Total Hrs"
    };

    public static JPanel build(List<EmployeeData> employees, List<AttendanceData> attendanceRecords) {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        panel.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        JPanel topRow = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JTextField idField = new JTextField(8);
        JComboBox<String> monthBox = new JComboBox<>(AppConfig.MONTH_NAMES);
        JTextField yearField = new JTextField("2024", 6);
        JButton viewButton = new JButton("View Log");

        topRow.add(new JLabel("Employee ID:"));
        topRow.add(idField);
        topRow.add(new JLabel("Month:"));
        topRow.add(monthBox);
        topRow.add(new JLabel("Year:"));
        topRow.add(yearField);
        topRow.add(viewButton);

        DefaultTableModel logModel = new DefaultTableModel(COLUMNS, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };
        JTable logTable = new JTable(logModel);
        JLabel totalsLabel = new JLabel(" ");

        viewButton.addActionListener(e -> {
            logModel.setRowCount(0);
            totalsLabel.setText(" ");
            int empId;
            try {
                empId = Integer.parseInt(idField.getText().trim());
            } catch (NumberFormatException ex) {
                totalsLabel.setText("Enter a valid numeric Employee ID.");
                return;
            }
            EmployeeData emp = EmployeeOperations.findById(employees, empId);
            if (emp == null) {
                totalsLabel.setText("Employee not found.");
                return;
            }
            int month = monthBox.getSelectedIndex() + 1;
            int year;
            try {
                year = Integer.parseInt(yearField.getText().trim());
            } catch (NumberFormatException ex) {
                totalsLabel.setText("Enter a valid year.");
                return;
            }

            List<AttendanceData> records = AttendanceOperations.getRecordsForMonth(attendanceRecords, empId, month, year);
            if (records.isEmpty()) {
                totalsLabel.setText("No attendance records for " + AppConfig.MONTH_NAMES[month - 1] + " " + year + ".");
                return;
            }
            for (AttendanceData r : records) {
                logModel.addRow(new Object[]{
                    r.date, r.clockIn, r.clockOut,
                    String.format("%.2f", r.hoursRegular),
                    String.format("%.2f", r.hoursOvertime),
                    String.format("%.2f", r.totalHours)
                });
            }
            double[] sums = AttendanceOperations.sumHours(records);
            totalsLabel.setText(String.format("%s %s — Total: %.2f regular + %.2f OT = %.2f hrs",
                    emp.firstName, emp.lastName, sums[0], sums[1], sums[2]));
        });

        panel.add(topRow, BorderLayout.NORTH);
        panel.add(new JScrollPane(logTable), BorderLayout.CENTER);
        panel.add(totalsLabel, BorderLayout.SOUTH);
        return panel;
    }
}
