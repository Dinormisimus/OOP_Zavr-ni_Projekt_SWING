package rpg.ui;

import javax.swing.*;
import javax.swing.border.AbstractBorder;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import rpg.*;

public class BubbleBorder extends AbstractBorder {
    private Color color;
    private int thickness;
    private int radii;

    public BubbleBorder(Color color, int thickness, int radii) {
        this.color = color;
        this.thickness = thickness;
        this.radii = radii;
    }

    @Override
    public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(color);
        g2.setStroke(new BasicStroke(thickness));
        g2.draw(new RoundRectangle2D.Double(x + (thickness/2.0), y + (thickness/2.0), width - thickness, height - thickness, radii, radii));
        g2.dispose();
    }
}