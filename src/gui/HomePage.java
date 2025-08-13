package gui;

import javax.swing.*;
import java.awt.*;

public class HomePage extends JFrame {

    // ✅ Custom JPanel for Background Image
    static class BackgroundPanel extends JPanel {
        private Image backgroundImage;

        public BackgroundPanel(String imagePath) {
            try {
                ImageIcon icon = new ImageIcon(imagePath);
                backgroundImage = icon.getImage().getScaledInstance(1920, 1080, Image.SCALE_SMOOTH);
            } catch (Exception e) {
                System.out.println("Background image not found.");
            }
            setLayout(new BorderLayout());
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (backgroundImage != null) {
                g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
            }
        }
    }

    public HomePage() {
        setTitle("District Scheme Tracker - Home");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setLocationRelativeTo(null);

        // ✅ Set custom background panel
        BackgroundPanel background = new BackgroundPanel("src/assets/background.jpg");
        setContentPane(background);

        // ✅ Header with logo and title
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        headerPanel.setOpaque(false);

        try {
            ImageIcon logoIcon = new ImageIcon("src/assets/emblem.png");
            Image logoImg = logoIcon.getImage().getScaledInstance(50, 50, Image.SCALE_SMOOTH);
            JLabel logoLabel = new JLabel(new ImageIcon(logoImg));
            headerPanel.add(logoLabel);
        } catch (Exception e) {
            System.out.println("Logo not found.");
        }

        JLabel titleLabel = new JLabel("District Scheme Tracker");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 50));
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel);

        background.add(headerPanel, BorderLayout.NORTH);

        // ✅ Button panel
        JPanel buttonPanel = new JPanel(new GridLayout(3, 1, 10, 10));
        buttonPanel.setOpaque(false);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(150, 500, 150, 500));

        JButton officerLoginBtn = new JButton("👮 Officer Login");
        JButton userLoginBtn = new JButton("👤 Citizen Login");
        JButton exitBtn = new JButton("❌ Exit");

        Font btnFont = new Font("Segoe UI", Font.BOLD, 16);
        JButton[] buttons = {officerLoginBtn, userLoginBtn, exitBtn};
        for (JButton btn : buttons) {
            btn.setFont(btnFont);
            btn.setBackground(new Color(50, 100, 150));
            btn.setForeground(Color.WHITE);
            btn.setFocusPainted(false);
            btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
            buttonPanel.add(btn);
        }

        officerLoginBtn.addActionListener(e -> new OfficerLogin());
        userLoginBtn.addActionListener(e -> new UserLogin());
        exitBtn.addActionListener(e -> System.exit(0));

        background.add(buttonPanel, BorderLayout.CENTER);

        // ✅ About Us section in footer
        JPanel footerPanel = new JPanel();
        footerPanel.setOpaque(false);
        footerPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 30, 20));

        JLabel aboutLabel = new JLabel("About Us: This portal enables citizens to apply and track district-level government schemes.", SwingConstants.CENTER);
        aboutLabel.setForeground(Color.WHITE);
        aboutLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));

        footerPanel.add(aboutLabel);
        background.add(footerPanel, BorderLayout.SOUTH);

        setVisible(true);
    }

    public static void main(String[] args) {
        new HomePage();
    }
}
