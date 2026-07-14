package motorph;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;



 //  Feature 5 — Company-wide Payroll Summary. showSummaryDialog()
 // displays a report of every employee's computed payroll for a
 // chosen period, with CSV export. Reuses DeductionCalculator and
 //  BenefitsCalculator from Feature 3.
 
public final class PayrollSummaryModule {

    private PayrollSummaryModule() {
    }

    public static void showSummaryDialog(JFrame owner, List<EmployeeData> employees, List<AttendanceData> attendanceRecords) {
        if (employees == null || employees.isEmpty()) {
            JOptionPane.showMessageDialog(owner,
                    "No employee data loaded. Please check your CSV file.",
                    "No Data", JOptionPane.WARNING_MESSAGE);
            return;
        }

        JDialog dialog = new JDialog(owner, "Payroll Summary Report", true);
        dialog.setSize(1100, 650);
        dialog.setLocationRelativeTo(owner);
        dialog.setLayout(new BorderLayout(8, 8));

        //  Top controls 
        JPanel controls = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JComboBox<String> monthBox = new JComboBox<>(AppConfig.MONTH_NAMES);
        JTextField yearField = new JTextField("2024", 6);
        JButton semiMonthlyBtn = new JButton("Semi-Monthly Report");
        JButton monthlyBtn = new JButton("Monthly Report");
        JButton exportBtn = new JButton("Export to CSV");
        exportBtn.setEnabled(false);

        controls.add(new JLabel("Month:"));
        controls.add(monthBox);
        controls.add(new JLabel("Year:"));
        controls.add(yearField);
        controls.add(semiMonthlyBtn);
        controls.add(monthlyBtn);
        controls.add(exportBtn);

        // Table 
        String[] columns = {
            "EMP #", "Name", "Position", "Gross Pay",
            "SSS", "PhilHealth", "Pag-IBIG", "Tax",
            "Total Deductions", "Benefits", "Net Pay"
        };
        DefaultTableModel tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };
        JTable table = new JTable(tableModel);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        table.getColumnModel().getColumn(0).setPreferredWidth(55);
        table.getColumnModel().getColumn(1).setPreferredWidth(160);
        table.getColumnModel().getColumn(2).setPreferredWidth(160);
        for (int i = 3; i < columns.length; i++) {
            table.getColumnModel().getColumn(i).setPreferredWidth(110);
        }

        // Track current report type for export
        final String[] currentReportType = {"none"};

        //  Semi-Monthly Report button 
        semiMonthlyBtn.addActionListener(e -> {
            tableModel.setRowCount(0);
            currentReportType[0] = "semi-monthly";

            double totGross = 0, totSSS = 0, totPH = 0;
            double totPI = 0, totTax = 0, totDed = 0;
            double totBen = 0, totNet = 0;

            for (EmployeeData emp : employees) {
                double gross = emp.grossSemiMonthlyRate;
                double sss = DeductionCalculator.calculateSSS(emp.basicSalary) / 2.0;
                double ph = DeductionCalculator.calculatePhilHealth(emp.basicSalary) / 2.0;
                double pi = DeductionCalculator.calculatePagIbig(emp.basicSalary) / 2.0;
                double tax = DeductionCalculator.calculateWithholdingTax(emp.basicSalary,
                        DeductionCalculator.calculateSSS(emp.basicSalary)
                        + DeductionCalculator.calculatePhilHealth(emp.basicSalary)
                        + DeductionCalculator.calculatePagIbig(emp.basicSalary)) / 2.0;
                double ben = BenefitsCalculator.getTotalBenefits(emp) / 2.0;
                double ded = sss + ph + pi + tax;
                double net = gross - ded + ben;

                tableModel.addRow(new Object[]{
                    emp.employeeId,
                    emp.firstName + " " + emp.lastName,
                    emp.position,
                    String.format("%.2f", gross),
                    String.format("%.2f", sss),
                    String.format("%.2f", ph),
                    String.format("%.2f", pi),
                    String.format("%.2f", tax),
                    String.format("%.2f", ded),
                    String.format("%.2f", ben),
                    String.format("%.2f", net)
                });

                totGross += gross; totSSS += sss; totPH += ph;
                totPI += pi; totTax += tax; totDed += ded;
                totBen += ben; totNet += net;
            }

            // Totals row
            tableModel.addRow(new Object[]{
                "", "── TOTALS ──", "",
                String.format("%.2f", totGross),
                String.format("%.2f", totSSS),
                String.format("%.2f", totPH),
                String.format("%.2f", totPI),
                String.format("%.2f", totTax),
                String.format("%.2f", totDed),
                String.format("%.2f", totBen),
                String.format("%.2f", totNet)
            });

            exportBtn.setEnabled(true);
        });

        // Monthly Report button
        monthlyBtn.addActionListener(e -> {
            tableModel.setRowCount(0);
            currentReportType[0] = "monthly";

            int month = monthBox.getSelectedIndex() + 1;
            int year;
            try {
                year = Integer.parseInt(yearField.getText().trim());
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog,
                        "Enter a valid year.", "Invalid Year", JOptionPane.ERROR_MESSAGE);
                return;
            }

            double totGross = 0, totSSS = 0, totPH = 0;
            double totPI = 0, totTax = 0, totDed = 0;
            double totBen = 0, totNet = 0;

            for (EmployeeData emp : employees) {
                List<AttendanceData> records = AttendanceOperations.getRecordsForMonth(
                        attendanceRecords, emp.employeeId, month, year);

                double gross, sss, ph, pi, tax, ben, ded, net;
                if (records.isEmpty()) {
                    gross = 0; sss = 0; ph = 0; pi = 0;
                    tax = 0; ded = 0;
                    ben = BenefitsCalculator.getTotalBenefits(emp);
                    net = 0;
                } else {
                    double[] hrs = AttendanceOperations.sumHours(records);
                    gross = hrs[2] * emp.hourlyRate;
                    sss = DeductionCalculator.calculateSSS(emp.basicSalary);
                    ph = DeductionCalculator.calculatePhilHealth(emp.basicSalary);
                    pi = DeductionCalculator.calculatePagIbig(emp.basicSalary);
                    tax = DeductionCalculator.calculateWithholdingTax(emp.basicSalary, sss + ph + pi);
                    ben = BenefitsCalculator.getTotalBenefits(emp);
                    ded = sss + ph + pi + tax;
                    net = gross - ded + ben;
                }

                tableModel.addRow(new Object[]{
                    emp.employeeId,
                    emp.firstName + " " + emp.lastName,
                    emp.position,
                    String.format("%.2f", gross),
                    String.format("%.2f", sss),
                    String.format("%.2f", ph),
                    String.format("%.2f", pi),
                    String.format("%.2f", tax),
                    String.format("%.2f", ded),
                    String.format("%.2f", ben),
                    String.format("%.2f", net)
                });

                totGross += gross; totSSS += sss; totPH += ph;
                totPI += pi; totTax += tax; totDed += ded;
                totBen += ben; totNet += net;
            }

            // Totals row
            tableModel.addRow(new Object[]{
                "", "── TOTALS ──", "",
                String.format("%.2f", totGross),
                String.format("%.2f", totSSS),
                String.format("%.2f", totPH),
                String.format("%.2f", totPI),
                String.format("%.2f", totTax),
                String.format("%.2f", totDed),
                String.format("%.2f", totBen),
                String.format("%.2f", totNet)
            });

            exportBtn.setEnabled(true);
        });

        //  Export to CSV button 
        exportBtn.addActionListener(e -> {
            exportTableToCSV(tableModel, currentReportType[0], monthBox, yearField);
        });

        // Assemble dialog display
        dialog.add(controls, BorderLayout.NORTH);
        dialog.add(new JScrollPane(table), BorderLayout.CENTER);
        dialog.setVisible(true);
    }

    // Exports the current table to a CSV file 
    private static void exportTableToCSV(DefaultTableModel tableModel,
            String reportType, JComboBox<String> monthBox, JTextField yearField) {

        String filename = reportType.equals("semi-monthly")
                ? "MotorPH_SemiMonthly_Payroll_Report.csv"
                : "MotorPH_Monthly_Payroll_Report_"
                + AppConfig.MONTH_NAMES[monthBox.getSelectedIndex()]
                + "_" + yearField.getText().trim() + ".csv";

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {

            // Report header
            if (reportType.equals("semi-monthly")) {
                writer.write("MotorPH Company-Wide Semi-Monthly Payroll Report\n");
            } else {
                writer.write("MotorPH Company-Wide Monthly Payroll Report - "
                        + AppConfig.MONTH_NAMES[monthBox.getSelectedIndex()]
                        + " " + yearField.getText().trim() + "\n");
            }
            writer.write("\n");

            // Column headers
            int colCount = tableModel.getColumnCount();
            for (int col = 0; col < colCount; col++) {
                writer.write(tableModel.getColumnName(col));
                if (col < colCount - 1) writer.write(",");
            }
            writer.write("\n");

            // Data rows
            for (int row = 0; row < tableModel.getRowCount(); row++) {
                for (int col = 0; col < colCount; col++) {
                    Object value = tableModel.getValueAt(row, col);
                    String cell = value == null ? "" : value.toString();
                    if (cell.contains(",")) {
                        cell = "\"" + cell + "\"";
                    }
                    writer.write(cell);
                    if (col < colCount - 1) writer.write(",");
                }
                writer.write("\n");
            }

            JOptionPane.showMessageDialog(null,
                    "Report exported successfully!\nSaved as: " + filename,
                    "Export Complete", JOptionPane.INFORMATION_MESSAGE);

        } catch (IOException ex) {
            JOptionPane.showMessageDialog(null,
                    "Error saving report: " + ex.getMessage(),
                    "Export Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}