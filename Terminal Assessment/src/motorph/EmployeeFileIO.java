package motorph;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;


public final class EmployeeFileIO {

    private EmployeeFileIO() {
    }

   
    public static List<EmployeeData> loadEmployees(String filepath) {
        List<EmployeeData> employees = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(filepath))) {
            String line;
            boolean firstLine = true;

            while ((line = reader.readLine()) != null) {
                if (firstLine) {
                    firstLine = false;
                    continue; // skip header row
                }
                if (line.trim().isEmpty()) {
                    continue;
                }

                String[] data = splitCsvLine(line);
                if (data.length < 19) {
                    continue; // malformed row, skip it
                }

                try {
                    EmployeeData emp = new EmployeeData();
                    emp.employeeId = Integer.parseInt(data[0].trim());
                    emp.firstName = data[1].trim();
                    emp.lastName = data[2].trim();
                    emp.birthday = data[3].trim();
                    emp.address = data[4].trim();
                    emp.phoneNumber = data[5].trim();
                    emp.sssNumber = data[6].trim();
                    emp.philHealthNumber = data[7].trim();
                    emp.tinNumber = data[8].trim();
                    emp.pagIbigNumber = data[9].trim();
                    emp.status = data[10].trim();
                    emp.position = data[11].trim();
                    emp.immediateSupervisor = data[12].trim();
                    emp.basicSalary = parseMoney(data[13]);
                    emp.riceSubsidy = parseMoney(data[14]);
                    emp.phoneAllowance = parseMoney(data[15]);
                    emp.clothingAllowance = parseMoney(data[16]);
                    emp.grossSemiMonthlyRate = parseMoney(data[17]);
                    emp.hourlyRate = parseMoney(data[18]);
                    employees.add(emp);
                } catch (NumberFormatException ex) {
                    Logger.getLogger(EmployeeFileIO.class.getName())
                            .log(Level.WARNING, "Skipping invalid employee row: " + line, ex);
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(EmployeeFileIO.class.getName())
                    .log(Level.SEVERE, "Error loading employees from " + filepath, ex);
        }

        return employees;
    }

    // Writes the given employee list to a CSV file in the standard column order.
    public static void saveEmployees(String filepath, List<EmployeeData> employees) {
        try (FileWriter writer = new FileWriter(filepath)) {
            writer.write("Employee #,Last Name,First Name,Birthday,Address,Phone Number,SSS #,"
                    + "Philhealth #,TIN #,Pag-ibig #,Status,Position,Immediate Supervisor,"
                    + "Basic Salary,Rice Subsidy,Phone Allowance,Clothing Allowance,"
                    + "Gross Semi-monthly Rate,Hourly Rate\n");

            for (EmployeeData e : employees) {
                writer.write(String.format("%d,%s,%s,%s,\"%s\",%s,%s,%s,%s,%s,%s,%s,%s,%.2f,%.2f,%.2f,%.2f,%.2f,%.2f%n",
                        e.employeeId, e.lastName, e.firstName, e.birthday, e.address, e.phoneNumber,
                        e.sssNumber, e.philHealthNumber, e.tinNumber, e.pagIbigNumber, e.status,
                        e.position, e.immediateSupervisor, e.basicSalary, e.riceSubsidy,
                        e.phoneAllowance, e.clothingAllowance, e.grossSemiMonthlyRate, e.hourlyRate));
            }
        } catch (IOException ex) {
            Logger.getLogger(EmployeeFileIO.class.getName())
                    .log(Level.SEVERE, "Error saving employees to " + filepath, ex);
        }
    }

     
    public static List<AttendanceData> loadAttendance(String filepath) {
        List<AttendanceData> records = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(filepath))) {
            String line;
            boolean firstLine = true;

            while ((line = reader.readLine()) != null) {
                if (firstLine) {
                    firstLine = false;
                    continue;
                }
                if (line.trim().isEmpty()) {
                    continue;
                }

                String[] data = splitCsvLine(line);
                if (data.length < 6) {
                    continue;
                }

                try {
                    int employeeId = Integer.parseInt(data[0].trim());
                    String date = data[3].trim();
                    String clockIn = data[4].trim();
                    String clockOut = data[5].trim();
                    AttendanceData record = AttendanceOperations.buildRecord(employeeId, date, clockIn, clockOut);
                    records.add(record);
                } catch (Exception ex) {
                    Logger.getLogger(EmployeeFileIO.class.getName())
                            .log(Level.WARNING, "Skipping invalid attendance row: " + line, ex);
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(EmployeeFileIO.class.getName())
                    .log(Level.SEVERE, "Error loading attendance from " + filepath, ex);
        }

        return records;
    }

    // Appends one attendance record to a CSV file (creates the file with header if missing). */
    public static void appendAttendance(String filepath, AttendanceData record) {
        java.io.File file = new java.io.File(filepath);
        boolean needsHeader = !file.exists();

        try (FileWriter writer = new FileWriter(filepath, true)) {
            if (needsHeader) {
                writer.write("Employee #,Last Name,First Name,Date,Log In,Log Out\n");
            }
            writer.write(String.format("%d,,,%s,%s,%s%n",
                    record.employeeId, record.date, record.clockIn, record.clockOut));
        } catch (IOException ex) {
            Logger.getLogger(EmployeeFileIO.class.getName())
                    .log(Level.SEVERE, "Error appending attendance to " + filepath, ex);
        }
    }

   

    // Minimal CSV splitter that respects double-quoted fields containing commas
    private static String[] splitCsvLine(String line) {
        List<String> fields = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        boolean inQuotes = false;

        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            if (c == '"') {
                inQuotes = !inQuotes;
            } else if (c == ',' && !inQuotes) {
                fields.add(current.toString());
                current.setLength(0);
            } else {
                current.append(c);
            }
        }
        fields.add(current.toString());
        return fields.toArray(new String[0]);
    }

    //Parses a money string that may contain thousands separators, e.g. "90,000" or "535.71". */
    private static double parseMoney(String raw) {
        String cleaned = raw.replace(",", "").trim();
        if (cleaned.isEmpty()) {
            return 0.0;
        }
        return Double.parseDouble(cleaned);
    }
}
