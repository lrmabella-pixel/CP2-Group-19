package motorph;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import javax.swing.text.DateFormatter;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


public final class AddEmployeePanel {

    private AddEmployeePanel() {
    }

    public static JPanel build(List<EmployeeData> employees, Runnable onEmployeeAdded) {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        panel.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        JPanel form = new JPanel(new GridLayout(0, 2, 8, 6));

        JTextField firstNameField = new JTextField();
        JTextField lastNameField = new JTextField();
        SpinnerDateModel dateModel = new SpinnerDateModel(
        new Date(), null, null, Calendar.DAY_OF_MONTH);
JSpinner birthdaySpinner = new JSpinner(dateModel);
JSpinner.DateEditor dateEditor = new JSpinner.DateEditor(birthdaySpinner, "MM/dd/yyyy");
DateFormatter dateFormatter = (DateFormatter) dateEditor.getTextField().getFormatter();
dateFormatter.setAllowsInvalid(false);
dateFormatter.setOverwriteMode(true);
birthdaySpinner.setEditor(dateEditor);
        JTextField addressField = new JTextField();
        JTextField phoneField = new JTextField();
        JTextField sssField = new JTextField();
        JTextField philHealthField = new JTextField();
        JTextField tinField = new JTextField();
        JTextField pagIbigField = new JTextField();
        JComboBox<String> statusBox = new JComboBox<>(AppConfig.STATUS_OPTIONS);
        JTextField positionField = new JTextField();
        JTextField supervisorField = new JTextField();
        JTextField basicSalaryField = new JTextField();
        JTextField riceSubsidyField = new JTextField("1500");
        JTextField phoneAllowanceField = new JTextField("500");
        JTextField clothingAllowanceField = new JTextField("500");

        addRow(form, "First Name *", firstNameField);
        addRow(form, "Last Name *", lastNameField);
        addRow(form, "Birthday", birthdaySpinner);
        addRow(form, "Address", addressField);
        addRow(form, "Phone Number", phoneField);
        addRow(form, "SSS Number", sssField);
        addRow(form, "PhilHealth Number", philHealthField);
        addRow(form, "TIN Number", tinField);
        addRow(form, "Pag-IBIG Number", pagIbigField);
        addRow(form, "Status", statusBox);
        addRow(form, "Position *", positionField);
        addRow(form, "Immediate Supervisor", supervisorField);
        addRow(form, "Basic Salary *", basicSalaryField);
        addRow(form, "Rice Subsidy", riceSubsidyField);
        addRow(form, "Phone Allowance", phoneAllowanceField);
        addRow(form, "Clothing Allowance", clothingAllowanceField);

        JButton addButton = new JButton("Add Employee");
        JLabel statusLabel = new JLabel(" ");

        addButton.addActionListener(e -> {
            String firstName = firstNameField.getText().trim();
            String lastName = lastNameField.getText().trim();
            String position = positionField.getText().trim();
            String basicSalaryText = basicSalaryField.getText().trim();

            if (firstName.isEmpty() || lastName.isEmpty() || position.isEmpty() || basicSalaryText.isEmpty()) {
                statusLabel.setForeground(Color.RED);
                statusLabel.setText("First Name, Last Name, Position, and Basic Salary are required.");
                return;
            }
            // Phone number validation - digits and dashes, 9 to 12 characters
String phone = phoneField.getText().trim();
if (!phone.isEmpty() && !phone.matches("[0-9\\-]{9,12}")) {
    statusLabel.setForeground(Color.RED);
    statusLabel.setText("Phone number must be 9-12 digits/dashes (e.g. 966-860-270).");
    return;
}

// SSS number validation - format: XX-XXXXXXX-X
String sss = sssField.getText().trim();
if (!sss.isEmpty() && !sss.matches("\\d{2}-\\d{7}-\\d")) {
    statusLabel.setForeground(Color.RED);
    statusLabel.setText("SSS Number format must be XX-XXXXXXX-X (e.g. 44-4506057-3).");
    return;
}

// PhilHealth validation - exactly 12 digits
String philHealth = philHealthField.getText().trim();
if (!philHealth.isEmpty() && !philHealth.matches("\\d{12}")) {
    statusLabel.setForeground(Color.RED);
    statusLabel.setText("PhilHealth Number must be exactly 12 digits (e.g. 820126853951).");
    return;
}

// TIN validation - format: XXX-XXX-XXX-XXX
String tin = tinField.getText().trim();
if (!tin.isEmpty() && !tin.matches("\\d{3}-\\d{3}-\\d{3}-\\d{3}")) {
    statusLabel.setForeground(Color.RED);
    statusLabel.setText("TIN format must be XXX-XXX-XXX-XXX (e.g. 442-605-657-000).");
    return;
}

// Pag-IBIG validation - exactly 12 digits
String pagIbig = pagIbigField.getText().trim();
if (!pagIbig.isEmpty() && !pagIbig.matches("\\d{12}")) {
    statusLabel.setForeground(Color.RED);
    statusLabel.setText("Pag-IBIG Number must be exactly 12 digits (e.g. 691295330870).");
    return;
}

            double basicSalary, riceSubsidy, phoneAllowance, clothingAllowance;
            try {
                basicSalary = Double.parseDouble(basicSalaryText);
                riceSubsidy = Double.parseDouble(riceSubsidyField.getText().trim());
                phoneAllowance = Double.parseDouble(phoneAllowanceField.getText().trim());
                clothingAllowance = Double.parseDouble(clothingAllowanceField.getText().trim());
            } catch (NumberFormatException ex) {
                statusLabel.setForeground(Color.RED);
                statusLabel.setText("Salary and allowance fields must be valid numbers.");
                return;
            }

         Date selectedDate = (Date) birthdaySpinner.getValue();
SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
String birthday = sdf.format(selectedDate);

EmployeeData newEmp = EmployeeOperations.addEmployee(
        employees, firstName, lastName, birthday,
                 addressField.getText().trim(), phone,
sss, philHealth,
tin, pagIbig,
                    (String) statusBox.getSelectedItem(), position, supervisorField.getText().trim(),
                    basicSalary, riceSubsidy, phoneAllowance, clothingAllowance);

            statusLabel.setForeground(new Color(22, 101, 52));
            statusLabel.setText("Employee #" + newEmp.employeeId + " " + newEmp.firstName + " " + newEmp.lastName + " added successfully.");

            firstNameField.setText(""); lastNameField.setText("");
birthdaySpinner.setValue(new Date());
            addressField.setText(""); phoneField.setText(""); sssField.setText("");
            philHealthField.setText(""); tinField.setText(""); pagIbigField.setText("");
            positionField.setText(""); supervisorField.setText(""); basicSalaryField.setText("");
            riceSubsidyField.setText("1500"); phoneAllowanceField.setText("500"); clothingAllowanceField.setText("500");

            if (onEmployeeAdded != null) {
                onEmployeeAdded.run();
            }
        });

        JPanel bottom = new JPanel(new BorderLayout());
        bottom.add(addButton, BorderLayout.WEST);
        bottom.add(statusLabel, BorderLayout.CENTER);

        panel.add(new JScrollPane(form), BorderLayout.CENTER);
        panel.add(bottom, BorderLayout.SOUTH);
        return panel;
    }

    private static void addRow(JPanel form, String label, JComponent field) {
        form.add(new JLabel(label));
        form.add(field);
    }
}