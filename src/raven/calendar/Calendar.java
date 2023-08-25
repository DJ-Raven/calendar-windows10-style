package raven.calendar;

import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.util.Animator;
import com.formdev.flatlaf.util.CubicBezierEasing;
import com.formdev.flatlaf.util.UIScale;
import java.awt.CardLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.swing.JPanel;
import raven.calendar.model.ModelDate;
import raven.calendar.model.ModelMonth;
import raven.calendar.utils.CalendarCellListener;
import raven.calendar.utils.CalendarEventCellRender;
import raven.calendar.utils.CalendarSelectedListener;
import raven.calendar.utils.DefaultCalendarEventCellRender;

/**
 *
 * @author Raven
 */
public class Calendar extends JPanel {

    private Animator animator;
    private ModelDate date = new ModelDate();
    private int selected = 1;
    private CalendarEventCellRender calendarEventCellRender = new DefaultCalendarEventCellRender();
    private final List<CalendarSelectedListener> events = new ArrayList<>();

    public Calendar() {
        init();
        initAnimator();
    }

    private void initAnimator() {
        animator = new Animator(350, new Animator.TimingTarget() {
            @Override
            public void timingEvent(float fraction) {
                if (selected == 1) {
                    panelDay.scale(0.5f + fraction * 0.5f, fraction);
                    panelMonth.scale(1f + fraction, 1f - fraction);
                } else if (selected == 2) {
                    panelDay.scale(1f - fraction * 0.5f, 1f - fraction);
                    panelMonth.scale(2f - fraction, fraction);
                } else if (selected == 3) {
                    panelMonth.scale(1f - fraction * 0.5f, 1f - fraction);
                    panelYear.scale(2f - fraction, fraction);
                } else if (selected == 4) {
                    panelMonth.scale(0.5f + fraction * 0.5f, fraction);
                    panelYear.scale(1f + fraction, 1f - fraction);
                } else if (selected == 5) {
                    panelDay.scale(0.5f + fraction * 0.5f, fraction);
                    panelYear.scale(1f + fraction, 1f - fraction);
                }

            }

            @Override
            public void begin() {
                if (selected == 1) {
                    panel.setComponentZOrder(panelDay, 0);
                    panelDay.setVisible(true);
                    panelMonth.setVisible(true);
                    panelYear.setVisible(false);
                } else if (selected == 2) {
                    panel.setComponentZOrder(panelMonth, 0);
                    panelDay.setVisible(true);
                    panelMonth.setVisible(true);
                    panelYear.setVisible(false);
                } else if (selected == 3) {
                    panel.setComponentZOrder(panelYear, 0);
                    panelDay.setVisible(false);
                    panelMonth.setVisible(true);
                    panelYear.setVisible(true);
                } else if (selected == 4) {
                    panel.setComponentZOrder(panelMonth, 0);
                    panelDay.setVisible(false);
                    panelMonth.setVisible(true);
                    panelYear.setVisible(true);
                } else if (selected == 5) {
                    panel.setComponentZOrder(panelDay, 0);
                    panelDay.setVisible(true);
                    panelYear.setVisible(true);
                    panelMonth.setVisible(false);
                }
            }

            @Override
            public void end() {
                if (selected == 1) {
                    panelHeader.showDate(panelDay.getMonthYear());
                    panelDay.grabFocus();
                } else if (selected == 2) {
                    panelHeader.showDate(panelMonth.getYear() + "");
                    panelMonth.grabFocus();
                } else if (selected == 3) {
                    panelHeader.showDate(panelYear.getYear());
                    panelYear.grabFocus();
                } else if (selected == 4) {
                    panelHeader.showDate(panelMonth.getYear() + "");
                    panelMonth.grabFocus();
                } else if (selected == 5) {
                    panelHeader.showDate(panelDay.getMonthYear());
                    panelDay.grabFocus();
                    selected = 1;
                }
            }
        });
        animator.setResolution(1);
        animator.setInterpolator(CubicBezierEasing.EASE_OUT);
    }

    private void init() {
        putClientProperty(FlatClientProperties.STYLE, ""
                + "background:if($Calendar.background,$Calendar.background,$Panel.background)");
        setLayout(new CalendarLayout());
        panelHeader = new PanelHeader();
        panel = new JPanel(new CardLayout());
        panelDay = new PanelDay();
        panelMonth = new PanelMonth();
        panelYear = new PanelYear();
        panel.add(panelDay);
        panel.add(panelMonth);
        panel.add(panelYear);

        panelDay.setPanelDateListener((Graphics2D g2, Rectangle2D rectangle, ModelDate e) -> {
            if (calendarEventCellRender != null) {
                boolean active = panelDay.getSelectedDate() != null && e.compareTo(panelDay.getSelectedDate());
                calendarEventCellRender.paint(g2, rectangle, active, e);
                return active;
            }
            return false;
        });
        panelDay.setCalendarCellListener(new CalendarCellListener() {
            @Override
            public void cellSelected(MouseEvent evt, int index) {
                if (index >= 0) {
                    runEventSelected(evt, panelDay.getModels().get(index));
                    repaint();
                }
            }

            @Override
            public void scrollChanged() {
                panelHeader.showDate(panelDay.getMonthYear());
            }

        });
        panelMonth.setCalendarCellListener(new CalendarCellListener() {
            @Override
            public void cellSelected(MouseEvent evt, int index) {
                changeStatus(1);
                panelDay.setMonth(panelMonth.getModels().get(index));
            }

            @Override
            public void scrollChanged() {
                panelHeader.showDate(panelMonth.getYear() + "");
            }
        });
        panelYear.setCalendarCellListener(new CalendarCellListener() {
            @Override
            public void cellSelected(MouseEvent evt, int index) {
                changeStatus(4);
                panelMonth.setYear(panelYear.getModels().get(index));
            }

            @Override
            public void scrollChanged() {
                panelHeader.showDate(panelYear.getYear());
            }
        });
        panelHeader.addEventButtonDate((ActionEvent e) -> {
            if (selected == 1) {
                changeStatus(2);
                panelMonth.setYear(panelDay.getMonth().getYear());
            } else if (selected == 2 || selected == 4) {
                changeStatus(3);
                panelYear.setYear(panelMonth.getYear());
            }
        });
        panelHeader.addEventButtonNow((ActionEvent e) -> {
            now();
        });
        panelHeader.showDate(panelDay.getMonthYear());

        add(panelHeader);
        add(panel);
        panelDay.setVisible(true);
        panelMonth.setVisible(false);
        panelYear.setVisible(false);
        panel.setComponentZOrder(panelDay, 0);
    }

    private void changeStatus(int selected) {
        if (!animator.isRunning()) {
            this.selected = selected;
            animator.start();
        }
    }

    private void initToDay() {
        date = new ModelDate();
        panelDay.setMonth(new ModelMonth(date.getYear(), date.getMonth()));
    }

    private void animateDate() {
        if (selected == 2 || selected == 4) {
            changeStatus(1);
        } else if (selected == 3) {
            changeStatus(5);
        } else {
            panelHeader.showDate(panelDay.getMonthYear());
            repaint();
        }
    }

    public void now() {
        initToDay();
        animateDate();
    }

    public void setSelectedMonth(ModelMonth month) {
        date.setYear(month.getYear());
        date.setMonth(month.getMonth());
        panelDay.setMonth(new ModelMonth(date.getYear(), date.getMonth()));
        animateDate();
    }

    public Date getSelectedDate() {
        ModelDate selectedDate = panelDay.getSelectedDate();
        return selectedDate == null ? null : selectedDate.toDate();
    }

    public CalendarEventCellRender<ModelDate> getCalendarEventCellRender() {
        return calendarEventCellRender;
    }

    public void setCalendarEventCellRender(CalendarEventCellRender<ModelDate> calendarEventCellRender) {
        this.calendarEventCellRender = calendarEventCellRender;
    }

    private void runEventSelected(MouseEvent evt, ModelDate date) {
        for (CalendarSelectedListener event : events) {
            event.selected(evt, date);
        }
    }

    public void addCalendarSelectedListener(CalendarSelectedListener event) {
        events.add(event);
    }

    private PanelHeader panelHeader;
    private JPanel panel;
    private PanelDay panelDay;
    private PanelMonth panelMonth;
    private PanelYear panelYear;

    private class CalendarLayout implements LayoutManager {

        @Override
        public void addLayoutComponent(String name, Component comp) {
        }

        @Override
        public void removeLayoutComponent(Component comp) {
        }

        @Override
        public Dimension preferredLayoutSize(Container parent) {
            synchronized (parent.getTreeLock()) {
                return UIScale.scale(new Dimension(300, 330));
            }
        }

        @Override
        public Dimension minimumLayoutSize(Container parent) {
            synchronized (parent.getTreeLock()) {
                return UIScale.scale(new Dimension(5, 5));
            }
        }

        @Override
        public void layoutContainer(Container parent) {
            synchronized (parent.getTreeLock()) {
                Insets insets = parent.getInsets();
                int x = insets.left;
                int y = insets.top;
                int width = parent.getWidth() - (insets.left + insets.right);
                int height = parent.getHeight() - (insets.top + insets.bottom);
                int headerHeight = panelHeader.getPreferredSize().height;
                panelHeader.setBounds(x, y, width, headerHeight);
                panel.setBounds(x, y + headerHeight, width, height - headerHeight);
            }
        }
    }
}
