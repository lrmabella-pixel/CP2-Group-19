package motorph;

import java.util.List;


 // Static utility functions for querying the in-memory employee
 // list (find by ID, determine next available ID, etc.).
 
public final class EmployeeOperations {

    private EmployeeOperations() {
    }

    // Finds the next available employee ID (max existing ID + 1). 
    public static int getNextEmployeeId(List<EmployeeData> employees) {
        int max = 10000;
        for (EmployeeData e : employees) {
            if (e.employeeId > max) {
                max = e.employeeId;
            }
        }
        return employees.isEmpty() ? 10001 : max + 1;
    }

    //  Finds an employee by ID, or null if not found. 
    public static EmployeeData findById(List<EmployeeData> employees, int employeeId) {
        for (EmployeeData e : employees) {
            if (e.employeeId == employeeId) {
                return e;
            }
        }
        return null;
    }

    
     // Builds a new EmployeeData
     
    public static EmployeeData addEmployee(List<EmployeeData> employees, String firstName, String lastName,
            String birthday, String address, String phone, String sss, String philHealth, String tin,
            String pagIbig, String status, String position, String supervisor, double basicSalary,
            double riceSubsidy, double phoneAllowance, double clothingAllowance) {

        EmployeeData emp = new EmployeeData();
        emp.employeeId = getNextEmployeeId(employees);
        emp.firstName = firstName;
        emp.lastName = lastName;
        emp.birthday = birthday;
        emp.address = address;
        emp.phoneNumber = phone;
        emp.sssNumber = sss;
        emp.philHealthNumber = philHealth;
        emp.tinNumber = tin;
        emp.pagIbigNumber = pagIbig;
        emp.status = status;
        emp.position = position;
        emp.immediateSupervisor = supervisor;
        emp.basicSalary = basicSalary;
        emp.riceSubsidy = riceSubsidy;
        emp.phoneAllowance = phoneAllowance;
        emp.clothingAllowance = clothingAllowance;
        emp.grossSemiMonthlyRate = basicSalary / 2.0;
        emp.hourlyRate = basicSalary / 160.0;

        employees.add(emp);
        return emp;
    }

    
    public static boolean updateField(List<EmployeeData> employees, int employeeId, String fieldName, Object newValue) {
        EmployeeData emp = findById(employees, employeeId);
        if (emp == null) {
            return false;
        }
        switch (fieldName) {
            case "firstName": emp.firstName = (String) newValue; break;
            case "lastName": emp.lastName = (String) newValue; break;
            case "birthday": emp.birthday = (String) newValue; break;
            case "address": emp.address = (String) newValue; break;
            case "phoneNumber": emp.phoneNumber = (String) newValue; break;
            case "sssNumber": emp.sssNumber = (String) newValue; break;
            case "philHealthNumber": emp.philHealthNumber = (String) newValue; break;
            case "tinNumber": emp.tinNumber = (String) newValue; break;
            case "pagIbigNumber": emp.pagIbigNumber = (String) newValue; break;
            case "status": emp.status = (String) newValue; break;
            case "position": emp.position = (String) newValue; break;
            case "immediateSupervisor": emp.immediateSupervisor = (String) newValue; break;
            case "basicSalary":
                double salary = (Double) newValue;
                emp.basicSalary = salary;
                emp.grossSemiMonthlyRate = salary / 2.0;
                emp.hourlyRate = salary / 160.0;
                break;
            case "riceSubsidy": emp.riceSubsidy = (Double) newValue; break;
            case "phoneAllowance": emp.phoneAllowance = (Double) newValue; break;
            case "clothingAllowance": emp.clothingAllowance = (Double) newValue; break;
            case "hourlyRate": emp.hourlyRate = (Double) newValue; break;
            default: return false;
        }
        return true;
    }

    // Removes the employee with the matching ID. Returns true if removed. 
    public static boolean deleteEmployee(List<EmployeeData> employees, int employeeId) {
        return employees.removeIf(e -> e.employeeId == employeeId);
    }

    // Returns the list of editable field names (excludes ID and gross semi-monthly rate).
    public static String[] getEditableFieldNames() {
        return new String[]{
            "firstName", "lastName", "birthday", "address", "phoneNumber",
            "sssNumber", "philHealthNumber", "tinNumber", "pagIbigNumber",
            "status", "position", "immediateSupervisor", "basicSalary",
            "riceSubsidy", "phoneAllowance", "clothingAllowance", "hourlyRate"
        };
    }

    //  Human-readable labels for the editable fields, in the same order. 
    public static String[] getEditableFieldLabels() {
        return new String[]{
            "First Name", "Last Name", "Birthday (MM/dd/yyyy)", "Address", "Phone Number",
            "SSS Number", "PhilHealth Number", "TIN Number", "Pag-IBIG Number",
            "Status", "Position", "Immediate Supervisor", "Basic Salary",
            "Rice Subsidy", "Phone Allowance", "Clothing Allowance", "Hourly Rate"
        };
    }

    //  True if the named field expects a numeric (double) value.
    public static boolean isNumericField(String fieldName) {
        return fieldName.equals("basicSalary") || fieldName.equals("riceSubsidy")
                || fieldName.equals("phoneAllowance") || fieldName.equals("clothingAllowance")
                || fieldName.equals("hourlyRate");
    }
}