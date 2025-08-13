package gui;

import db.DBConnection;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;

public class UserSignUp extends JFrame {
    public UserSignUp() {
        setTitle("Citizen Sign Up");
        setSize(450, 350);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JPanel formPanel = new JPanel(new GridLayout(6, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 20));

        JTextField nameField = new JTextField();
        JTextField usernameField = new JTextField();
        JTextField aadharField = new JTextField();
        JTextField ageField = new JTextField();
        JComboBox<String> genderBox = new JComboBox<>(new String[]{"Male", "Female", "Other"});
        JPasswordField passwordField = new JPasswordField();

        formPanel.add(new JLabel("Full Name:"));
        formPanel.add(nameField);
        formPanel.add(new JLabel("Username:"));
        formPanel.add(usernameField);
        formPanel.add(new JLabel("Aadhar No:"));
        formPanel.add(aadharField);
        formPanel.add(new JLabel("Age:"));
        formPanel.add(ageField);
        formPanel.add(new JLabel("Gender:"));
        formPanel.add(genderBox);
        formPanel.add(new JLabel("Password:"));
        formPanel.add(passwordField);

        JButton signupButton = new JButton("📝 Register");
        JPanel btnPanel = new JPanel();
        btnPanel.add(signupButton);

        add(formPanel, BorderLayout.CENTER);
        add(btnPanel, BorderLayout.SOUTH);

        signupButton.addActionListener(e -> {
            String name = nameField.getText();
            String username = usernameField.getText();
            String aadhar = aadharField.getText();
            int age = Integer.parseInt(ageField.getText());
            String gender = (String) genderBox.getSelectedItem();
            String password = new String(passwordField.getPassword());

            try (Connection conn = DBConnection.getConnection()) {
                String query = "INSERT INTO citizens (name, username, aadhar_no, age, gender, password) VALUES (?, ?, ?, ?, ?, ?)";
                PreparedStatement ps = conn.prepareStatement(query);
                ps.setString(1, name);
                ps.setString(2, username);
                ps.setString(3, aadhar);
                ps.setInt(4, age);
                ps.setString(5, gender);
                ps.setString(6, password);

                int rows = ps.executeUpdate();
                if (rows > 0) {
                    JOptionPane.showMessageDialog(this, "✅ Registered successfully!");
                    new UserLogin(); // Redirect to login
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(this, "❌ Registration failed.");
                }

            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "❌ Error: " + ex.getMessage());
            }
        });

        setVisible(true);
    }

    public static void main(String[] args) {
        new UserSignUp();
    }
}
