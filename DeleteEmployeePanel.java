package motorph;

import javax.swing.*;
import java.awt.*;
import java.util.List;


 // Build Delete Employee tab pin GUI 
 // Never instantiated. build() wires the "Delete Employee" tab 
 // finds an employee by ID, confirms with the user, then removes
 // the record and triggers the onEmployeeDeleted callback (saves
 // to CSV + logs to AuditLogger).
 
public final class DeleteEmployeePanel {

    private DeleteEmployeePanel() {
    }

    public static JPanel build(JFrame owner, List<EmployeeData> employees, Runnable onEmployeeDeleted) {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        panel.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        JPanel topRow = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JTextField idField = new JTextField(10);
        JButton findButton = new JButton("Find Employee");
        JButton deleteButton = new JButton("Delete Employee");
        deleteButton.setEnabled(false);
        deleteButton.setBackground(new Color(220, 38, 38));
        deleteButton.setForeground(Color.WHITE);

        topRow.add(new JLabel("Employee ID:"));
        topRow.add(idField);
        topRow.add(findButton);

        JTextArea detailsArea = new JTextArea(6, 40);
        detailsArea.setEditable(false);
        JLabel resultLabel = new JLabel(" ");

        final int[] foundId = {-1};

        findButton.addActionListener(e -> {
            try {
                int id = Integer.parseInt(idField.getText().trim());
                EmployeeData emp = EmployeeOperations.findById(employees, id);
                if (emp == null) {
                    detailsArea.setText("Employee not found.");
                    deleteButton.setEnabled(false);
                    foundId[0] = -1;
                } else {
                    detailsArea.setText(
                            "Name: " + emp.firstName + " " + emp.lastName + "\n"
                            + "Position: " + emp.position + "\n"
                            + "Status: " + emp.status + "\n"
                            + "Basic Salary: " + String.format("₱%,.2f", emp.basicSalary)
                    );
                    deleteButton.setEnabled(true);
                    foundId[0] = id;
                }
            } catch (NumberFormatException ex) {
                detailsArea.setText("Enter a valid numeric Employee ID.");
                deleteButton.setEnabled(false);
                foundId[0] = -1;
            }
            resultLabel.setText(" ");
        });

        deleteButton.addActionListener(e -> {
            if (foundId[0] == -1) {
                return;
            }
            int confirm = JOptionPane.showConfirmDialog(owner,
                    "This will permanently remove employee #" + foundId[0] + ". Continue?",
                    "Confirm Delete", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if (confirm == JOptionPane.YES_OPTION) {
                boolean removed = EmployeeOperations.deleteEmployee(employees, foundId[0]);
                resultLabel.setForeground(removed ? new Color(22, 101, 52) : Color.RED);
                resultLabel.setText(removed ? "Employee deleted successfully." : "Delete failed.");
                detailsArea.setText("");
                deleteButton.setEnabled(false);
                foundId[0] = -1;
                if (removed && onEmployeeDeleted != null) {
                    onEmployeeDeleted.run();
                }
            }
        });

        JPanel center = new JPanel();
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
        center.add(topRow);
        center.add(new JScrollPane(detailsArea));

        JPanel bottom = new JPanel(new BorderLayout());
        bottom.add(deleteButton, BorderLayout.WEST);
        bottom.add(resultLabel, BorderLayout.CENTER);

        panel.add(center, BorderLayout.NORTH);
        panel.add(bottom, BorderLayout.SOUTH);
        return panel;
    }
}