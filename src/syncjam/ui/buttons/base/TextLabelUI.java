package syncjam.ui.buttons.base;

import syncjam.ui.Colors;

import javax.swing.*;
import java.awt.*;

public class TextLabelUI extends JLabel
{
    private Colors foreground = Colors.Foreground1;

    public TextLabelUI(String text)
    {
        super(text);
    }
    public TextLabelUI(String text, int horizontalAlignment)
    {
        super(text, horizontalAlignment);
    }

    public void setForeground(Colors fg) {
        foreground = fg;
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        setForeground(Colors.get(foreground));
    }
}
