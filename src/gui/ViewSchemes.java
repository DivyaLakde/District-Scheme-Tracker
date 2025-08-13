package gui;

import db.DBConnection;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class ViewSchemes extends JFrame {
    private JTable table;
    private DefaultTableModel tableModel;

    public ViewSchemes(String role) {
        setTitle("Available Schemes");
        setSize(1000, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true); // 🔴 This line is very important


        GradientPanel mainPanel = new GradientPanel();
        mainPanel.setLayout(new BorderLayout());
        setContentPane(mainPanel);

        // 🔷 Header banner
        JLabel header = new JLabel("📋 Available Government Schemes", SwingConstants.CENTER);
        header.setFont(new Font("Segoe UI", Font.BOLD, 26));
        header.setForeground(Color.WHITE);
        header.setOpaque(true);
        header.setBackground(new Color(70, 130, 180));
        header.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        mainPanel.add(header, BorderLayout.NORTH);

        // 🟩 Table setup
        String[] columnNames = {"Scheme ID", "Name", "Description", "Category", "Eligibility", "Start Date", "End Date"};
        tableModel = new DefaultTableModel(null, columnNames);
        table = new JTable(tableModel);
        table.setRowHeight(30);
        table.setFont(new Font("Arial", Font.PLAIN, 14));
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 15));

        // Zebra striping
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                           boolean hasFocus, int row, int column) {
                Component comp = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (!isSelected) {
                    comp.setBackground(row % 2 == 0 ? Color.WHITE : new Color(230, 240, 255));
                } else {
                    comp.setBackground(new Color(186, 214, 255));
                }
                return comp;
            }
        });

        loadSchemes();
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // Buttons
        JButton addBtn = new JButton("➕ Add Scheme");
        JButton backBtn = new JButton("⬅ Back");
        JButton deleteBtn = new JButton("🗑️ Delete Scheme");

        addBtn.setFont(new Font("Arial", Font.PLAIN, 16));
        backBtn.setFont(new Font("Arial", Font.PLAIN, 16));
        deleteBtn.setFont(new Font("Arial", Font.PLAIN, 16));

        addHoverEffect(addBtn, new Color(0, 123, 255), new Color(0, 150, 255));
        addHoverEffect(backBtn, new Color(100, 100, 100), new Color(120, 120, 120));
        addHoverEffect(deleteBtn, new Color(220, 53, 69), new Color(255, 80, 100)); // Red hover effect

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setOpaque(false);
        buttonPanel.add(backBtn);

        if (role.equalsIgnoreCase("officer")) {
            buttonPanel.add(addBtn);
            buttonPanel.add(deleteBtn);
        }

        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        // Button actions
        backBtn.addActionListener(e -> dispose());
        addBtn.addActionListener(e -> {
            // Create input fields
            JTextField nameField = new JTextField();
            JTextField descField = new JTextField();
            JTextField categoryField = new JTextField();
            JTextField eligibilityField = new JTextField();
            JTextField validFromField = new JTextField("yyyy-mm-dd");
            JTextField validToField = new JTextField("yyyy-mm-dd");

            JPanel formPanel = new JPanel(new GridLayout(0, 2, 10, 10));
            formPanel.add(new JLabel("Scheme Name:"));
            formPanel.add(nameField);
            formPanel.add(new JLabel("Description:"));
            formPanel.add(descField);
            formPanel.add(new JLabel("Category:"));
            formPanel.add(categoryField);
            formPanel.add(new JLabel("Eligibility:"));
            formPanel.add(eligibilityField);
            formPanel.add(new JLabel("Valid From:"));
            formPanel.add(validFromField);
            formPanel.add(new JLabel("Valid To:"));
            formPanel.add(validToField);

            int result = JOptionPane.showConfirmDialog(this, formPanel,
                    "Add New Scheme", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

            if (result == JOptionPane.OK_OPTION) {
                try (Connection conn = DBConnection.getConnection()) {
                    String sql = "INSERT INTO schemes(name, description, category, eligibility, valid_from, valid_to) VALUES (?, ?, ?, ?, ?, ?)";
                    PreparedStatement pstmt = conn.prepareStatement(sql);
                    pstmt.setString(1, nameField.getText());
                    pstmt.setString(2, descField.getText());
                    pstmt.setString(3, categoryField.getText());
                    pstmt.setString(4, eligibilityField.getText());
                    pstmt.setString(5, validFromField.getText());
                    pstmt.setString(6, validToField.getText());
                    pstmt.executeUpdate();
                    JOptionPane.showMessageDialog(this, "✅ Scheme added successfully!");
                    refreshTable(); // Optional method to reload table
                } catch (Exception ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(this, "❌ Failed to add scheme.");
                }
            }
        });


        deleteBtn.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "Please select a scheme to delete.");
                return;
            }

            String schemeId = tableModel.getValueAt(selectedRow, 0).toString(); // Assuming scheme_id is column 0

            int confirm = JOptionPane.showConfirmDialog(this,
                    "Are you sure you want to delete Scheme ID " + schemeId + "?",
                    "Confirm Delete", JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                try (Connection conn = DBConnection.getConnection()) {
                    String sql = "DELETE FROM schemes WHERE scheme_id = ?";
                    PreparedStatement ps = conn.prepareStatement(sql);
                    ps.setString(1, schemeId);
                    int rowsAffected = ps.executeUpdate();

                    if (rowsAffected > 0) {
                        tableModel.removeRow(selectedRow); // Remove from UI
                        JOptionPane.showMessageDialog(this, "Scheme deleted successfully.");
                    } else {
                        JOptionPane.showMessageDialog(this, "Deletion failed.");
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(this, "Error while deleting.");
                }
            }
        });


    }



    private void loadSchemes() {
        try (Connection conn = DBConnection.getConnection()) {
            String query = "SELECT * FROM schemes";
            ResultSet rs = conn.createStatement().executeQuery(query);

            while (rs.next()) {
                tableModel.addRow(new Object[]{
                        rs.getString("scheme_id"),
                        rs.getString("name"),
                        rs.getString("description"),
                        rs.getString("category"),
                        rs.getString("eligibility"),
                        rs.getString("valid_from"),
                        rs.getString("valid_to")
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void addHoverEffect(JButton button, Color normal, Color hover) {
        button.setBackground(normal);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                button.setBackground(hover);
            }

            public void mouseExited(MouseEvent e) {
                button.setBackground(normal);
            }
        });
    }

    // Gradient background panel
    class GradientPanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            int w = getWidth();
            int h = getHeight();
            Color color1 = new Color(200, 220, 255);
            Color color2 = Color.WHITE;
            GradientPaint gp = new GradientPaint(0, 0, color1, 0, h, color2);
            g2d.setPaint(gp);
            g2d.fillRect(0, 0, w, h);
        }
    }

    private void refreshTable() {
        tableModel.setRowCount(0); // Clear existing data
        loadSchemes(); // Reload from DB
    }


    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ViewSchemes("officer").setVisible(true));
    }
}
