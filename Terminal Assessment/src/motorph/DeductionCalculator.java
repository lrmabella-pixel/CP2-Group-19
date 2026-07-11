package motorph;


public final class DeductionCalculator {

    private DeductionCalculator() {
    }

   public static double calculateSSS(double basicSalary) {
    double[][] table = {
        {0, 3249.99, 135.00},
        {3250, 3749.99, 157.50},
        {3750, 4249.99, 180.00},
        {4250, 4749.99, 202.50},
        {4750, 5249.99, 225.00},
        {5250, 5749.99, 247.50},
        {5750, 6249.99, 270.00},
        {6250, 6749.99, 292.50},
        {6750, 7249.99, 315.00},
        {7250, 7749.99, 337.50},
        {7750, 8249.99, 360.00},
        {8250, 8749.99, 382.50},
        {8750, 9249.99, 405.00},
        {9250, 9749.99, 427.50},
        {9750, 10249.99, 450.00},
        {10250, 10749.99, 472.50},
        {10750, 11249.99, 495.00},
        {11250, 11749.99, 517.50},
        {11750, 12249.99, 540.00},
        {12250, 12749.99, 562.50},
        {12750, 13249.99, 585.00},
        {13250, 13749.99, 607.50},
        {13750, 14249.99, 630.00},
        {14250, 14749.99, 652.50},
        {14750, 15249.99, 675.00},
        {15250, 15749.99, 697.50},
        {15750, 16249.99, 720.00},
        {16250, 16749.99, 742.50},
        {16750, 17249.99, 765.00},
        {17250, 17749.99, 787.50},
        {17750, 18249.99, 810.00},
        {18250, 18749.99, 832.50},
        {18750, 19249.99, 855.00},
        {19250, 19749.99, 877.50},
        {19750, 20249.99, 900.00},
        {20250, 20749.99, 922.50},
        {20750, 21249.99, 945.00},
        {21250, 21749.99, 967.50},
        {21750, 22249.99, 990.00},
        {22250, 22749.99, 1012.50},
        {22750, 23249.99, 1035.00},
        {23250, 23749.99, 1057.50},
        {23750, 24249.99, 1080.00},
        {24250, 24749.99, 1102.50},
        {24750, 25249.99, 1125.00},
        {25250, 25749.99, 1147.50},
        {25750, 26249.99, 1170.00},
        {26250, 26749.99, 1192.50},
        {26750, 27249.99, 1215.00},
        {27250, 27749.99, 1237.50},
        {27750, 28249.99, 1260.00},
        {28250, 28749.99, 1282.50},
        {28750, 29249.99, 1305.00},
        {29250, 29749.99, 1327.50},
        {29750, 30249.99, 1350.00},
        {30250, 30749.99, 1372.50},
        {30750, 31249.99, 1395.00},
        {31250, 31749.99, 1417.50},
        {31750, 32249.99, 1440.00},
        {32250, 32749.99, 1462.50},
        {32750, 33249.99, 1485.00},
        {33250, 33749.99, 1507.50},
        {33750, 34249.99, 1530.00},
        {34250, 34749.99, 1552.50},
        {34750, 35249.99, 1575.00},
        {35250, 35749.99, 1597.50},
        {35750, 36249.99, 1620.00},
        {36250, 36749.99, 1642.50},
        {36750, 37249.99, 1665.00},
        {37250, 37749.99, 1687.50},
        {37750, Double.MAX_VALUE, 1750.00}
    };
    for (double[] range : table) {
        if (basicSalary >= range[0] && basicSalary <= range[1]) {
            return range[2];
        }
    }
    return 0.0;
}
 

    public static double calculatePhilHealth(double basicSalary) {
    // 5% total premium, employee pays half = 2.5% of basic salary
    double premium = basicSalary * 0.05 / 2;
    double max = 2250.00;
    double min = 500.00;
    if (premium > max) return max;
    if (premium < min) return min;
    return premium;
}

   public static double calculatePagIbig(double basicSalary) {
    // Flat ₱200 employee contribution per month
    return 200.00;
}

    public static double calculateWithholdingTax(double basicSalary, double deductionsSoFar) {
        double taxable = basicSalary - deductionsSoFar;
        if (taxable <= 20832) return 0.0;
        if (taxable <= 33333) return (taxable - 20833) * 0.20;
        if (taxable <= 66667) return (taxable - 33333) * 0.25 + 2500;
        if (taxable <= 166667) return (taxable - 66667) * 0.30 + 10833.33;
        if (taxable <= 666667) return (taxable - 166667) * 0.32 + 40833.33;
        return (taxable - 666667) * 0.35 + 200833.33;
    }

    public static double getTotalDeductions(double basicSalary) {
        double sss = calculateSSS(basicSalary);
        double philHealth = calculatePhilHealth(basicSalary);
        double pagIbig = calculatePagIbig(basicSalary);
        double tax = calculateWithholdingTax(basicSalary, sss + philHealth + pagIbig);
        return sss + philHealth + pagIbig + tax;
    }
}