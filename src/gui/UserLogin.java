package gui;

import javax.swing.*;
import java.awt.*;

public class UserLogin extends JFrame {
    public UserLogin() {
        setTitle("Citizen Login");
        setExtendedState(JFrame.MAXIMIZED_BOTH); // Full screen
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // ✅ Set background image using custom JPanel
        setContentPane(new JPanel() {
            Image bg = new ImageIcon("src/assets/background.jpg").getImage(); // ✅ your image path

            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.drawImage(bg, 0, 0, getWidth(), getHeight(), this); // scale to full screen
            }
        });

        setLayout(new GridBagLayout()); // center the card on background

        Font titleFont = new Font("Segoe UI", Font.BOLD, 36);
        Font labelFont = new Font("Segoe UI", Font.BOLD, 22);
        Font fieldFont = new Font("Segoe UI", Font.PLAIN, 20);
        Font buttonFont = new Font("Segoe UI", Font.BOLD, 20);

        JPanel card = new JPanel(new GridBagLayout());
        card.setBackground(new Color(255, 255, 255, 0)); // 180 = semi-transparent white
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY, 2),
                BorderFactory.createEmptyBorder(40, 60, 40, 60)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(50, 20, 20, 20);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridwidth = 2;
        gbc.gridx = 0; gbc.gridy = 0;

        JLabel titleLabel = new JLabel("Citizen Login");

        titleLabel.setForeground(Color.WHITE);

        titleLabel.setFont(titleFont);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        card.add(titleLabel, gbc);

        gbc.gridwidth = 1;
        gbc.gridy++;

        // Username
        JLabel userLabel = new JLabel("Username:");
        userLabel.setForeground(Color.WHITE);
        userLabel.setFont(labelFont);
        JTextField userField = new JTextField(15);
        userField.setFont(fieldFont);

        gbc.gridx = 0;
        card.add(userLabel, gbc);
        gbc.gridx = 1;
        card.add(userField, gbc);

        // Password
        JLabel passLabel = new JLabel("Password:");
        passLabel.setForeground(Color.WHITE);
        passLabel.setFont(labelFont);
        JPasswordField passField = new JPasswordField(15);
        passField.setFont(fieldFont);

        gbc.gridx = 0; gbc.gridy++;
        card.add(passLabel, gbc);
        gbc.gridx = 1;
        card.add(passField, gbc);

        // Buttons
        JButton loginButton = new JButton("🔐 Login");
        JButton signupButton = new JButton("📝 Sign Up");
        loginButton.setFont(buttonFont);
        signupButton.setFont(buttonFont);
        loginButton.setBackground(new Color(0, 120, 215));
        loginButton.setForeground(Color.WHITE);
        signupButton.setBackground(new Color(0, 180, 90));
        signupButton.setForeground(Color.WHITE);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 40, 20));
        buttonPanel.setOpaque(false); // ✅ transparent background
        buttonPanel.add(loginButton);
        buttonPanel.add(signupButton);

        gbc.gridx = 0; gbc.gridy++;
        gbc.gridwidth = 2;
        card.add(buttonPanel, gbc);

        // Button actions
        loginButton.addActionListener(e -> {
            String username = userField.getText();
            String password = new String(passField.getPassword());
            JOptionPane.showMessageDialog(this, "Logged in as " + username);
            new CitizenHomePage(username); // your homepage
            dispose();
        });

        signupButton.addActionListener(e -> {
            new UserSignUp(); // create this GUI
            dispose();
        });

        add(card);
        setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(UserLogin::new);
    }
}