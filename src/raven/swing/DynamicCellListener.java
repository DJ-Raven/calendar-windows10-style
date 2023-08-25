package raven.swing;

import java.awt.event.MouseEvent;

/**
 *
 * @author RAVEN
 */
public interface DynamicCellListener {

    public void scrollChanged(boolean scrollNext);

    public void mouseSelected(MouseEvent mouse);
}
