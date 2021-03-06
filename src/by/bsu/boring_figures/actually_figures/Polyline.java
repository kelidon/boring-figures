package by.bsu.boring_figures.actually_figures;


import java.awt.*;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author shchors_vs
 * @version 1.0
 * @created 02-Mar-2020 3:52:17 PM
 */
public class Polyline extends Figure1D {

    private List<Point> points;

    public Polyline(List<Point> points) {
        super(points);
        this.points = points;
    }

    public List<Point> getPoints() {
        return points;
    }

    @Override
    public void draw(Graphics2D graphics2D) {
        int[] xPoints = points.stream().mapToInt(Point::getX).toArray();
        int[] yPoints = points.stream().mapToInt(Point::getY).toArray();
        graphics2D.setColor(getBorderColor());
        graphics2D.drawPolyline(xPoints, yPoints, points.size());
    }

    @Override
    public boolean contains(Point point) {
        for (int i = 1; i < points.size(); i++) {
            LineSegment lineSegment = new LineSegment(points.get(i - 1), points.get(i));
            if (lineSegment.contains(point)) return true;
        }
        return false;
    }

    /**
     * @param newLocation
     */
    @Override
    public void move(Point newLocation) {
        int shiftX = newLocation.getX() - location().getX();
        int shiftY = newLocation.getY() - location().getY();
        this.points = this.getPoints().stream()
                .map(p -> new Point(p.getX() + shiftX, p.getY() + shiftY))
                .collect(Collectors.toList());

        initCenter(points);
    }
}