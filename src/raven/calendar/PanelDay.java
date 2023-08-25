package raven.calendar;

import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.ui.FlatUIUtils;
import raven.calendar.utils.CalendarCellListener;
import raven.calendar.utils.CellRenderCustom;
import raven.calendar.model.ModelMonth;
import raven.calendar.model.ModelDate;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;
import java.util.Calendar;
import java.util.Date;
import javax.swing.SwingUtilities;
import raven.calendar.utils.PanelDateListener;
import raven.swing.DynamicCell;
import raven.swing.DynamicCellListener;

/**
 *
 * @author Raven
 */
public class PanelDay extends DynamicCell<ModelDate> {

    public void setPanelDateListener(PanelDateListener panelDateListener) {
        this.panelDateListener = panelDateListener;
    }

    public ModelMonth getMonth() {
        return month;
    }

    public void setMonth(ModelMonth month) {
        this.month = month;
        init(getDisplayDate(month));
    }

    private ModelDate selectedDate;
    private final Point mouse = new Point();
    private ModelMonth month;
    public static final String DATE[] = {"Su", "Mo", "Tu", "We", "Th", "Fr", "Sa"};
    private CalendarCellListener calendarCellListener;
    private PanelDateListener panelDateListener;

    public PanelDay() {
        init();
    }

    private void init() {
        putClientProperty(FlatClientProperties.STYLE, ""
                + "border:40,0,0,0;"
                + "background:if($Calendar.background,$Calendar.background,$Panel.background)");
        MouseAdapter mouseAdapter = new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                mouse.setLocation(e.getPoint());
                repaint();
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                mouse.setLocation(e.getPoint());
                repaint();
            }
        };
        DynamicCellListener dynamicCellListener = new DynamicCellListener() {
            @Override
            public void scrollChanged(boolean scrollNext) {
                calendarCellListener.scrollChanged();
            }

            @Override
            public void mouseSelected(MouseEvent mouse) {
                if (SwingUtilities.isLeftMouseButton(mouse)) {
                    int index = getSelectedIndex();
                    if (index != -1) {
                        if (mouse.isControlDown()) {
                            if (selectedDate != null && selectedDate.compareTo(getModels().get(index))) {
                                selectedDate = null;
                            } else {
                                selectedDate = getModels().get(index);
                            }
                        } else {
                            selectedDate = getModels().get(index);
                        }
                    }
                    calendarCellListener.cellSelected(mouse, index);
                }
            }
        };
        addEventDynamicCellListenter(dynamicCellListener);
        addMouseMotionListener(mouseAdapter);
        setDynamicCellRender(new CellRenderCustom<ModelDate>(mouse) {
            @Override
            public void paint(Graphics2D g2, Rectangle rectangle) {
                FontMetrics fm = g2.getFontMetrics();
                double cellWidth = rectangle.getWidth() / 7f;
                double cellHeight = 40f;
                double cx = 0;
                for (String date : DATE) {
                    Rectangle2D fr = fm.getStringBounds(date, g2);
                    float x = (float) (cx + (cellWidth - fr.getWidth()) / 2f);
                    float y = (float) (((cellHeight - fr.getHeight()) / 2) + fm.getAscent());
                    g2.drawString(date, x, y);
                    cx += cellWidth;
                }
            }

            @Override
            public void paintCell(Graphics2D g2, Rectangle2D rectangle, ModelDate e) {
                boolean active = false;
                if (panelDateListener != null) {
                    active = panelDateListener.cellPaint(g2, rectangle, e);
                }
                if (e.isToday()) {
                    g2.setColor(FlatUIUtils.getUIColor("Calendar.selectionForeground", getForeground()));
                } else {
                    if (active || (e.getYear() == month.getYear() && e.getMonth() == month.getMonth())) {
                        g2.setColor(getForeground());
                    } else {
                        g2.setColor(convertColor(getForeground(), 0.3f));
                    }
                }
                FontMetrics fm = g2.getFontMetrics();
                String text = e.getDay() + "";
                Rectangle2D fr = fm.getStringBounds(text, g2);
                float x = (float) ((rectangle.getWidth() - fr.getWidth()) / 2f);
                float y = (float) (((rectangle.getHeight() - fr.getHeight()) / 2) + fm.getAscent());
                g2.drawString(text, x, y);
            }

            @Override
            public ModelDate next(ModelDate last) {
                int index = (getModels().size() - 1) / 2;
                index += 1;
                if (index < getModels().size()) {
                    ModelDate m = getModels().get(index);
                    month.setMonth(m.getMonth());
                    month.setYear(m.getYear());
                }
                return new ModelDate(addDate(last, 1));
            }

            @Override
            public ModelDate previous(ModelDate first) {
                int index = (getModels().size() - 1) / 2;
                index -= 1;
                if (index < getModels().size()) {
                    ModelDate m = getModels().get(index);
                    month.setMonth(m.getMonth());
                    month.setYear(m.getYear());
                }
                return new ModelDate(addDate(first, -1));
            }
        });
        month = new ModelMonth();
        init(getDisplayDate(month));
    }

    private Date addDate(ModelDate date, int values) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, date.getYear());
        calendar.set(Calendar.MONTH, date.getMonth() - 1);
        calendar.set(Calendar.DATE, date.getDay());
        calendar.add(Calendar.DATE, values);
        return calendar.getTime();
    }

    private ModelDate getDisplayDate(ModelMonth month) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, month.getYear());
        calendar.set(Calendar.MONTH, month.getMonth() - 1);
        calendar.set(Calendar.DATE, 1);
        int m = calendar.get(Calendar.DAY_OF_WEEK) - 1;
        calendar.set(Calendar.DATE, -(m + 6));

        return new ModelDate(calendar.getTime());
    }

    public CalendarCellListener getCalendarCellListener() {
        return calendarCellListener;
    }

    public void setCalendarCellListener(CalendarCellListener calendarCellListener) {
        this.calendarCellListener = calendarCellListener;
    }

    public String getMonthYear() {
        return PanelMonth.MONTH[month.getMonth() - 1] + " - " + month.getYear();
    }

    public ModelDate getSelectedDate() {
        return selectedDate;
    }
}
