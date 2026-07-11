package motorph;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public final class PayrollPanel {

    private PayrollPanel() {
    }

    public static JPanel build(List<EmployeeData> employees, List<AttendanceData> attendanceRecords) {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        panel.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        JPanel topRow = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JTextField idField = new JTextField(10);
        JComboBox<String> coverageBox = new JComboBox<>(new String[]{
            "1st Half (1st - 15th)", "2nd Half (16th - End of Month)"
        });
        JComboBox<String> monthBox = new JComboBox<>(AppConfig.MONTH_NAMES);
        JTextField yearField = new JTextField("2024", 6);
        JButton computeButton = new JButton("Compute Payroll");
        JButton deductionsButton = new JButton("View Deductions");
        deductionsButton.setEnabled(false);

        topRow.add(new JLabel("Employee ID:"));
        topRow.add(idField);
        topRow.add(new JLabel("Pay Coverage:"));
        topRow.add(coverageBox);
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

            int coverageIndex = coverageBox.getSelectedIndex();
            int startDay = (coverageIndex == 0) ? 1 : 16;
            int endDay = (coverageIndex == 0) ? 15 : daysInMonth(month, year);

            List<AttendanceData> records = AttendanceOperations.getRecordsForDayRange(attendanceRecords, empId, month, year, startDay, endDay);
            if (records.isEmpty()) {
                resultArea.setText("No attendance records for " + buildCoverageLabel(coverageIndex, month, year) + ".");
                return;
            }

            PayrollResult result = PayrollCalculator.computeSemiMonthlyPayrollFromAttendance(emp, records);
            String coverageLabel = buildCoverageLabel(coverageIndex, month, year);
            resultArea.setText("Pay Coverage: " + coverageLabel + "\n\n" + PayrollReportFormatter.format(emp, result, true));

            lastResult[0] = result;
            lastEmployee[0] = emp;
            deductionsButton.setEnabled(true);
        });

        deductionsButton.addActionListener(e -> {
            if (lastResult[0] == null) {
                return;
            }
            showDeductionsPopup(panel, lastEmployee[0], lastResult[0]);
        });

        panel.add(topRow, BorderLayout.NORTH);
        panel.add(new JScrollPane(resultArea), BorderLayout.CENTER);
        return panel;
    }

    private static void showDeductionsPopup(Component owner, EmployeeData emp, PayrollResult result) {
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
                result.sss.amount, result.philHealth.amount, result.pagIbig.amount, result.tax.amount,
                result.totalDeductions
        );

        JTextArea area = new JTextArea(message);
        area.setEditable(false);
        area.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 13));
        area.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        JOptionPane.showMessageDialog(owner, area, "Deductions", JOptionPane.PLAIN_MESSAGE);
    }

    private static String buildCoverageLabel(int coverageIndex, int month, int year) {
        String monthName = AppConfig.MONTH_NAMES[month - 1];
        if (coverageIndex == 0) {
            return monthName + " 1 - 15, " + year;
        } else {
            return monthName + " 16 - " + daysInMonth(month, year) + ", " + year;
        }
    }

    private static int daysInMonth(int month, int year) {
        switch (month) {
            case 1: case 3: case 5: case 7: case 8: case 10: case 12:
                return 31;
            case 4: case 6: case 9: case 11:
                return 30;
            case 2:
                boolean leap = (year % 4 == 0 && year % 100 != 0) || (year % 400 == 0);
                return leap ? 29 : 28;
            default:
                return 30;
        }
    }
}
