package motorph;


 // Plain bundle of computed payroll numbers.

public class PayrollResult {
    public int employeeId;
    public String employeeName;
    public double basicPay;
    public double regularPay;
    public double overtimePay;
    public double holidayPay;
    public double totalHours;
    public double regularHours;
    public double overtimeHours;
    public double holidayHours;
    public DeductionData sss;
    public DeductionData philHealth;
    public DeductionData pagIbig;
    public DeductionData tax;
    public double totalDeductions;
    public double totalBenefits;
    public double netPay;

    public PayrollResult() {
    }
}