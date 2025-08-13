package gui;

import db.DBConnection;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class OfficerDashboard extends JFrame {
    private JTable table;
    private DefaultTableModel tableModel;

    public OfficerDashboard() {
        setTitle("District Officer Dashboard");
        setSize(1000, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setLocationRelativeTo(null);

        // Statistic cards panel
        JPanel statsPanel = new JPanel(new GridLayout(1, 4, 10, 10));
        statsPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        statsPanel.add(createStatCard("New Applications", "5", new Color(33, 150, 243)));
        statsPanel.add(createStatCard("Approved", "12", new Color(76, 175, 80)));
        statsPanel.add(createStatCard("Rejected", "3", new Color(244, 67, 54)));
        statsPanel.add(createStatCard("Pending", "8", new Color(255, 152, 0)));

        add(statsPanel, BorderLayout.NORTH);

        // Buttons panel
        int unreadCount = getUnreadMessageCount();
        String label = unreadCount > 0 ? "\uD83D\uDCE8 Messages (" + unreadCount + ")" : "\uD83D\uDCE8 Messages";

        JButton viewSchemesBtn = new JButton("\uD83D\uDCCB View Schemes");
        viewSchemesBtn.setFont(new Font("Arial", Font.PLAIN, 16));
        viewSchemesBtn.setBackground(new Color(70, 130, 180));
        viewSchemesBtn.setForeground(Color.WHITE);

        JButton msgBtn = new JButton(label);
        msgBtn.setFont(new Font("Arial", Font.PLAIN, 16));
        msgBtn.setBackground(new Color(123, 31, 162));
        msgBtn.setForeground(Color.WHITE);

        //JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        //bottomPanel.setOpaque(false);
        //bottomPanel.add(viewSchemesBtn);
        //bottomPanel.add(msgBtn);


        JButton deleteApplicantBtn = new JButton("🗑️ Delete Applicant");
        deleteApplicantBtn.setFont(new Font("Arial", Font.PLAIN, 16));
        deleteApplicantBtn.setBackground(new Color(220, 20, 60)); // Crimson
        deleteApplicantBtn.setForeground(Color.WHITE);

// Add action
        deleteApplicantBtn.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "Please select an applicant to delete.");
                return;
            }
            String appId = (String) table.getValueAt(selectedRow, 0);

            int confirm = JOptionPane.showConfirmDialog(this,
                    "Are you sure you want to delete application ID " + appId + "?",
                    "Confirm Delete", JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                try (Connection conn = DBConnection.getConnection()) {
                    PreparedStatement ps = conn.prepareStatement("DELETE FROM applications WHERE application_id = ?");
                    ps.setString(1, appId);
                    ps.executeUpdate();
                    tableModel.removeRow(selectedRow);
                    JOptionPane.showMessageDialog(this, "Applicant deleted successfully.");
                } catch (Exception ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(this, "Error deleting applicant.");
                }
            }
        });
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomPanel.setOpaque(false);
        bottomPanel.add(viewSchemesBtn);
        bottomPanel.add(msgBtn);
        bottomPanel.add(deleteApplicantBtn);

        add(bottomPanel, BorderLayout.SOUTH);

        viewSchemesBtn.addActionListener(e -> new ViewSchemes("officer"));
        msgBtn.addActionListener(e -> {
            new MessagesViewer();
            // Refresh unread count on button after closing MessagesViewer
            int refreshedCount = getUnreadMessageCount();
            msgBtn.setText(refreshedCount > 0 ? "📨 Messages (" + refreshedCount + ")" : "📨 Messages");
        });


        // Table panel
        String[] columnNames = {"Application ID", "Name", "Scheme", "Status", "Actions"};
        tableModel = new DefaultTableModel(null, columnNames);
        table = new JTable(tableModel);
        table.setRowHeight(40);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

        loadTableData();

        table.getColumn("Actions").setCellRenderer(new ButtonRenderer());
        table.getColumn("Actions").setCellEditor(new ButtonEditor(new JCheckBox()));

        add(new JScrollPane(table), BorderLayout.CENTER);
    }

    private int getUnreadMessageCount() {
        int count = 0;
        try (Connection conn = DBConnection.getConnection()) {
            PreparedStatement ps = conn.prepareStatement("SELECT COUNT(*) FROM messages WHERE status = 'unread'");
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                count = rs.getInt(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return count;
    }

    private void loadTableData() {
        try (Connection conn = DBConnection.getConnection()) {
            String query = "SELECT a.application_id, a.name, s.name AS scheme, a.status " +
                    "FROM applications a JOIN schemes s ON a.scheme_id = s.scheme_id";
            ResultSet rs = conn.createStatement().executeQuery(query);

            while (rs.next()) {
                tableModel.addRow(new Object[]{
                        rs.getString("application_id"),
                        rs.getString("name"),
                        rs.getString("scheme"),
                        rs.getString("status"),
                        ""
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private JPanel createStatCard(String title, String value, Color color) {
        JPanel panel = new JPanel();
        panel.setBackground(color);
        panel.setPreferredSize(new Dimension(120, 60));
        panel.setLayout(new BorderLayout());

        JLabel lblTitle = new JLabel(title, SwingConstants.CENTER);
        lblTitle.setForeground(Color.WHITE);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 14));

        JLabel lblValue = new JLabel(value, SwingConstants.CENTER);
        lblValue.setForeground(Color.WHITE);
        lblValue.setFont(new Font("Arial", Font.BOLD, 20));

        panel.add(lblTitle, BorderLayout.NORTH);
        panel.add(lblValue, BorderLayout.CENTER);
        return panel;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new OfficerDashboard().setVisible(true));
    }

    class ButtonRenderer extends JPanel implements TableCellRenderer {
        private JButton btnApprove = new JButton("Approve");
        private JButton btnReject = new JButton("Reject");
        private JButton btnView = new JButton("View");

        public ButtonRenderer() {
            setLayout(new FlowLayout(FlowLayout.CENTER, 5, 0));
            btnApprove.setBackground(new Color(76, 175, 80));
            btnApprove.setForeground(Color.WHITE);
            btnReject.setBackground(new Color(244, 67, 54));
            btnReject.setForeground(Color.WHITE);
            btnView.setBackground(new Color(33, 150, 243));
            btnView.setForeground(Color.WHITE);

            add(btnApprove);
            add(btnReject);
            add(btnView);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus,
                                                       int row, int column) {
            return this;
        }
    }

    class ButtonEditor extends DefaultCellEditor {
        private JPanel panel;
        private JButton btnApprove = new JButton("Approve");
        private JButton btnReject = new JButton("Reject");
        private JButton btnView = new JButton("View");

        public ButtonEditor(JCheckBox checkBox) {
            super(checkBox);
            panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
            panel.add(btnApprove);
            panel.add(btnReject);
            panel.add(btnView);

            btnApprove.addActionListener(e -> {
                int selectedRow = table.getSelectedRow();
                String appId = (String) table.getValueAt(selectedRow, 0);
                try (Connection conn = DBConnection.getConnection()) {
                    PreparedStatement ps = conn.prepareStatement("UPDATE applications SET status = 'Approved' WHERE application_id = ?");
                    ps.setString(1, appId);
                    ps.executeUpdate();
                    table.setValueAt("Approved", selectedRow, 3);
                    fireEditingStopped();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            });

            btnReject.addActionListener(e -> {
                int selectedRow = table.getSelectedRow();
                String appId = (String) table.getValueAt(selectedRow, 0);
                try (Connection conn = DBConnection.getConnection()) {
                    PreparedStatement ps = conn.prepareStatement("UPDATE applications SET status = 'Rejected' WHERE application_id = ?");
                    ps.setString(1, appId);
                    ps.executeUpdate();
                    table.setValueAt("Rejected", selectedRow, 3);
                    fireEditingStopped();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            });

            btnView.addActionListener(e -> {
                int selectedRow = table.getSelectedRow();
                String appId = (String) table.getValueAt(selectedRow, 0);
                try (Connection conn = DBConnection.getConnection()) {
                    PreparedStatement ps = conn.prepareStatement("SELECT * FROM applications WHERE application_id = ?");
                    ps.setString(1, appId);
                    ResultSet rs = ps.executeQuery();
                    if (rs.next()) {
                        String details = "Name: " + rs.getString("name") + "\n" +
                                "Aadhar No: " + rs.getString("aadhar_no") + "\n" +
                                "Age: " + rs.getInt("age") + "\n" +
                                "Gender: " + rs.getString("gender") + "\n" +
                                "Status: " + rs.getString("status");
                        JOptionPane.showMessageDialog(null, details, "Application Details", JOptionPane.INFORMATION_MESSAGE);
                    }
                    fireEditingStopped();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            });
        }

        public Component getTableCellEditorComponent(JTable table, Object value,
                                                     boolean isSelected, int row, int column) {
            return panel;
        }

        public Object getCellEditorValue() {
            return "";
        }
    }
}
