package motorph;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;


 // Never instantiated. build() shows the audit trail table 
 // all logged actions with timestamp, user, role, action, details.
 // Visible to ADMIN and FINANCE roles only.
 
public final class AuditTrailPanel {

    private AuditTrailPanel() {
    }

    private static final String[] COLUMNS = {
        "Timestamp", "Username", "Role", "Action", "Details"
    };

    public static JPanel build() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        panel.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        JPanel topRow = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton refreshBtn = new JButton("Refresh");
        JButton exportBtn = new JButton("Export to CSV");
        topRow.add(refreshBtn);
        topRow.add(exportBtn);

        DefaultTableModel tableModel = new DefaultTableModel(COLUMNS, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };
        JTable table = new JTable(tableModel);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        table.getColumnModel().getColumn(0).setPreferredWidth(150);
        table.getColumnModel().getColumn(1).setPreferredWidth(100);
        table.getColumnModel().getColumn(2).setPreferredWidth(80);
        table.getColumnModel().getColumn(3).setPreferredWidth(150);
        table.getColumnModel().getColumn(4).setPreferredWidth(400);

        JLabel countLabel = new JLabel(" ");

        // Load logs into table
        Runnable loadLogs = () -> {
            tableModel.setRowCount(0);
            List<AuditLog> logs = AuditLogger.getLogs();
            for (AuditLog log : logs) {
                tableModel.addRow(new Object[]{
                    log.timestamp, log.username, log.role,
                    log.action, log.details
                });
            }
            countLabel.setText(logs.size() + " audit entries total.");
        };

        loadLogs.run();

        refreshBtn.addActionListener(e -> loadLogs.run());

        exportBtn.addActionListener(e -> {
            List<AuditLog> logs = AuditLogger.getLogs();
            if (logs.isEmpty()) {
                JOptionPane.showMessageDialog(panel,
                        "No audit entries to export.",
                        "Empty Log", JOptionPane.WARNING_MESSAGE);
                return;
            }
            try (java.io.BufferedWriter writer = new java.io.BufferedWriter(
                    new java.io.FileWriter("audit_log_export.csv"))) {
                writer.write("Timestamp,Username,Role,Action,Details\n");
                for (AuditLog log : logs) {
                    writer.write(String.format("%s,%s,%s,%s,\"%s\"%n",
                            log.timestamp, log.username, log.role,
                            log.action, log.details));
                }
                JOptionPane.showMessageDialog(panel,
                        "Audit log exported to audit_log_export.csv",
                        "Export Complete", JOptionPane.INFORMATION_MESSAGE);
            } catch (java.io.IOException ex) {
                JOptionPane.showMessageDialog(panel,
                        "Export failed: " + ex.getMessage(),
                        "Export Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        panel.add(topRow, BorderLayout.NORTH);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        panel.add(countLabel, BorderLayout.SOUTH);
        return panel;
    }
}