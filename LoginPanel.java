package motorph;

import javax.swing.*;
import java.awt.*;
import java.util.List;

// Role Based Access 
 // Never instantiated. showLoginDialog() displays a login dialog with:
 // Admin/HR/Payroll/Finance login (username + password)
 // Employee Self-Service button (Employee ID only)
 
public final class LoginPanel {

    private LoginPanel() {
    }

    public static UserData showLoginDialog(List<UserData> users,
            List<EmployeeData> employees,
            List<AttendanceData> attendanceRecords) {

        JDialog dialog = new JDialog();
        dialog.setTitle("MotorPH Employee App  Login");
        dialog.setSize(400, 340);
        dialog.setLocationRelativeTo(null);
        dialog.setModal(true);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dialog.setLayout(new BorderLayout());

        //  Header 
        JPanel header = new JPanel(new GridLayout(2, 1));
        header.setBackground(new Color(26, 26, 64));
        header.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
        JLabel title = new JLabel("WELCOME TO MOTORPH");
        title.setForeground(Color.WHITE);
        title.setFont(new Font("SansSerif", Font.BOLD, 16));
        JLabel subtitle = new JLabel("Please log in to continue");
        subtitle.setForeground(new Color(180, 180, 200));
        subtitle.setFont(new Font("SansSerif", Font.PLAIN, 12));
        header.add(title);
        header.add(subtitle);

        //  Form 
        JPanel form = new JPanel(new GridLayout(0, 2, 8, 10));
        form.setBorder(BorderFactory.createEmptyBorder(20, 24, 10, 24));

        JTextField usernameField = new JTextField();
        JPasswordField passwordField = new JPasswordField();

        form.add(new JLabel("Username:"));
        form.add(usernameField);
        form.add(new JLabel("Password:"));
        form.add(passwordField);

        //  Buttons
        JButton loginBtn = new JButton("Login");
        JButton selfServiceBtn = new JButton("Employee Self-Service Portal ");
        loginBtn.setBackground(new Color(26, 26, 64));
        loginBtn.setForeground(Color.WHITE);
        selfServiceBtn.setBackground(new Color(45, 100, 160));
        selfServiceBtn.setForeground(Color.WHITE);

        JPanel btnPanel = new JPanel(new GridLayout(2, 1, 0, 6));
        btnPanel.setBorder(BorderFactory.createEmptyBorder(0, 24, 16, 24));
        btnPanel.add(loginBtn);
        btnPanel.add(selfServiceBtn);

        JLabel errorLabel = new JLabel(" ");
        errorLabel.setForeground(Color.RED);
        errorLabel.setHorizontalAlignment(SwingConstants.CENTER);

        JPanel south = new JPanel(new BorderLayout());
        south.add(errorLabel, BorderLayout.NORTH);
        south.add(btnPanel, BorderLayout.CENTER);

        dialog.add(header, BorderLayout.NORTH);
        dialog.add(form, BorderLayout.CENTER);
        dialog.add(south, BorderLayout.SOUTH);

        final UserData[] result = {null};

        // Admin/HR/Payroll/Finance login 
        loginBtn.addActionListener(e -> {
            String username = usernameField.getText().trim();
            String password = new String(passwordField.getPassword()).trim();

            if (username.isEmpty() || password.isEmpty()) {
                errorLabel.setText("Please enter username and password.");
                return;
            }

            UserData user = UserFileIO.authenticate(users, username, password);
            if (user == null) {
                errorLabel.setText("Invalid username or password.");
                passwordField.setText("");
            } else {
                result[0] = user;
                AuditLogger.log(user.username, user.role,
                        "LOGIN",
                        user.username + " (" + user.role + ") logged in successfully.");
                dialog.dispose();
            }
        });

        //  Employee Self-Service login 
        selfServiceBtn.addActionListener(e -> {
            String idInput = JOptionPane.showInputDialog(dialog,
                    "Enter your Employee ID:",
                    "Employee Self-Service Portal ",
                    JOptionPane.PLAIN_MESSAGE);

            if (idInput == null || idInput.trim().isEmpty()) return;

            try {
                int empId = Integer.parseInt(idInput.trim());
                EmployeeData emp = EmployeeOperations.findById(employees, empId);
                if (emp == null) {
                    JOptionPane.showMessageDialog(dialog,
                            "Employee ID not found. Please try again.",
                            "Not Found", JOptionPane.ERROR_MESSAGE);
                } else {
                    AuditLogger.log("EMP#" + empId, "EMPLOYEE",
                            "SELF-SERVICE LOGIN",
                            emp.firstName + " " + emp.lastName
                            + " accessed self-service portal.");
                    dialog.dispose();
                    EmployeeSelfServiceFrame portal =
                            new EmployeeSelfServiceFrame(emp, attendanceRecords,
                                    () -> Main.showLoginThenMain(
                                            users, employees, attendanceRecords));
                    portal.setVisible(true);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog,
                        "Please enter a valid numeric Employee ID.",
                        "Invalid ID", JOptionPane.ERROR_MESSAGE);
            }
        });

        // Allow Enter key to trigger login
        passwordField.addActionListener(e -> loginBtn.doClick());

        dialog.setVisible(true);
        return result[0];
    }
}