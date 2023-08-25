package raven.calendar.utils;

import java.awt.event.MouseEvent;

/**
 *
 * @author Raven
 */
public interface CalendarCellListener {

    public void cellSelected(MouseEvent evet, int index);

    public void scrollChanged();
}
