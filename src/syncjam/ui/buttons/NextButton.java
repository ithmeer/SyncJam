package syncjam.ui.buttons;

import syncjam.ui.buttons.base.ButtonUI;

import java.awt.*;

public class NextButton extends ButtonUI
{
    public NextButton(int w, int h)
    {
        super(w, h);
        setPreferredSize(new Dimension(getW() + 19, getH()));
    }
    public NextButton(int w, int h, Color c)
    {
        super(w, h, c);
        setPreferredSize(new Dimension(getW() + 19, getH()));
    }

    public void clicked() {}

    public void paintComponent(Graphics g)
    {
        super.paintComponent(g);

        g.fillPolygon(arrowShape(getWidth()-getW(),0));
        g.fillPolygon(arrowShape(getWidth()-getW() + getW()/7*2,0));
    }

    public Polygon arrowShape(int x, int y)
    {
        return new Polygon(
                new int[]{x,            x,                  x + getW()/2},
                new int[]{y + getH()/4, y + (getH()/4) * 3, y + getH()/2}, 3);
    }
}