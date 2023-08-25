package raven.calendar.utils;

import com.formdev.flatlaf.ui.FlatUIUtils;
import com.formdev.flatlaf.util.UIScale;
import java.awt.Graphics2D;
import java.awt.geom.Area;
import java.awt.geom.Rectangle2D;
import raven.calendar.model.ModelDate;

/**
 *
 * @author Raven
 */
public class DefaultCalendarEventCellRender implements CalendarEventCellRender<ModelDate> {

    @Override
    public void paint(Graphics2D g2, Rectangle2D rectangle2D, boolean isSelected, ModelDate value) {
        boolean today = value.isToday();
        if (today) {
            double cellWidth = rectangle2D.getWidth();
            double cellHeight = rectangle2D.getHeight();
            g2.setColor(FlatUIUtils.getUIColor("Calendar.selectionColor", "Component.accentColor"));
            float s1 = UIScale.scale(1f);
            float s2 = UIScale.scale(3f);
            float s3 = UIScale.scale(5f);
            Area area = new Area(new Rectangle2D.Double(s1, s1, cellWidth - s1 * 2, cellHeight - s1 * 2));
            if (isSelected) {
                area.subtract(new Area(new Rectangle2D.Double(s2, s2, cellWidth - s2 * 2, cellHeight - s2 * 2)));
                area.add(new Area(new Rectangle2D.Double(s3, s3, cellWidth - s3 * 2, cellHeight - s3 * 2)));
            }
            g2.fill(area);
        } else if (isSelected) {
            float s1 = UIScale.scale(1f);
            float s2 = UIScale.scale(3f);
            double cellWidth = rectangle2D.getWidth();
            double cellHeight = rectangle2D.getHeight();
            g2.setColor(FlatUIUtils.getUIColor("Calendar.selectionColor", "Component.accentColor"));
            Area area = new Area(new Rectangle2D.Double(s1, s1, cellWidth - s1 * 2, cellHeight - s1 * 2));
            area.subtract(new Area(new Rectangle2D.Double(s2, s2, cellWidth - s2 * 2, cellHeight - s2 * 2)));
            g2.fill(area);
        }
    }
}
