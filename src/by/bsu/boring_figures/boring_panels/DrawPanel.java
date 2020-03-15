package by.bsu.boring_figures.boring_panels;

import by.bsu.boring_figures.actually_figures.Figure;
import by.bsu.boring_figures.actually_figures.Point;
import by.bsu.boring_figures.service.FiguresBuilder;
import by.bsu.boring_figures.service.PointsShortageException;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import static by.bsu.boring_figures.boring_panels.ToolsPanel.figuresBinding;
import static by.bsu.boring_figures.boring_panels.ToolsPanel.verticesSpinner;
import static java.awt.event.InputEvent.ALT_MASK;
import static java.awt.event.InputEvent.CTRL_MASK;


import static java.awt.event.ActionEvent.SHIFT_MASK;

public class DrawPanel extends JPanel {

    private List<Figure> figures;
    private Figure selected;
    private List<Point> points;
    private FiguresBuilder builder;

    public DrawPanel() {
        super(true);
        this.figures = new ArrayList<>();
        this.points = new ArrayList<>();
        this.builder = new FiguresBuilder();

        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {

                if (selected != null && (e.getModifiers() & ALT_MASK) != 0) {
                    System.out.println("move " + selected.getClass().getSimpleName());
                    selected.move(new Point(e.getX(), e.getY()));
                    repaint();
                    return;
                }

                if ((e.getModifiers() & SHIFT_MASK) != 0) {
                    points.clear();
                } else if ((e.getModifiers() & CTRL_MASK) != 0) {
                    points.add(new Point(e.getX(), e.getY()));

                    //FIXME clear points when the new figure is chosen
                    Class<?> clazz = figuresBinding.get(ToolsPanel.figuresComboBox.getSelectedItem());

                    builder.setClazz(clazz);
                    builder.setPoints(points);
                    builder.setVerticesNumber((int) verticesSpinner.getValue());

                    try {
                        Figure f = builder.build();
                        addFigure(f);

                        //FIXME clear points when the figure is done
                        if (!f.getClass().getSimpleName().startsWith("Poly")) {
                            points.clear();
                            System.out.println("nice");
                        }

                    } catch (IllegalAccessException | InvocationTargetException | InstantiationException ex) {
                        System.out.println("can't bulid");
                    } catch (PointsShortageException ex) {
                        System.out.println("need more points");
                    }
                    return;

                }
                Point p = new Point(e.getX(), e.getY());
                setSelected(figures.stream()
                        .filter(f -> f.contains(p))
                        .findFirst()
                        .orElse(null));

            }
        });
    }

    public void addFigure(Figure figure) {
        this.figures.add(figure);
        setSelected(figure);
        repaint();
    }

    public void setSelected(Figure figure) {
        if (figure != this.selected) {
            if (this.selected != null) this.selected.deselect();
            if (figure == null || !figures.contains(figure)) {
                this.selected = null;
            } else {
                this.selected = figure;
                this.selected.select();
            }
            repaint();
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponents(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setStroke(new BasicStroke(5));
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        figures.forEach(drawable -> drawable.draw(g2d));
    }
}
