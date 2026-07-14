package motorph;

import java.util.HashSet;
import java.util.Set;


 // Static function that checks if a given date (MM/dd/yyyy) falls on a
 // Philippine Regular Holiday — which entitles employees to double pay
 // (200% of daily rate) under the Labor Code of the Philippines.
 // Only regular holidays are covered

public final class PhilippineHolidays {

    private PhilippineHolidays() {
    }

    // Fixed regular holidays (month/day format, year-independent)
    private static final Set<String> FIXED_HOLIDAYS = new HashSet<>();

   static {
    FIXED_HOLIDAYS.add("1/1");    // New Year's Day
    FIXED_HOLIDAYS.add("4/9");    // Araw ng Kagitingan
    FIXED_HOLIDAYS.add("5/1");    // Labor Day
    FIXED_HOLIDAYS.add("6/12");   // Independence Day
    FIXED_HOLIDAYS.add("11/1");   // All Saints Day
    FIXED_HOLIDAYS.add("11/30");  // Bonifacio Day
    FIXED_HOLIDAYS.add("12/25");  // Christmas Day
    FIXED_HOLIDAYS.add("12/30");  // Rizal Day
}

    
     // Returns true if the given date string (MM/dd/yyyy or M/d/yyyy)
     // falls on a Philippine Regular Holiday.
     
    public static boolean isRegularHoliday(String date) {
    if (date == null || date.trim().isEmpty()) return false;

    String[] parts = date.split("/");
    if (parts.length < 3) return false;

    // Parse removing any leading zeros
    int month = Integer.parseInt(parts[0].trim());
    int day = Integer.parseInt(parts[1].trim());
    int year = Integer.parseInt(parts[2].trim());

    // Check fixed holidays using month/day without leading zeros
    String key = month + "/" + day;
    if (FIXED_HOLIDAYS.contains(key)) {
        return true;
    }

    // National Heroes Day 
    if (month == 8) {
        return isLastMondayOfAugust(day, year);
    }

    return false;
}

    // Checks if the given day is the last Monday of August for the given year. 
    private static boolean isLastMondayOfAugust(int day, int year) {
        // Find the last Monday of August
        java.util.Calendar cal = java.util.Calendar.getInstance();
        cal.set(year, java.util.Calendar.AUGUST, 1);

        // Find the last day of August
        int lastDay = cal.getActualMaximum(java.util.Calendar.DAY_OF_MONTH);
        cal.set(java.util.Calendar.DAY_OF_MONTH, lastDay);

        // Walk backwards to find the last Monday
        while (cal.get(java.util.Calendar.DAY_OF_WEEK) != java.util.Calendar.MONDAY) {
            cal.add(java.util.Calendar.DAY_OF_MONTH, -1);
        }

        return cal.get(java.util.Calendar.DAY_OF_MONTH) == day;
    }
}
