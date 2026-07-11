package motorph;


public class PayrollResult {
    public int employeeId;
    public String employeeName;
    public double basicPay;        // or grossPay for monthly hours-based calc
    public double totalHours;
    public double regularHours;
    public double overtimeHours;
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