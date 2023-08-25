package raven.calendar.utils;

import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;

/**
 *
 * @author Raven
 * @param <E>
 */
public interface CalendarEventCellRender<E> {

    public void paint(Graphics2D g2, Rectangle2D rectangle2D, boolean isSelected, E value);
}
