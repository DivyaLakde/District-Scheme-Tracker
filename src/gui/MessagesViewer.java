package gui;

import db.DBConnection;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class MessagesViewer extends JFrame {
    private JTable table;
    private DefaultTableModel model;

    public MessagesViewer() {
        setTitle("Citizen Messages");
        setSize(800, 400);
        setLocationRelativeTo(null);

        model = new DefaultTableModel(new String[]{"Message ID", "Name", "Message", "Reply", "Status"}, 0);
        table = new JTable(model);
        table.setRowHeight(30);
        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);

        // 🟦 Load messages from DB
        loadMessages();

        // ✅ Reply button
        JButton replyButton = new JButton("Reply to Selected");
        replyButton.setFont(new Font("Segoe UI", Font.BOLD, 16));
        replyButton.setBackground(new Color(33, 150, 243));
        replyButton.setForeground(Color.WHITE);
        replyButton.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row != -1) {
                String reply = JOptionPane.showInputDialog(this, "Enter reply to citizen:");
                if (reply != null && !reply.trim().isEmpty()) {
                    int messageId = Integer.parseInt(model.getValueAt(row, 0).toString());
                    try (Connection conn = DBConnection.getConnection()) {
                        PreparedStatement ps = conn.prepareStatement(
                                "UPDATE messages SET reply = ?, status = 'read' WHERE message_id = ?"
                        );
                        ps.setString(1, reply);
                        ps.setInt(2, messageId);
                        ps.executeUpdate();
                        model.setValueAt(reply, row, 3); // update reply column
                        model.setValueAt("read", row, 4); // update status
                        JOptionPane.showMessageDialog(this, "Reply sent.");
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        JOptionPane.showMessageDialog(this, "Error sending reply.");
                    }
                }
            } else {
                JOptionPane.showMessageDialog(this, "Please select a message first.");
            }
        });

        // 🔽 Button panel for both buttons
        JButton deleteButton = new JButton("Delete Selected");
        deleteButton.setFont(new Font("Segoe UI", Font.BOLD, 16));
        deleteButton.setBackground(new Color(244, 67, 54)); // Red
        deleteButton.setForeground(Color.WHITE);
        deleteButton.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row != -1) {
                int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this message?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    int messageId = Integer.parseInt(model.getValueAt(row, 0).toString());
                    try (Connection conn = DBConnection.getConnection()) {
                        PreparedStatement ps = conn.prepareStatement(
                                "DELETE FROM messages WHERE message_id = ?"
                        );
                        ps.setInt(1, messageId);
                        ps.executeUpdate();
                        model.removeRow(row);
                        JOptionPane.showMessageDialog(this, "Message deleted.");
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        JOptionPane.showMessageDialog(this, "Error deleting message.");
                    }
                }
            } else {
                JOptionPane.showMessageDialog(this, "Please select a message to delete.");
            }
        });

// ✅ Move your existing reply button above this block if needed
// Already in your code: replyButton

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(1, 2));
        buttonPanel.add(replyButton);
        buttonPanel.add(deleteButton);
        add(buttonPanel, BorderLayout.SOUTH);

        setVisible(true);
    }

    private void loadMessages() {
        try (Connection conn = DBConnection.getConnection()) {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT message_id, name, message_content, reply, status FROM messages");

            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getInt("message_id"),
                        rs.getString("name"),
                        rs.getString("message_content"),
                        rs.getString("reply"),
                        rs.getString("status")
                });

                // 🔹 Mark unread messages as 'read'
                if ("unread".equalsIgnoreCase(rs.getString("status"))) {
                    PreparedStatement ps = conn.prepareStatement(
                            "UPDATE messages SET status = 'read' WHERE message_id = ?"
                    );
                    ps.setInt(1, rs.getInt("message_id"));
                    ps.executeUpdate();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
