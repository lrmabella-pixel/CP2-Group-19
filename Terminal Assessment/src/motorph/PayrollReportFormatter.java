package motorph;


public final class PayrollReportFormatter {

    private PayrollReportFormatter() {
    }

    public static String format(EmployeeData emp, PayrollResult r, boolean monthly) {
        StringBuilder sb = new StringBuilder();
        sb.append(r.employeeName).append("  (").append(emp.position).append(")\n");
        sb.append("------------------------------------------------------\n");
        if (monthly) {
            sb.append(String.format("Regular Hours:      %8.2f hrs%n", r.regularHours));
            sb.append(String.format("Overtime Hours:     %8.2f hrs%n", r.overtimeHours));
            sb.append(String.format("Total Hours:         %8.2f hrs%n", r.totalHours));
            sb.append(String.format("Hourly Rate:        ₱%,10.2f%n", emp.hourlyRate));
            sb.append(String.format("Gross Pay:          ₱%,10.2f%n", r.basicPay));
        } else {
            sb.append(String.format("Basic Pay (Semi-Monthly): ₱%,10.2f%n", r.basicPay));
        }
        sb.append("------------------------------------------------------\n");
        sb.append(String.format("Benefits Added:     ₱%,10.2f%n", r.totalBenefits));
        sb.append(String.format("SSS:               (₱%,10.2f)%n", r.sss.amount));
        sb.append(String.format("PhilHealth:        (₱%,10.2f)%n", r.philHealth.amount));
        sb.append(String.format("Pag-IBIG:          (₱%,10.2f)%n", r.pagIbig.amount));
        sb.append(String.format("Withholding Tax:   (₱%,10.2f)%n", r.tax.amount));
        sb.append(String.format("Total Deductions:  (₱%,10.2f)%n", r.totalDeductions));
        sb.append("------------------------------------------------------\n");
        sb.append(String.format("NET PAY:            ₱%,10.2f%n", r.netPay));
        return sb.toString();
    }
}
