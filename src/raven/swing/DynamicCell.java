package raven.swing;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JPanel;

/**
 *
 * @author RAVEN
 * @param <E>
 */
public class DynamicCell<E> extends JPanel {

    public int getSelectedIndex() {
        return selectedIndex;
    }

    public void setSelectedIndex(int selectedIndex) {
        this.selectedIndex = selectedIndex;
        repaint();
    }

    public float getScale() {
        return scale;
    }

    public float getAlpha() {
        return alpha;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public void setColumn(int column) {
        this.column = column;
    }

    public int getRow() {
        return row;
    }

    public int getColumn() {
        return column;
    }

    public List<E> getModels() {
        return models;
    }

    public double getCellHeight() {
        return (getHeight() - (getInsets().top + getInsets().bottom)) / (float) row;
    }

    public double getStartLocation() {
        return viewLocation * getCellHeight();
    }

    public void scale(float scale, float alpha) {
        this.scale = scale;
        this.alpha = alpha;
        repaint();
    }

    public void addEventDynamicCellListenter(DynamicCellListener event) {
        events.add(event);
    }

    private final List<DynamicCellListener> events = new ArrayList<>();
    private DynamicCellRender<E> dynamicCellRender;
    private final List<E> models = new ArrayList<>();
    private int selectedIndex;
    private int pressIndex = -1;
    private double viewLocation = -0.5f;
    private int row = 6;
    private int column = 7;
    private float scale = 1f;
    private float alpha = 1f;

    public DynamicCell() {
        init();
    }

    private void init() {
        setOpaque(false);
        setFocusable(true);
        MouseAdapter mouseAdapter = new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                grabFocus();
                pressIndex = getSelectedCellIndex(e.getPoint());
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                int index = getSelectedCellIndex(e.getPoint());
                if (index == pressIndex) {
                    selectedIndex = index;
                    runEventMouseSelected(e);
                }
            }

            @Override
            public void mouseMoved(MouseEvent e) {

            }

            @Override
            public void mouseExited(MouseEvent e) {

            }

            @Override
            public void mouseWheelMoved(MouseWheelEvent e) {
                double scroll;
                if (e.getWheelRotation() > 0) {
                    scroll = getCellHeight() * 0.5f;
                } else {
                    scroll = -getCellHeight() * 0.5f;
                }
                scroll(scroll);
            }

            @Override
            public void mouseDragged(MouseEvent e) {

            }

        };
        addMouseListener(mouseAdapter);
        addMouseMotionListener(mouseAdapter);
        addMouseWheelListener(mouseAdapter);
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_UP) {
                    scroll(-getCellHeight() * 0.5f);

                } else if (e.getKeyCode() == KeyEvent.VK_DOWN) {
                    scroll(getCellHeight() * 0.5f);
                }
            }
        });
    }

    public void init(E e) {
        models.clear();
        models.add(e);
        for (int i = 0; i <= column * (row + 1) - 2; i++) {
            E c = models.get(models.size() - 1);
            models.add(dynamicCellRender.next(c));
        }
    }

    private void addFirst(int number) {
        removeLast(number);
        for (int i = 0; i < number * column; i++) {
            E e = models.get(0);
            models.add(0, dynamicCellRender.previous(e));
        }
    }

    private void removeFirst(int number) {
        for (int i = 0; i < number * column; i++) {
            models.remove(0);
        }
    }

    private void addLaft(int number) {
        removeFirst(number);
        for (int i = 0; i < number * column; i++) {
            E c = models.get(models.size() - 1);
            models.add(dynamicCellRender.next(c));
        }
    }

    private void removeLast(int number) {
        for (int i = 0; i < number * column; i++) {
            models.remove(models.size() - 1);
        }
    }

    private synchronized void scroll(double scroll) {
        if (!models.isEmpty()) {
            double height = getCellHeight();
            double location = viewLocation * height;
            location -= scroll;
            if (location > 0) {
                int size = (int) Math.ceil(location / height);
                addFirst(size);
                location -= height * size;

            } else if (location < -height) {
                int size = (int) Math.ceil(location / height) * -1;
                addLaft(size);
                location += (height * size);
            }
            repaint();
            viewLocation = location / height;
        }
        selectedIndex = -1;
        runEventScroll(scroll > 0);
    }

    private void runEventScroll(boolean scrollNext) {
        for (DynamicCellListener event : events) {
            event.scrollChanged(scrollNext);
        }
    }

    private void runEventMouseSelected(MouseEvent mouse) {
        for (DynamicCellListener event : events) {
            event.mouseSelected(mouse);
        }
    }

    private int getSelectedCellIndex(Point mouse) {
        int index = -1;
        Insets inset = getInsets();
        double width = getWidth() - (inset.left + inset.right);
        double cellWidth = width / (float) column;
        double cellHeight = getCellHeight();
        double location = (viewLocation * cellHeight);
        double x = inset.left;
        double y = inset.top + location;
        for (int i = 0; i < models.size(); i++) {
            if (isCellContains(x, y, cellWidth, cellHeight, mouse)) {
                index = i;
                break;
            }
            if ((i + 1) % column == 0) {
                x = 0;
                y += cellHeight;
            } else {
                x += cellWidth;
            }
        }
        return index;
    }

    private boolean isCellContains(double x, double y, double width, double height, Point point) {
        Insets inset = getInsets();
        return (point.x >= inset.left && point.x <= getWidth() - inset.right && point.y >= inset.top && point.y <= getHeight() - inset.bottom) && new Rectangle2D.Double(x, y, width, height).contains(point);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setColor(getBackground());
        g2.fill(new Rectangle2D.Double(0, 0, getWidth(), getHeight()));
        Insets inset = getInsets();
        int width = getWidth() - (inset.left + inset.right);
        int height = getHeight() - (inset.top + inset.bottom);
        if (dynamicCellRender != null) {
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
            AffineTransform tr2 = g2.getTransform();
            tr2.translate((width - width * scale) / 2f, (height - height * scale) / 2);
            tr2.scale(scale, scale);
            g2.setTransform(tr2);
            BufferedImage img = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB);
            Graphics2D gra = img.createGraphics();
            gra.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);
            gra.setFont(getFont());
            gra.setColor(getForeground());
            if (width > 0 && height > 0) {
                create((Graphics2D) gra.create(), width, height);
            }
            dynamicCellRender.paint(gra, new Rectangle(0, 0, getWidth(), getHeight()));
            gra.dispose();
            g2.drawImage(img, 0, 0, this);
        }
        g2.dispose();
    }

    private void create(Graphics2D g2, int width, int height) {
        Insets inset = getInsets();
        AffineTransform defaultAffineTransform = g2.getTransform();
        g2.translate(inset.left, inset.top);
        double cellWidth = width / (float) column;
        double cellHeight = getCellHeight();
        double location = (viewLocation * cellHeight);
        g2.setFont(getFont());
        g2.setColor(getBackground());
        g2.fill(new Rectangle(0, 0, width, height));
        dynamicCellRender.paintBackground(g2, this, new Rectangle(0, 0, width, height));
        double x = 0;
        double y = location;
        for (int i = 0; i < models.size(); i++) {
            AffineTransform tran = g2.getTransform();
            g2.translate(x, y);
            dynamicCellRender.paintCell(g2, new Rectangle2D.Double(0, 0, cellWidth, cellHeight), models.get(i));
            g2.setTransform(tran);
            if ((i + 1) % column == 0) {
                x = 0;
                y += cellHeight;
            } else {
                x += cellWidth;
            }
        }
        g2.setComposite(AlphaComposite.Xor);
        float haft = (float) (getCellHeight() * 0.5f);
        g2.setPaint(new GradientPaint(0, 0, Color.BLACK, 0, haft, new Color(0, 0, 0, 0)));
        g2.fill(new Rectangle2D.Double(0, 0, width, haft));
        g2.setPaint(new GradientPaint(0, height - haft, new Color(0, 0, 0, 0), 0, height, Color.BLACK));
        g2.fill(new Rectangle2D.Double(0, height - haft, width, haft));
        g2.setTransform(defaultAffineTransform);
        if (inset.top > 0) {
            g2.setComposite(AlphaComposite.Clear);
            g2.fill(new Rectangle(0, 0, getWidth(), inset.top));
        }
        if (inset.bottom > 0) {
            g2.setComposite(AlphaComposite.Clear);
            g2.fill(new Rectangle(0, getHeight() - inset.bottom, getWidth(), inset.bottom));
        }
        g2.dispose();
    }

    public DynamicCellRender<E> getDynamicCellRender() {
        return dynamicCellRender;
    }

    public void setDynamicCellRender(DynamicCellRender<E> dynamicCellRender) {
        this.dynamicCellRender = dynamicCellRender;
    }
}
