import javax.swing.*;
import java.awt.*;
import java.awt.geom.*;
import java.util.ArrayList;
import java.util.List;

public class RotateF extends JPanel {
    private double angle = 0;                // rotation angle of small F
    private final List<Point2D.Double> path; // points making up big-F path (centered coords)
    private int pathIndex = 0;               // current index along path
    private final int delay = 40;            // ms between frames

    public RotateF() {
        setBackground(Color.WHITE);
        path = buildBigFPath();
        // Timer drives rotation + movement
        new Timer(delay, e -> {
            angle = (angle + 6) % 360;
            pathIndex = (pathIndex + 1) % path.size();
            repaint();
        }).start();
    }

    private List<Point2D.Double> buildBigFPath() {
        List<Point2D.Double> pts = new ArrayList<>();
        // Coordinates are centered (0,0). Adjust size as needed.
        // We'll build segments for a big "F":
        // top horizontal: (-180,-120) -> (180,-120)
        // back to left: (180,-120) -> (-180,-120) (we already have forward sweep, but to simulate drawing direction we'll do forward then back if desired)
        // left vertical: (-180,-120) -> (-180,120)
        // middle horizontal: (-180,0) -> (60,0)
        addSegment(pts, -180, -120, 180, -120, 80); // top horizontal
        addSegment(pts, 180, -120, -180, -120, 80); // return (optional for continuous path)
        addSegment(pts, -180, -120, -180, 120, 100); // left vertical down
        addSegment(pts, -180, 0, 60, 0, 70); // middle horizontal
        return pts;
    }

    private void addSegment(List<Point2D.Double> pts, double x1, double y1, double x2, double y2, int steps) {
        for (int i = 0; i <= steps; i++) {
            double t = (double) i / steps;
            double x = x1 + t * (x2 - x1);
            double y = y1 + t * (y2 - y1);
            pts.add(new Point2D.Double(x, y));
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        // enable rendering quality
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Move origin to center of panel
        int cx = getWidth() / 2;
        int cy = getHeight() / 2;
        g2.translate(cx, cy);

        // Draw the big-F path for reference (light gray)
        g2.setStroke(new BasicStroke(3f));
        g2.setColor(new Color(200, 200, 200));
        for (int i = 0; i < path.size() - 1; i++) {
            Point2D.Double p1 = path.get(i);
            Point2D.Double p2 = path.get(i + 1);
            g2.draw(new Line2D.Double(p1.x, p1.y, p2.x, p2.y));
        }

        // Draw a small marker showing current path position
        Point2D.Double pos = path.get(pathIndex);
        g2.setColor(Color.GRAY);
        g2.fill(new Ellipse2D.Double(pos.x - 3, pos.y - 3, 6, 6));

        // Save transform
        AffineTransform saved = g2.getTransform();

        // Move to current position on path, rotate, then draw the small "F"
        g2.translate(pos.x, pos.y);
        g2.rotate(Math.toRadians(angle));

        // Draw small rotating "F" centered at (0,0)
        String s = "F";
        Font font = new Font("Arial", Font.BOLD, 80);
        g2.setFont(font);
        FontMetrics fm = g2.getFontMetrics(font);
        // Center the string horizontally and vertically
        int strWidth = fm.stringWidth(s);
        int strHeight = fm.getAscent() - fm.getDescent();
        g2.setColor(Color.BLUE.darker());
        g2.drawString(s, -strWidth / 2, strHeight / 2);

        // Restore transform and dispose
        g2.setTransform(saved);
        g2.dispose();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame f = new JFrame("Rotating F tracing a bigger F");
            RotateF panel = new RotateF();
            f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            f.add(panel);
            f.setSize(700, 600);
            f.setLocationRelativeTo(null);
            f.setVisible(true);
        });
    }
}
