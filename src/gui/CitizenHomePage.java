package gui;

import db.DBConnection;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class CitizenHomePage extends JFrame {

    private String username;
    public CitizenHomePage(String username) {
        setTitle("Welcome, " + username);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setLocationRelativeTo(null);

        JPanel gradientBackground = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                int width = getWidth();
                int height = getHeight();
                Color color1 = new Color(255, 165, 0); // orange-like
                Color color2 = new Color(255, 40, 71); // pink-like
                GradientPaint gp = new GradientPaint(0, 0, color1, width, height, color2);
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, width, height);
            }
        };
        gradientBackground.setLayout(new BorderLayout());
        setContentPane(gradientBackground);


        JPanel contentPanel = new JPanel(new GridLayout(6, 1, 10, 10));
        contentPanel.setOpaque(false);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(150, 500, 150, 500));

        JLabel welcomeLabel = new JLabel("🎉 Welcome, " + username + "!");
        welcomeLabel.setHorizontalAlignment(SwingConstants.CENTER);
        welcomeLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        welcomeLabel.setForeground(Color.WHITE);

        JButton viewSchemes = new JButton(" View Schemes");
        JButton checkStatus = new JButton(" Check Application Status");
        JButton applyBtn = new JButton(" Apply for Scheme");
        JButton helpBtn = new JButton(" Help");
        JButton viewRepliesBtn = new JButton(" View Officer Replies");
        JButton exitBtn = new JButton(" Exit");

        JButton[] buttons = {viewSchemes, checkStatus, applyBtn, helpBtn, viewRepliesBtn, exitBtn};
        for (JButton btn : buttons) {
            btn.setFont(new Font("Segoe UI", Font.BOLD, 16));
            btn.setFocusPainted(false);
            btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
            contentPanel.add(btn);
        }

        viewSchemes.addActionListener(e -> new ViewSchemes("citizen"));
        checkStatus.addActionListener(e -> new CheckApplicationStatus());
        applyBtn.addActionListener(e -> new ApplyScheme());
        helpBtn.addActionListener(this::openHelpDialog);
        viewRepliesBtn.addActionListener(e -> showRepliesDialog(username));
        exitBtn.addActionListener(e -> System.exit(0));

        gradientBackground.add(welcomeLabel, BorderLayout.NORTH);
        gradientBackground.add(contentPanel, BorderLayout.CENTER);


        setVisible(true);
    }

    private void openHelpDialog(ActionEvent e) {
        JDialog dialog = new JDialog(this, "📩 Help / Send Message", true);
        dialog.setSize(600, 400);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout(10, 10));

        // Main panel
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
        panel.setBackground(new Color(100, 100, 160)); // Soft background

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;

        JLabel nameLabel = new JLabel("Your Name:");
        nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(nameLabel, gbc);

        JTextField nameField = new JTextField(username);
        nameField.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        gbc.gridx = 1;
        panel.add(nameField, gbc);

        JLabel messageLabel = new JLabel("Message:");
        messageLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(messageLabel, gbc);

        JTextArea messageArea = new JTextArea(6, 20);
        messageArea.setLineWrap(true);
        messageArea.setWrapStyleWord(true);
        messageArea.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        JScrollPane messageScroll = new JScrollPane(messageArea);
        gbc.gridx = 1;
        gbc.gridy = 1;
        panel.add(messageScroll, gbc);

        // Button panel
        JPanel buttonPanel = new JPanel();
        JButton sendButton = new JButton("Send");
        JButton cancelButton = new JButton("Cancel");

        sendButton.setFont(new Font("Segoe UI", Font.BOLD, 16));
        cancelButton.setFont(new Font("Segoe UI", Font.BOLD, 16));

        sendButton.setBackground(new Color(0, 123, 255));
        sendButton.setForeground(Color.WHITE);
        cancelButton.setBackground(Color.GRAY);
        cancelButton.setForeground(Color.WHITE);

        buttonPanel.add(sendButton);
        buttonPanel.add(cancelButton);

        dialog.add(panel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);

        // Action Listeners
        sendButton.addActionListener(ae -> {
            String name = nameField.getText().trim();
            String message = messageArea.getText().trim();
            if (!name.isEmpty() && !message.isEmpty()) {

                try (Connection conn = DBConnection.getConnection()) {
                    PreparedStatement ps = conn.prepareStatement(
                            "INSERT INTO messages(name, message_content) VALUES (?, ?)"
                    );
                    ps.setString(1, name);
                    ps.setString(2, message);
                    ps.executeUpdate();
                    JOptionPane.showMessageDialog(dialog, "✅ Message sent successfully!");
                    dialog.dispose();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(dialog, "❌ Error sending message.");
                    ex.printStackTrace();
                }
            } else {
                JOptionPane.showMessageDialog(dialog, "❗ Please fill in both fields.");
            }
        });

        cancelButton.addActionListener(ae -> dialog.dispose());

        dialog.setVisible(true);
    }

    private void showRepliesDialog(String username) {
        JDialog dialog = new JDialog(this, "📨 Officer Replies", true);
        dialog.setSize(700, 400);  // Half screen size approx
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout(10, 10));

        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        panel.setBackground(new Color(112, 128, 144)); // Dark Blue background

        JTextArea replyArea = new JTextArea();
        replyArea.setEditable(false);
        replyArea.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        replyArea.setForeground(Color.WHITE);
        replyArea.setBackground(new Color(25, 25, 25)); // Darker blue inside
        replyArea.setMargin(new Insets(10, 10, 10, 10));

        try (Connection conn = DBConnection.getConnection()) {
            PreparedStatement ps = conn.prepareStatement(
                    "SELECT message_content, reply FROM messages WHERE name = ? AND reply IS NOT NULL"
            );

            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();

            StringBuilder sb = new StringBuilder();
            while (rs.next()) {
                sb.append("📝 Your Message: ").append(rs.getString("message_content")).append("\n");
                sb.append("📨 Officer Reply: ").append(rs.getString("reply")).append("\n\n");
            }

            if (sb.length() == 0) {
                replyArea.setText("❌ No replies from officer yet.");
            } else {
                replyArea.setText(sb.toString());
            }

        } catch (Exception ex) {
            replyArea.setText("❌ Error loading replies.");
            ex.printStackTrace();
        }

        JScrollPane scrollPane = new JScrollPane(replyArea);
        panel.add(scrollPane, BorderLayout.CENTER);

        JButton closeBtn = new JButton("Close");
        closeBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        closeBtn.setBackground(Color.DARK_GRAY);
        closeBtn.setForeground(Color.WHITE);
        closeBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        closeBtn.addActionListener(e -> dialog.dispose());

        JPanel btnPanel = new JPanel();
        btnPanel.setBackground(new Color(60, 80, 120));
        btnPanel.add(closeBtn);

        dialog.add(panel, BorderLayout.CENTER);
        dialog.add(btnPanel, BorderLayout.SOUTH);

        dialog.setVisible(true);
    }


    public static void main(String[] args) {
        new CitizenHomePage("Citizen");
    }
}
