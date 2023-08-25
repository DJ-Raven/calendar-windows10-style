package raven.calendar;

import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.util.UIScale;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JPanel;

/**
 *
 * @author Raven
 */
public class PanelHeader extends JPanel {

    public PanelHeader() {
        init();
    }

    private void init() {
        putClientProperty(FlatClientProperties.STYLE, ""
                + "border:5,5,5,5;"
                + "background:if($Calendar.background,$Calendar.background,$Panel.background)");
        setLayout(new HeaderLayout());
        buttonDate = new JButton("Date");
        buttonNow = new JButton("Now");

        buttonDate.putClientProperty(FlatClientProperties.STYLE, ""
                + "background:if($Calendar.background,$Calendar.background,$Panel.background);"
                + "borderWidth:0;"
                + "focusWidth:0;"
                + "arc:999");
        buttonNow.putClientProperty(FlatClientProperties.STYLE, ""
                + "background:if($Calendar.background,$Calendar.background,$Panel.background);"
                + "borderWidth:0;"
                + "focusWidth:0;"
                + "arc:999");
        add(buttonDate);
        add(buttonNow);
    }

    public void addEventButtonDate(ActionListener event) {
        buttonDate.addActionListener(event);
    }

    public void addEventButtonNow(ActionListener event) {
        buttonNow.addActionListener(event);
    }

    public void showDate(String text) {
        buttonDate.setText(text);
    }

    private JButton buttonDate;
    private JButton buttonNow;

    private class HeaderLayout implements LayoutManager {

        @Override
        public void addLayoutComponent(String name, Component comp) {
        }

        @Override
        public void removeLayoutComponent(Component comp) {
        }

        @Override
        public Dimension preferredLayoutSize(Container parent) {
            synchronized (parent.getTreeLock()) {
                Insets insets = parent.getInsets();
                int width = insets.left + insets.right + Math.max(UIScale.scale(100), buttonDate.getPreferredSize().width) + buttonDate.getPreferredSize().width;
                int height = insets.top + insets.bottom + Math.max(buttonDate.getPreferredSize().height, buttonNow.getPreferredSize().height);
                return new Dimension(width, height);
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
            Insets insets = parent.getInsets();
            int x = insets.left;
            int y = insets.top;
            int width = parent.getWidth() - (insets.left + insets.right);
            int height = parent.getHeight() - (insets.top + insets.bottom);
            buttonDate.setBounds(x, y, Math.max(UIScale.scale(100), buttonDate.getPreferredSize().width), height);

            int nwidth = buttonNow.getPreferredSize().width;
            buttonNow.setBounds(x + width - nwidth, y, nwidth, height);
        }
    }
}
