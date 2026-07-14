package motorph;


 // Central constants holder — CSV filenames, dropdown options, month names.
 //Declared once here for reuse across modules; private constructor prevents instantiation.
 

public final class AppConfig {

    private AppConfig() {
    }

    public static final String EMPLOYEE_FILE = "employee_data.csv";
    public static final String ATTENDANCE_FILE = "attendance_record_1.csv";
    public static final String USERS_FILE = "users.csv";

    public static final String[] STATUS_OPTIONS = {"Regular", "Probationary"};

    public static final String[] MONTH_NAMES = {
        "January", "February", "March", "April", "May", "June",
        "July", "August", "September", "October", "November", "December"
    };
}