package motorph;

import javax.swing.*;
import java.awt.*;
import java.util.List;


public final class EditEmployeePanel {

    private EditEmployeePanel() {
    }

    public static JPanel build(List<EmployeeData> employees, Runnable onEmployeeUpdated) {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        panel.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        JPanel topRow = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JTextField idField = new JTextField(10);
        JButton loadButton = new JButton("Load Employee");
        topRow.add(new JLabel("Employee ID:"));
        topRow.add(idField);
        topRow.add(loadButton);

        JLabel nameLabel = new JLabel("No employee loaded.");

        JComboBox<String> fieldBox = new JComboBox<>(EmployeeOperations.getEditableFieldLabels());
        JTextField newValueField = new JTextField();
        JButton saveButton = new JButton("Save Field");
        JLabel resultLabel = new JLabel(" ");

        JPanel editRow = new JPanel(new GridLayout(0, 2, 8, 6));
        editRow.add(new JLabel("Field to Edit:"));
        editRow.add(fieldBox);
        editRow.add(new JLabel("New Value:"));
        editRow.add(newValueField);

        final int[] loadedId = {-1};

        loadButton.addActionListener(e -> {
            try {
                int id = Integer.parseInt(idField.getText().trim());
                EmployeeData emp = EmployeeOperations.findById(employees, id);
                if (emp == null) {
                    nameLabel.setText("Employee not found.");
                    loadedId[0] = -1;
                } else {
                    nameLabel.setText("Loaded: " + emp.firstName + " " + emp.lastName + " (" + emp.position + ")");
                    loadedId[0] = id;
                }
            } catch (NumberFormatException ex) {
                nameLabel.setText("Enter a valid numeric Employee ID.");
                loadedId[0] = -1;
            }
            resultLabel.setText(" ");
        });

        saveButton.addActionListener(e -> {
            if (loadedId[0] == -1) {
                resultLabel.setForeground(Color.RED);
                resultLabel.setText("Load a valid employee first.");
                return;
            }
            int selectedIndex = fieldBox.getSelectedIndex();
            String fieldName = EmployeeOperations.getEditableFieldNames()[selectedIndex];
            String label = EmployeeOperations.getEditableFieldLabels()[selectedIndex];
            String rawValue = newValueField.getText().trim();

            if (rawValue.isEmpty()) {
                resultLabel.setForeground(Color.RED);
                resultLabel.setText("New value cannot be empty.");
                return;
            }

            Object value;
            if (EmployeeOperations.isNumericField(fieldName)) {
                try {
                    value = Double.parseDouble(rawValue);
                } catch (NumberFormatException ex) {
                    resultLabel.setForeground(Color.RED);
                    resultLabel.setText("Enter a valid number for " + label + ".");
                    return;
                }
            } else {
                value = rawValue;
            }

            boolean ok = EmployeeOperations.updateField(employees, loadedId[0], fieldName, value);
            if (ok) {
                resultLabel.setForeground(new Color(22, 101, 52));
                resultLabel.setText(label + " updated successfully.");
                EmployeeData refreshed = EmployeeOperations.findById(employees, loadedId[0]);
                nameLabel.setText("Loaded: " + refreshed.firstName + " " + refreshed.lastName + " (" + refreshed.position + ")");
                if (onEmployeeUpdated != null) {
                    onEmployeeUpdated.run();
                }
            } else {
                resultLabel.setForeground(Color.RED);
                resultLabel.setText("Update failed.");
            }
            newValueField.setText("");
        });

        JPanel center = new JPanel();
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
        center.add(topRow);
        center.add(nameLabel);
        center.add(editRow);

        JPanel bottom = new JPanel(new BorderLayout());
        bottom.add(saveButton, BorderLayout.WEST);
        bottom.add(resultLabel, BorderLayout.CENTER);

        panel.add(center, BorderLayout.NORTH);
        panel.add(bottom, BorderLayout.SOUTH);
        return panel;
    }
}
