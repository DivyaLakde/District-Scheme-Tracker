package gui;

import javax.swing.*;
import java.awt.*;

public class OfficerLogin extends JFrame {
    public OfficerLogin() {
        setTitle("Officer Login");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setLocationRelativeTo(null);

        // 🔹 Background panel with image
        JLabel background = new JLabel();
        background.setLayout(new BorderLayout());
        ImageIcon bgIcon = new ImageIcon("src/assets/background.jpg");
        Image scaledImg = bgIcon.getImage().getScaledInstance(1920, 1080, Image.SCALE_SMOOTH);
        background.setIcon(new ImageIcon(scaledImg));
        setContentPane(background);

        // 🔹 Title panel
        JLabel titleLabel = new JLabel("Officer Login", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 42));
        titleLabel.setForeground(Color.WHITE);
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setOpaque(false);
        titlePanel.setBorder(BorderFactory.createEmptyBorder(30, 0, 0, 0));
        titlePanel.add(titleLabel, BorderLayout.CENTER);
        background.add(titlePanel, BorderLayout.NORTH);

        // 🔹 Shadowed glass panel for form
        JPanel glassPanel = new JPanel(new GridBagLayout()) {
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.setColor(new Color(255, 255, 255, 80));
                g.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30);
            }
        };
        glassPanel.setOpaque(false);
        glassPanel.setPreferredSize(new Dimension(500, 450));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 15, 15, 15);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;

        // 🔹 Officer Avatar
        try {
            ImageIcon officerIcon = new ImageIcon("src/assets/officer_avatar.png");
            Image avatar = officerIcon.getImage().getScaledInstance(100, 100, Image.SCALE_SMOOTH);
            JLabel avatarLabel = new JLabel(new ImageIcon(avatar));
            glassPanel.add(avatarLabel, gbc);
        } catch (Exception e) {
            System.out.println("Officer image not found!");
        }

        // 🔹 Username
        gbc.gridy++;
        gbc.gridwidth = 1;
        JLabel userLabel = new JLabel("Username:");
        userLabel.setFont(new Font("Segoe UI", Font.PLAIN, 20));
        glassPanel.add(userLabel, gbc);

        gbc.gridx = 1;
        JTextField userField = new JTextField(18);
        userField.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        glassPanel.add(userField, gbc);

        // 🔹 Password
        gbc.gridy++;
        gbc.gridx = 0;
        JLabel passLabel = new JLabel("Password:");
        passLabel.setFont(new Font("Segoe UI", Font.PLAIN, 20));
        glassPanel.add(passLabel, gbc);

        gbc.gridx = 1;
        JPasswordField passField = new JPasswordField(18);
        passField.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        glassPanel.add(passField, gbc);

        // 🔹 Login Button
        gbc.gridy++;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        JButton loginBtn = new JButton("Login");
        loginBtn.setFont(new Font("Segoe UI", Font.BOLD, 20));
        loginBtn.setBackground(new Color(0, 120, 215));
        loginBtn.setForeground(Color.WHITE);
        loginBtn.setFocusPainted(false);
        glassPanel.add(loginBtn, gbc);

        loginBtn.addActionListener(e -> {
            String username = userField.getText();
            String password = new String(passField.getPassword());
            if (username.equals("officer") && password.equals("admin")) {
                new OfficerDashboard().setVisible(true);

                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Invalid credentials!");
            }
        });

        // Add glass panel to center of background
        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setOpaque(false);
        centerPanel.add(glassPanel);
        background.add(centerPanel, BorderLayout.CENTER);

        setVisible(true);
    }

    public static void main(String[] args) {
        new OfficerLogin();
    }
}

