package motorph;

import javax.swing.*;
import java.awt.*;
import java.util.List;


 //  Updated to log attendance recording to AuditLogger.
 
public final class RecordAttendancePanel {

    private RecordAttendancePanel() {
    }

    public static JPanel build(List<EmployeeData> employees,
            List<AttendanceData> attendanceRecords,
            UserData loggedInUser) {

        JPanel panel = new JPanel(new BorderLayout(8, 8));
        panel.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        JPanel form = new JPanel(new GridLayout(0, 2, 8, 8));
        JTextField idField = new JTextField();
        JTextField dateField = new JTextField();
        JTextField clockInField = new JTextField();
        JTextField clockOutField = new JTextField();

        addRow(form, "Employee ID", idField);
        addRow(form, "Date (MM/dd/yyyy)", dateField);
        addRow(form, "Clock-In (H:mm, 24-hr)", clockInField);
        addRow(form, "Clock-Out (H:mm, 24-hr)", clockOutField);

        JButton recordButton = new JButton("Record Attendance");
        JLabel resultLabel = new JLabel(" ");

        recordButton.addActionListener(e -> {
            int empId;
            try {
                empId = Integer.parseInt(idField.getText().trim());
            } catch (NumberFormatException ex) {
                resultLabel.setForeground(Color.RED);
                resultLabel.setText("Enter a valid numeric Employee ID.");
                return;
            }
            EmployeeData emp = EmployeeOperations.findById(employees, empId);
            if (emp == null) {
                resultLabel.setForeground(Color.RED);
                resultLabel.setText("Employee not found.");
                return;
            }
            String date = dateField.getText().trim();
            String clockIn = clockInField.getText().trim();
            String clockOut = clockOutField.getText().trim();

            if (!date.matches("\\d{1,2}/\\d{1,2}/\\d{4}")) {
                resultLabel.setForeground(Color.RED);
                resultLabel.setText("Date must be in MM/dd/yyyy format.");
                return;
            }
            if (!clockIn.matches("\\d{1,2}:\\d{2}")
                    || !clockOut.matches("\\d{1,2}:\\d{2}")) {
                resultLabel.setForeground(Color.RED);
                resultLabel.setText("Times must be in H:mm format.");
                return;
            }

            try {
                AttendanceData record = AttendanceOperations.buildRecord(
                        empId, date, clockIn, clockOut);
                if (record.totalHours <= 0) {
                    resultLabel.setForeground(Color.RED);
                    resultLabel.setText("Clock-out must be after clock-in.");
                    return;
                }
                attendanceRecords.add(record);
                resultLabel.setForeground(new Color(22, 101, 52));
                resultLabel.setText(String.format(
                        "Recorded %.2f hrs (%.2f regular, %.2f OT) for %s %s on %s.",
                        record.totalHours, record.hoursRegular, record.hoursOvertime,
                        emp.firstName, emp.lastName, date));

                // Log to audit trail
                String user = loggedInUser != null ? loggedInUser.username : "SYSTEM";
                String role = loggedInUser != null ? loggedInUser.role : "SYSTEM";
                AuditLogger.log(user, role, "RECORD ATTENDANCE",
                        String.format("Recorded %.2f hrs for %s %s on %s.",
                                record.totalHours, emp.firstName, emp.lastName, date));

                idField.setText(""); dateField.setText("");
                clockInField.setText(""); clockOutField.setText("");
            } catch (Exception ex) {
                resultLabel.setForeground(Color.RED);
                resultLabel.setText("Could not parse time values: " + ex.getMessage());
            }
        });

        JPanel bottom = new JPanel(new BorderLayout());
        bottom.add(recordButton, BorderLayout.WEST);
        bottom.add(resultLabel, BorderLayout.CENTER);

        panel.add(form, BorderLayout.NORTH);
        panel.add(bottom, BorderLayout.SOUTH);
        return panel;
    }

    private static void addRow(JPanel form, String label, JComponent field) {
        form.add(new JLabel(label));
        form.add(field);
    }
}