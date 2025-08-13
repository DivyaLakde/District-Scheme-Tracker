package gui;

import db.DBConnection;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class CitizenMessagesViewer extends JFrame {

    public CitizenMessagesViewer() {
        setTitle("📨 Citizen Messages");
        setSize(800, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        // Table header font and style
        JTable table = new JTable();
        DefaultTableModel model = new DefaultTableModel(new String[]{"Your Name", "Your Message", "Officer Reply"}, 0);
        table.setModel(model);

        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 16));
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.setRowHeight(30);

        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);

        // Load messages from DB
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT sender_name, message_content, reply FROM messages";
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                String name = rs.getString("sender_name");
                String message = rs.getString("message_content");
                String reply = rs.getString("reply");

                if (reply == null || reply.trim().isEmpty()) {
                    reply = "⏳ Awaiting Officer Reply...";
                }

                model.addRow(new Object[]{name, message, reply});
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "❌ Error loading messages");
        }

        setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(CitizenMessagesViewer::new);
    }
}
