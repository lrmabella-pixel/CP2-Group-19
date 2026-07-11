package motorph;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;


public final class ViewEmployeesPanel {

    private ViewEmployeesPanel() {
    }

    private static final String[] COLUMNS = {
        "EMP #", "Name", "Position", "Status", "Basic Salary", "Hourly Rate"
    };

    /** Builds the complete "View Employees" tab panel. */
    public static JPanel build(List<EmployeeData> employees) {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        panel.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        JPanel topBar = new JPanel(new BorderLayout(8, 0));
        JTextField searchField = new JTextField();
        JButton searchButton = new JButton("Search");
        JButton refreshButton = new JButton("Show All");
        topBar.add(new JLabel("Search (ID / name / position): "), BorderLayout.WEST);
        topBar.add(searchField, BorderLayout.CENTER);

        JPanel searchButtons = new JPanel();
        searchButtons.add(searchButton);
        searchButtons.add(refreshButton);
        topBar.add(searchButtons, BorderLayout.EAST);

        DefaultTableModel tableModel = new DefaultTableModel(COLUMNS, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };
        JTable table = new JTable(tableModel);
        fillTable(tableModel, employees);

        JLabel countLabel = new JLabel(employees.size() + " employees loaded.");

        searchButton.addActionListener(e -> {
            String query = searchField.getText().trim().toLowerCase();
            List<EmployeeData> filtered = filterEmployees(employees, query);
            fillTable(tableModel, filtered);
            countLabel.setText(filtered.size() + " of " + employees.size() + " employees shown.");
        });
        refreshButton.addActionListener(e -> {
            searchField.setText("");
            fillTable(tableModel, employees);
            countLabel.setText(employees.size() + " employees loaded.");
        });

        panel.add(topBar, BorderLayout.NORTH);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        panel.add(countLabel, BorderLayout.SOUTH);

        return panel;
    }

    // Pure function: returns a new filtered list, does not mutate the input. */
    public static List<EmployeeData> filterEmployees(List<EmployeeData> employees, String query) {
        List<EmployeeData> result = new ArrayList<>();
        for (EmployeeData e : employees) {
            String haystack = (e.employeeId + " " + e.firstName + " " + e.lastName + " " + e.position).toLowerCase();
            if (haystack.contains(query)) {
                result.add(e);
            }
        }
        return result;
    }

    /** Rebuilds the table rows from a given employee list. */
    public static void fillTable(DefaultTableModel tableModel, List<EmployeeData> employees) {
        tableModel.setRowCount(0);
        for (EmployeeData e : employees) {
            tableModel.addRow(new Object[]{
                e.employeeId,
                e.firstName + " " + e.lastName,
                e.position,
                e.status,
                String.format("₱%,.2f", e.basicSalary),
                String.format("₱%,.2f", e.hourlyRate)
            });
        }
    }
}