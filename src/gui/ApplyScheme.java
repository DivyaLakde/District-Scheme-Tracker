package gui;

import db.DBConnection;

import javax.swing.*;
import java.awt.*;
import java.sql.*;
import gui.GradientPanel;


public class ApplyScheme extends JFrame {
    private JTextField nameField, aadharField, ageField;

    private JComboBox<String> genderBox, categoryBox, schemeBox;

    public ApplyScheme() {
        setTitle("Apply for Government Scheme"); // ✅ TITLE
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 800);
        setLocationRelativeTo(null);


        GradientPanel gradientPanel = new GradientPanel();
        gradientPanel.setLayout(new GridBagLayout());
        setContentPane(gradientPanel);


        JPanel card = new JPanel(new GridBagLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(180, 180, 255), 2),
                BorderFactory.createEmptyBorder(25, 40, 25, 40)
        ));

        Font labelFont = new Font("Segoe UI", Font.BOLD, 18);
        Font fieldFont = new Font("Segoe UI", Font.PLAIN, 18);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(12, 15, 12, 15);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = 0;

        card.add(makeLabel("👤 Name:", labelFont), gbc);
        nameField = makeTextField(fieldFont);
        gbc.gridx = 1;
        card.add(nameField, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        card.add(makeLabel("🆔 Aadhar Number:", labelFont), gbc);
        aadharField = makeTextField(fieldFont);
        gbc.gridx = 1;
        card.add(aadharField, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        card.add(makeLabel("🎂 Age:", labelFont), gbc);
        ageField = makeTextField(fieldFont);
        gbc.gridx = 1;
        card.add(ageField, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        card.add(makeLabel("⚧️ Gender:", labelFont), gbc);
        genderBox = new JComboBox<>(new String[]{"Male", "Female", "Other"});
        genderBox.setFont(fieldFont);
        gbc.gridx = 1;
        card.add(genderBox, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        card.add(makeLabel("🧾 Category:", labelFont), gbc);
        categoryBox = new JComboBox<>(new String[]{"OBC", "ST", "SC", "BPL", "Farmer", "Women"});
        categoryBox.setFont(fieldFont);
        gbc.gridx = 1;
        card.add(categoryBox, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        card.add(makeLabel("🏷️ Select Scheme:", labelFont), gbc);
        schemeBox = new JComboBox<>();
        schemeBox.setFont(fieldFont);
        gbc.gridx = 1;
        card.add(schemeBox, gbc);
        loadSchemes();

        JButton submitBtn = new JButton("📤 Submit Application");
        submitBtn.setBackground(new Color(0, 120, 215));
        submitBtn.setForeground(Color.WHITE);
        submitBtn.setFont(new Font("Segoe UI", Font.BOLD, 18));
        submitBtn.setFocusPainted(false);
        submitBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 2;
        card.add(submitBtn, gbc);

        submitBtn.addActionListener(e -> submitApplication());

        gradientPanel.add(card);
        setVisible(true);
    }

    private JLabel makeLabel(String text, Font font) {
        JLabel label = new JLabel(text);
        label.setFont(font);
        return label;
    }

    private JTextField makeTextField(Font font) {
        JTextField field = new JTextField();
        field.setFont(font);
        field.setPreferredSize(new Dimension(200, 40));
        return field;
    }

    private void loadSchemes() {
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT scheme_id, name FROM schemes";
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                int id = rs.getInt("scheme_id");
                String name = rs.getString("name");
                schemeBox.addItem(id + " - " + name);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void submitApplication() {
        String name = nameField.getText();
        String aadhar = aadharField.getText();
        String ageStr = ageField.getText();
        String gender = (String) genderBox.getSelectedItem();
        String category = (String) categoryBox.getSelectedItem();
        String schemeInfo = (String) schemeBox.getSelectedItem();

        if (name.isEmpty() || aadhar.isEmpty() || ageStr.isEmpty() || schemeInfo == null) {
            JOptionPane.showMessageDialog(this, "⚠️ Please fill all required fields.");
            return;
        }

        try {
            int age = Integer.parseInt(ageStr);
            int schemeId = Integer.parseInt(schemeInfo.split(" - ")[0]);

            Connection conn = DBConnection.getConnection();
            String sql = "INSERT INTO applications (name, aadhar_no, age, gender, eligibility_category, scheme_id, status) VALUES (?, ?, ?, ?, ?, ?, 'Pending')";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, name);
            stmt.setString(2, aadhar);
            stmt.setInt(3, age);
            stmt.setString(4, gender);
            stmt.setString(5, category);
            stmt.setInt(6, schemeId);
            stmt.executeUpdate();

            JOptionPane.showMessageDialog(this, "✅ Application submitted successfully!");
            dispose();
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "❌ Age must be a number.");
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "❌ Error submitting application.");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ApplyScheme());
    }
}
