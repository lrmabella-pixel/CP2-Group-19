package motorph;

import java.util.List;

public final class PayrollCalculator {

    private PayrollCalculator() {
    }

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
        double totalBenefits = BenefitsCalculator.getTotalBenefits(employee);

        double netPay = employee.grossSemiMonthlyRate - totalDeductions + totalBenefits;
        if (netPay < 0) netPay = 0;

        PayrollResult result = new PayrollResult();
        result.employeeId = employee.employeeId;
        result.employeeName = employee.firstName + " " + employee.lastName;
        result.basicPay = employee.grossSemiMonthlyRate;
        result.sss = new DeductionData("SSS", sss);
        result.philHealth = new DeductionData("PhilHealth", philHealth);
        result.pagIbig = new DeductionData("Pag-IBIG", pagIbig);
        result.tax = new DeductionData("Withholding Tax", tax);
        result.totalDeductions = totalDeductions;
        result.totalBenefits = totalBenefits;
        result.netPay = netPay;
        return result;
    }

    public static PayrollResult computeSemiMonthlyPayrollFromAttendance(EmployeeData employee, List<AttendanceData> recordsForHalfMonth) {
        double[] hours = AttendanceOperations.sumHours(recordsForHalfMonth);
        double regularHours = hours[0];
        double overtimeHours = hours[1];
        double totalHours = hours[2];

        // Gross Pay = hours worked in this half-month x hourly rate
        double grossPay = totalHours * employee.hourlyRate;

        // Compute full monthly deductions first
        double monthlySss = DeductionCalculator.calculateSSS(employee.basicSalary);
        double monthlyPhilHealth = DeductionCalculator.calculatePhilHealth(employee.basicSalary);
        double monthlyPagIbig = DeductionCalculator.calculatePagIbig(employee.basicSalary);
        double monthlyTax = DeductionCalculator.calculateWithholdingTax(
                employee.basicSalary, monthlySss + monthlyPhilHealth + monthlyPagIbig);

        // Display half of each deduction for semi-monthly payslip
        double sss = monthlySss / 2.0;
        double philHealth = monthlyPhilHealth / 2.0;
        double pagIbig = monthlyPagIbig / 2.0;
        double tax = monthlyTax / 2.0;
        double totalDeductions = sss + philHealth + pagIbig + tax;
        double totalBenefits = BenefitsCalculator.getTotalBenefits(employee)/ 2.0;

        double netPay = grossPay - totalDeductions + totalBenefits;
        if (netPay < 0) netPay = 0;

        PayrollResult result = new PayrollResult();
        result.employeeId = employee.employeeId;
        result.employeeName = employee.firstName + " " + employee.lastName;
        result.basicPay = grossPay;
        result.regularHours = regularHours;
        result.overtimeHours = overtimeHours;
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

    public static PayrollResult computeMonthlyPayroll(EmployeeData employee, List<AttendanceData> recordsForMonth) {
        double[] hours = AttendanceOperations.sumHours(recordsForMonth);
        double regularHours = hours[0];
        double overtimeHours = hours[1];
        double totalHours = hours[2];
        double grossPay = totalHours * employee.hourlyRate;

        double sss = DeductionCalculator.calculateSSS(employee.basicSalary);
        double philHealth = DeductionCalculator.calculatePhilHealth(employee.basicSalary);
        double pagIbig = DeductionCalculator.calculatePagIbig(employee.basicSalary);
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
        result.regularHours = regularHours;
        result.overtimeHours = overtimeHours;
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