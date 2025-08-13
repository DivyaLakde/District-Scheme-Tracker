package gui;

import db.DBConnection;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Date;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import com.google.zxing.*;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.common.BitMatrix;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;

public class CheckApplicationStatus extends JFrame {
    JTextField appIdField;
    JLabel resultLabel, remarksLabel;
    String officerRemarks = "";

    public CheckApplicationStatus() {
        setTitle("Check Application Status");
        setExtendedState(JFrame.MAXIMIZED_BOTH); // ✅ Full screen
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        //JPanel mainPanel = new JPanel(new GridLayout(6, 1, 10, 10));
        GradientPanel mainPanel = new GradientPanel();
        mainPanel.setLayout(new GridLayout(6, 1, 10, 10));
        //mainPanel.setBackground(new Color(240, 248, 255)); // Light blue background

        mainPanel.setBorder(BorderFactory.createEmptyBorder(60, 300, 60, 300));

        appIdField = new JTextField();
        appIdField.setFont(new java.awt.Font("Segoe UI", java.awt.Font.PLAIN, 18));
        resultLabel = new JLabel("Status will appear here.");
        remarksLabel = new JLabel("");

        resultLabel.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 18));
        remarksLabel.setFont(new java.awt.Font("Segoe UI", java.awt.Font.PLAIN, 16));

        JButton checkBtn = new JButton("✅ Check Status");
        checkBtn.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 16));
        JButton downloadBtn = new JButton("📄 Download Acknowledgment (PDF)");
        downloadBtn.setFont(new java.awt.Font("Segoe UI", java.awt.Font.PLAIN, 16));

        mainPanel.add(new JLabel("Enter Application ID:", SwingConstants.CENTER)).setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 18));
        mainPanel.add(appIdField);
        mainPanel.add(checkBtn);
        mainPanel.add(resultLabel);
        mainPanel.add(remarksLabel);
        mainPanel.add(downloadBtn);

        checkBtn.addActionListener(e -> checkStatus());
        downloadBtn.addActionListener(e -> {
            try {
                generatePDF();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        add(mainPanel);
        setVisible(true);
    }

    public void checkStatus() {
        String appId = appIdField.getText().trim();
        if (appId.isEmpty()) {
            resultLabel.setText("❌ Please enter Application ID.");
            remarksLabel.setText("");
            return;
        }

        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT a.name, s.name AS scheme_name, a.status, a.apply_date, a.officer_remarks " +
                    "FROM applications a JOIN schemes s ON a.scheme_id = s.scheme_id WHERE application_id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, appId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String name = rs.getString("name");
                String scheme = rs.getString("scheme_name");
                String status = rs.getString("status");
                Date date = rs.getTimestamp("apply_date");
                officerRemarks = rs.getString("officer_remarks");

                resultLabel.setText("✅ " + name + " - " + scheme + " [" + status + "]");
                remarksLabel.setText("📌 Remarks: " + (officerRemarks != null ? officerRemarks : "None"));
            } else {
                resultLabel.setText("❌ Application ID not found.");
                remarksLabel.setText("");
            }

        } catch (Exception e) {
            resultLabel.setText("❌ Error: " + e.getMessage());
        }
    }

    private void generatePDF() throws Exception {
        String appId = appIdField.getText().trim();
        if (appId.isEmpty()) {
            JOptionPane.showMessageDialog(this, "❌ Please check status before downloading.");
            return;
        }

        String name = "", scheme = "", status = "", dateStr = "";
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT a.name, s.name AS scheme_name, a.status, a.apply_date, a.officer_remarks " +
                    "FROM applications a JOIN schemes s ON a.scheme_id = s.scheme_id WHERE application_id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, appId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                name = rs.getString("name");
                scheme = rs.getString("scheme_name");
                status = rs.getString("status");
                dateStr = rs.getTimestamp("apply_date").toString();
                officerRemarks = rs.getString("officer_remarks");
            } else {
                JOptionPane.showMessageDialog(this, "❌ Application ID not found.");
                return;
            }
        }

        String userHome = System.getProperty("user.home");
        File pdfFile = new File(userHome + "/Downloads/Acknowledgment_" + appId + ".pdf");

        Document document = new Document();
        PdfWriter.getInstance(document, new FileOutputStream(pdfFile));
        document.open();

        // Logo
        String logoPath = "src/assets/emblem.png";
        if (new File(logoPath).exists()) {
            com.itextpdf.text.Image logo = com.itextpdf.text.Image.getInstance(logoPath);
            logo.scaleToFit(80, 80);
            logo.setAlignment(Element.ALIGN_CENTER);
            document.add(logo);
        }

        // Title
        com.itextpdf.text.Font titleFont = new com.itextpdf.text.Font(
                com.itextpdf.text.Font.FontFamily.HELVETICA, 16, com.itextpdf.text.Font.BOLD
        );

        Paragraph title = new Paragraph("Government Scheme Application Acknowledgment", titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        document.add(title);
        document.add(Chunk.NEWLINE);

        com.itextpdf.text.Font normalFont = new com.itextpdf.text.Font(
                com.itextpdf.text.Font.FontFamily.HELVETICA, 16, com.itextpdf.text.Font.BOLD
        );

        document.add(new Paragraph("Application ID: " + appId, normalFont));
        document.add(new Paragraph("Applicant Name: " + name, normalFont));
        document.add(new Paragraph("Scheme: " + scheme, normalFont));
        document.add(new Paragraph("Status: " + status, normalFont));
        document.add(new Paragraph("Apply Date: " + dateStr, normalFont));
        document.add(new Paragraph("Officer Remarks: " + (officerRemarks != null ? officerRemarks : "None"), normalFont));
        document.add(Chunk.NEWLINE);

        // QR Code
        String qrFilePath = "src/assets/qr_" + appId + ".png";
        generateQRCodeImage("Application ID: " + appId + "\nStatus: " + status, 150, 150, qrFilePath);
        com.itextpdf.text.Image qr = com.itextpdf.text.Image.getInstance(qrFilePath);
        qr.scaleToFit(100, 100);
        qr.setAlignment(Element.ALIGN_CENTER);
        document.add(qr);

        document.close();
        JOptionPane.showMessageDialog(this, "📄 PDF saved to: " + pdfFile.getAbsolutePath());
    }

    private void generateQRCodeImage(String text, int width, int height, String filePath) throws Exception {
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, width, height);

        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                int grayValue = (bitMatrix.get(x, y) ? 0 : 255);
                image.setRGB(x, y, (grayValue << 16) | (grayValue << 8) | grayValue);
            }
        }

        File outputFile = new File(filePath);
        ImageIO.write(image, "png", outputFile);
    }

    public static void main(String[] args) {
        new CheckApplicationStatus();
    }
}
