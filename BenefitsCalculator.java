package motorph;


 // Static function that sums an employee's non-taxable monthly
 // benefits (rice subsidy + phone allowance + clothing allowance).
 
public final class BenefitsCalculator {

    private BenefitsCalculator() {
    }

    public static double getTotalBenefits(EmployeeData employee) {
        return employee.riceSubsidy + employee.phoneAllowance + employee.clothingAllowance;
    }
}
