package ru.ekbi.nntest;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.ArrayList;

public class Canvas extends JFrame implements MouseListener {

    private static final int POINT_WIDTH = 20; //px
    private static final int POINT_BORDER_WIDTH = 3; //px
    public static final int SCALE = 8;

    private int initialWidth;
    private int initialHeight;

    private BufferedImage img;
    private BufferedImage prediction;

    private List<Point> points = new ArrayList<>();

    public Canvas(int width, int height) {
        initialWidth = width;
        initialHeight = height;
        img = new BufferedImage(initialWidth, initialHeight, BufferedImage.TYPE_INT_RGB);
        prediction = new BufferedImage(initialWidth / SCALE, initialHeight / SCALE, BufferedImage.TYPE_INT_RGB);

        this.setSize(initialWidth + 16, initialHeight + 38);
        this.setVisible(true);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLocation(50, 50);
        addMouseListener(this);
    }

    @Override
    public void paint(Graphics g) {

        Graphics graphics = img.getGraphics();
        graphics.drawImage(prediction, 0, 0, initialWidth, initialHeight, this);

        for (Point p : points) {
            graphics.setColor(Color.WHITE);
            graphics.fillOval(p.x - POINT_BORDER_WIDTH, p.y - POINT_BORDER_WIDTH, POINT_WIDTH + (2 * POINT_BORDER_WIDTH),
                    POINT_WIDTH + (2 * POINT_BORDER_WIDTH));
            graphics.setColor(p.type == 0 ? Color.GREEN : Color.BLUE);
            graphics.fillOval(p.x, p.y, POINT_WIDTH, POINT_WIDTH);
        }
        g.drawImage(img, 8, 30, initialWidth, initialHeight, this);
    }

    public void setPredictionPointType(int x, int y, int rgb)
    {
        prediction.setRGB(x, y, rgb);
    }

    public List<Point> getPoints()
    {
        return points;
    }

    public int getInitialHeight()
    {
        return initialHeight;
    }

    public int getInitialWidth()
    {
        return initialWidth;
    }

    @Override
    public void mouseClicked(MouseEvent e)
    {

    }

    @Override
    public void mousePressed(MouseEvent e) {
        int type = (e.getButton() == 3) ? 1 : 0;
        points.add(new Point(e.getX() - 16, e.getY() - 38, type));
    }

    @Override
    public void mouseReleased(MouseEvent e)
    {

    }

    @Override
    public void mouseEntered(MouseEvent e)
    {

    }

    @Override
    public void mouseExited(MouseEvent e)
    {

    }
}