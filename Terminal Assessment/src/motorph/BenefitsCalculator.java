package motorph;


public final class BenefitsCalculator {

    private BenefitsCalculator() {
    }

    public static double getTotalBenefits(EmployeeData employee) {
        return employee.riceSubsidy + employee.phoneAllowance + employee.clothingAllowance;
    }
}
