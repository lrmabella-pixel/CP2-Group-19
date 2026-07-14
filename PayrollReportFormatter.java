package motorph;


 // Formats a PayrollResult into a readable payslip string.
 // Shows overtime at 1.25x rate and holiday at 2.0x rate.

public final class PayrollReportFormatter {

    private PayrollReportFormatter() {
    }

    public static String format(EmployeeData emp, PayrollResult r, boolean monthly) {
        StringBuilder sb = new StringBuilder();

        // Header
        sb.append("========================================================\n");
        sb.append("                   MOTORPH PAYSLIP                     \n");
        sb.append("========================================================\n");

        //  Employee Info 
        sb.append(String.format("%-25s %s%n", "Employee:", r.employeeName));
        sb.append(String.format("%-25s %s%n", "Position:", emp.position));
        sb.append(String.format("%-25s %s%n", "Status:", emp.status));
        sb.append("--------------------------------------------------------\n");

        // Hours Section 
        sb.append(String.format("%-25s %10.2f hrs%n", "Regular Hours:", r.regularHours));
        sb.append(String.format("%-25s %10.2f hrs%n", "Overtime Hours:", r.overtimeHours));
        sb.append(String.format("%-25s %10.2f hrs%n", "Holiday Hours:", r.holidayHours));
        sb.append(String.format("%-25s %10.2f hrs%n", "Total Hours:", r.totalHours));
        sb.append(String.format("%-25s %s%n", "Hourly Rate:",
                String.format("₱%,.2f", emp.hourlyRate)));
        sb.append("--------------------------------------------------------\n");

        //  Earnings Section 
        sb.append(String.format("%-25s %s%n", "Regular Pay:",
                String.format("₱%,.2f", r.regularPay)));
       sb.append(String.format("%-25s %s%n", "Overtime Pay:",
        String.format("₱%,.2f", r.overtimePay)));
sb.append(String.format("%-25s %s%n", "Holiday Pay:",
        String.format("₱%,.2f", r.holidayPay)));
        sb.append(String.format("%-25s %s%n", "Benefits Added:",
                String.format("₱%,.2f", r.totalBenefits)));
        double totalGross = r.basicPay + r.totalBenefits;
sb.append(String.format("%-25s %s%n", "Gross Pay:",
        String.format("₱%,.2f", totalGross)));
        sb.append("--------------------------------------------------------\n");

        //  Deductions Section 
        sb.append(String.format("%-25s %s%n", "SSS:",
                String.format("(₱%,.2f)", r.sss.amount)));
        sb.append(String.format("%-25s %s%n", "PhilHealth:",
                String.format("(₱%,.2f)", r.philHealth.amount)));
        sb.append(String.format("%-25s %s%n", "Pag-IBIG:",
                String.format("(₱%,.2f)", r.pagIbig.amount)));
        sb.append(String.format("%-25s %s%n", "Tax:",
                String.format("(₱%,.2f)", r.tax.amount)));
        sb.append(String.format("%-25s %s%n", "Total Deductions:",
                String.format("(₱%,.2f)", r.totalDeductions)));
        sb.append("========================================================\n");

        // Net Pay 
        sb.append(String.format("%-25s %s%n", "NET PAY:",
                String.format("₱%,.2f", r.netPay)));
        sb.append("========================================================\n");

        return sb.toString();
    }
}