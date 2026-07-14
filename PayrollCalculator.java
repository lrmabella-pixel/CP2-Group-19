package motorph;

import java.util.List;

// How it is being computed
 //Computation rules:
 // Regular Pay   = regular hours x hourly rate
 // Overtime Pay  = overtime hours x (hourly rate x 1.25)
 // Holiday Pay   = holiday hours x (hourly rate x 2.0)
 //Gross Pay     = Regular Pay + Overtime Pay + Holiday Pay
 // SSS/PhilHealth/PagIbig based on basic salary
 // Tax based on actual gross pay (monthly equivalent)
 
public final class PayrollCalculator {

    private PayrollCalculator() {
    }

    public static final double OVERTIME_RATE_MULTIPLIER = 1.25;
    public static final double HOLIDAY_RATE_MULTIPLIER = 2.0;

    public static PayrollResult computeSemiMonthlyPayroll(EmployeeData employee) {
        double monthlySss = DeductionCalculator.calculateSSS(employee.basicSalary);
        double monthlyPhilHealth = DeductionCalculator.calculatePhilHealth(employee.basicSalary);
        double monthlyPagIbig = DeductionCalculator.calculatePagIbig(employee.basicSalary);
        double monthlyTax = DeductionCalculator.calculateWithholdingTax(
                employee.basicSalary, monthlySss + monthlyPhilHealth + monthlyPagIbig);

        double sss = monthlySss / 2.0;
        double philHealth = monthlyPhilHealth / 2.0;
        double pagIbig = monthlyPagIbig / 2.0;
        double tax = monthlyTax / 2.0;
        double totalDeductions = sss + philHealth + pagIbig + tax;
        double totalBenefits = BenefitsCalculator.getTotalBenefits(employee) / 2.0;

        double netPay = employee.grossSemiMonthlyRate - totalDeductions + totalBenefits;
        if (netPay < 0) netPay = 0;

        PayrollResult result = new PayrollResult();
        result.employeeId = employee.employeeId;
        result.employeeName = employee.firstName + " " + employee.lastName;
        result.basicPay = employee.grossSemiMonthlyRate;
        result.regularPay = employee.grossSemiMonthlyRate;
        result.overtimePay = 0;
        result.holidayPay = 0;
        result.regularHours = 0;
        result.overtimeHours = 0;
        result.holidayHours = 0;
        result.totalHours = 0;
        result.sss = new DeductionData("SSS", sss);
        result.philHealth = new DeductionData("PhilHealth", philHealth);
        result.pagIbig = new DeductionData("Pag-IBIG", pagIbig);
        result.tax = new DeductionData("Withholding Tax", tax);
        result.totalDeductions = totalDeductions;
        result.totalBenefits = totalBenefits;
        result.netPay = netPay;
        return result;
    }

    public static PayrollResult computeSemiMonthlyPayrollFromAttendance(
            EmployeeData employee, List<AttendanceData> recordsForHalfMonth) {

        double[] hours = AttendanceOperations.sumHours(recordsForHalfMonth);
        double regularHours = hours[0];
        double overtimeHours = hours[1];
        double totalHours = hours[2];
        double holidayHours = hours[3];

        double regularPay = regularHours * employee.hourlyRate;
        double overtimePay = overtimeHours * (employee.hourlyRate * OVERTIME_RATE_MULTIPLIER);
        double holidayPay = holidayHours * (employee.hourlyRate * HOLIDAY_RATE_MULTIPLIER);
        double grossPay = regularPay + overtimePay + holidayPay;

        double monthlySss = DeductionCalculator.calculateSSS(employee.basicSalary);
        double monthlyPhilHealth = DeductionCalculator.calculatePhilHealth(employee.basicSalary);
        double monthlyPagIbig = DeductionCalculator.calculatePagIbig(employee.basicSalary);
        // Holiday pay is non-taxable — only regular + overtime pay is taxable
// Tax based on fixed basic salary (BIR standard practice)
// Overtime and holiday adjustments handled at year-end ITR
double monthlyTax = DeductionCalculator.calculateWithholdingTax(
        employee.basicSalary, monthlySss + monthlyPhilHealth + monthlyPagIbig);

        double sss = monthlySss / 2.0;
        double philHealth = monthlyPhilHealth / 2.0;
        double pagIbig = monthlyPagIbig / 2.0;
        double tax = monthlyTax / 2.0;
        double totalDeductions = sss + philHealth + pagIbig + tax;
        double totalBenefits = BenefitsCalculator.getTotalBenefits(employee) / 2.0;

        double netPay = grossPay - totalDeductions + totalBenefits;
        if (netPay < 0) netPay = 0;

        PayrollResult result = new PayrollResult();
        result.employeeId = employee.employeeId;
        result.employeeName = employee.firstName + " " + employee.lastName;
        result.basicPay = grossPay;
        result.regularPay = regularPay;
        result.overtimePay = overtimePay;
        result.holidayPay = holidayPay;
        result.regularHours = regularHours;
        result.overtimeHours = overtimeHours;
        result.holidayHours = holidayHours;
        result.totalHours = totalHours;
        result.sss = new DeductionData("SSS", sss);
        result.philHealth = new DeductionData("PhilHealth", philHealth);
        result.pagIbig = new DeductionData("Pag-IBIG", pagIbig);
        result.tax = new DeductionData("Withholding Tax", tax);
        result.totalDeductions = totalDeductions;
        result.totalBenefits = totalBenefits;
        result.netPay = netPay;
        return result;
    }

    public static PayrollResult computeMonthlyPayroll(
            EmployeeData employee, List<AttendanceData> recordsForMonth) {

        double[] hours = AttendanceOperations.sumHours(recordsForMonth);
        double regularHours = hours[0];
        double overtimeHours = hours[1];
        double totalHours = hours[2];
        double holidayHours = hours[3];

        double regularPay = regularHours * employee.hourlyRate;
        double overtimePay = overtimeHours * (employee.hourlyRate * OVERTIME_RATE_MULTIPLIER);
        double holidayPay = holidayHours * (employee.hourlyRate * HOLIDAY_RATE_MULTIPLIER);
        double grossPay = regularPay + overtimePay + holidayPay;

        double sss = DeductionCalculator.calculateSSS(employee.basicSalary);
        double philHealth = DeductionCalculator.calculatePhilHealth(employee.basicSalary);
        double pagIbig = DeductionCalculator.calculatePagIbig(employee.basicSalary);
        // Holiday pay is non-taxable — only regular + overtime pay is taxable
// Tax based on fixed basic salary (BIR standard practice)
double tax = DeductionCalculator.calculateWithholdingTax(
        employee.basicSalary, sss + philHealth + pagIbig);
        double totalDeductions = sss + philHealth + pagIbig + tax;
        double totalBenefits = BenefitsCalculator.getTotalBenefits(employee);

        double netPay = grossPay - totalDeductions + totalBenefits;
        if (netPay < 0) netPay = 0;

        PayrollResult result = new PayrollResult();
        result.employeeId = employee.employeeId;
        result.employeeName = employee.firstName + " " + employee.lastName;
        result.basicPay = grossPay;
        result.regularPay = regularPay;
        result.overtimePay = overtimePay;
        result.holidayPay = holidayPay;
        result.regularHours = regularHours;
        result.overtimeHours = overtimeHours;
        result.holidayHours = holidayHours;
        result.totalHours = totalHours;
        result.sss = new DeductionData("SSS", sss);
        result.philHealth = new DeductionData("PhilHealth", philHealth);
        result.pagIbig = new DeductionData("Pag-IBIG", pagIbig);
        result.tax = new DeductionData("Withholding Tax", tax);
        result.totalDeductions = totalDeductions;
        result.totalBenefits = totalBenefits;
        result.netPay = netPay;
        return result;
    }
}