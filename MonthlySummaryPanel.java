package motorph;

import javax.swing.*;
import java.awt.*;
import java.util.List;


 // Never instantiated. build() wires the "Monthly Summary" tab directly to
 // AttendanceOperations.getRecordsForMonth(...),
 // PayrollCalculator.computeMonthlyPayroll(...), and
 // PayrollReportFormatter.format(...) — all static functions.
 // Updated to log payroll computation to AuditLogger.
// Deduction Breakdown 
 
public final class MonthlySummaryPanel {

    private MonthlySummaryPanel() {
    }

    public static JPanel build(List<EmployeeData> employees,
            List<AttendanceData> attendanceRecords,
            UserData loggedInUser) {

        JPanel panel = new JPanel(new BorderLayout(8, 8));
        panel.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        JPanel topRow = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JTextField idField = new JTextField(8);
        JComboBox<String> monthBox = new JComboBox<>(AppConfig.MONTH_NAMES);
        JTextField yearField = new JTextField("2024", 6);
        JButton computeButton = new JButton("View Summary");
        JButton deductionsButton = new JButton("View Deductions");
        deductionsButton.setEnabled(false);

        topRow.add(new JLabel("Employee ID:"));
        topRow.add(idField);
        topRow.add(new JLabel("Month:"));
        topRow.add(monthBox);
        topRow.add(new JLabel("Year:"));
        topRow.add(yearField);
        topRow.add(computeButton);
        topRow.add(deductionsButton);

        JTextArea resultArea = new JTextArea(16, 50);
        resultArea.setEditable(false);
        resultArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 13));

        final PayrollResult[] lastResult = {null};
        final EmployeeData[] lastEmployee = {null};

        computeButton.addActionListener(e -> {
            deductionsButton.setEnabled(false);
            lastResult[0] = null;

            int empId;
            try {
                empId = Integer.parseInt(idField.getText().trim());
            } catch (NumberFormatException ex) {
                resultArea.setText("Enter a valid numeric Employee ID.");
                return;
            }
            EmployeeData emp = EmployeeOperations.findById(employees, empId);
            if (emp == null) {
                resultArea.setText("Employee not found.");
                return;
            }
            int month = monthBox.getSelectedIndex() + 1;
            int year;
            try {
                year = Integer.parseInt(yearField.getText().trim());
            } catch (NumberFormatException ex) {
                resultArea.setText("Enter a valid year.");
                return;
            }

            List<AttendanceData> records = AttendanceOperations.getRecordsForMonth(
                    attendanceRecords, empId, month, year);
            if (records.isEmpty()) {
                resultArea.setText("No attendance records for "
                        + AppConfig.MONTH_NAMES[month - 1] + " " + year + ".");
                return;
            }

            PayrollResult result = PayrollCalculator.computeMonthlyPayroll(
                    emp, records);
            String coverageLabel = buildCoverageLabel(month, year);
            resultArea.setText("Pay Coverage: " + coverageLabel + "\n\n"
                    + PayrollReportFormatter.format(emp, result, true));

            lastResult[0] = result;
            lastEmployee[0] = emp;
            deductionsButton.setEnabled(true);

            // Log to audit trail
            String user = loggedInUser != null ? loggedInUser.username : "SYSTEM";
            String roleStr = loggedInUser != null ? loggedInUser.role : "SYSTEM";
            AuditLogger.log(user, roleStr, "MONTHLY SUMMARY COMPUTED",
                    String.format("Monthly payroll computed for %s %s, %s.",
                            emp.firstName, emp.lastName, coverageLabel));
        });

        deductionsButton.addActionListener(e -> {
            if (lastResult[0] == null) return;
            showDeductionsPopup(panel, lastEmployee[0], lastResult[0]);
        });

        panel.add(topRow, BorderLayout.NORTH);
        panel.add(new JScrollPane(resultArea), BorderLayout.CENTER);
        return panel;
    }

    private static void showDeductionsPopup(Component owner,
            EmployeeData emp, PayrollResult result) {
        String message = String.format(
                "%s %s — Deduction Breakdown%n"
                + "------------------------------------------%n"
                + "SSS:               ₱%,10.2f%n"
                + "PhilHealth:        ₱%,10.2f%n"
                + "Pag-IBIG:          ₱%,10.2f%n"
                + "Withholding Tax:   ₱%,10.2f%n"
                + "------------------------------------------%n"
                + "Total Deductions:  ₱%,10.2f",
                emp.firstName, emp.lastName,
                result.sss.amount, result.philHealth.amount,
                result.pagIbig.amount, result.tax.amount,
                result.totalDeductions);

        JTextArea area = new JTextArea(message);
        area.setEditable(false);
        area.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 13));
        area.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        JOptionPane.showMessageDialog(owner, area,
                "Deductions", JOptionPane.PLAIN_MESSAGE);
    }

    private static String buildCoverageLabel(int month, int year) {
        String monthName = AppConfig.MONTH_NAMES[month - 1];
        int lastDay = daysInMonth(month, year);
        return monthName + " 1 - " + lastDay + ", " + year;
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