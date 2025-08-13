package gui;

import javax.swing.JPanel;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GradientPaint;
import java.awt.Color;

public class GradientPanel extends JPanel {
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        int w = getWidth();
        int h = getHeight();

        Color startColor = new Color(255, 204, 102);   // light orange
        Color endColor = new Color(255, 153, 204);     // soft pink



        GradientPaint gp = new GradientPaint(0, 0, startColor, w, h, endColor);
        g2d.setPaint(gp);
        g2d.fillRect(0, 0, w, h);
    }
}

