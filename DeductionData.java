package motorph;


 // Plain data holder for one named deduction (e.g. "SSS", "Tax")
 // and its computed amount. Used to list deduction breakdowns.
 
public class DeductionData {
    public String name;
    public double amount;

    public DeductionData() {
    }

    public DeductionData(String name, double amount) {
        this.name = name;
        this.amount = amount;
    }
}
