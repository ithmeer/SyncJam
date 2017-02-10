package syncjam.ui.base;

import syncjam.ui.Colors;

import javax.swing.border.MatteBorder;
import java.awt.*;

/**
 * Created by Marty on 2/7/2017.
 */
public class ColorableMatteBorder extends MatteBorder {
    private final Colors _color;
    public ColorableMatteBorder(int top, int left, int bottom, int right, Colors matteColor) {
        super(top, left, bottom, right, Colors.get(matteColor));
        _color = matteColor;
    }

    @Override
    public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
        color = Colors.get(_color);
        super.paintBorder(c, g, x, y, width, height);
    }
}
