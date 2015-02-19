package syncjam.ui.buttons;

import java.awt.*;

public class NextButton extends UIButton
{
    public NextButton(int w, int h) { super(w, h); }
    public NextButton(int w, int h, Color c)
    {
        super(w, h, c);
    }

    public void clicked() {}

    public void paintComponent(Graphics g)
    {
        super.paintComponent(g);

        g.fillPolygon(arrowShape(0,0));
        g.fillPolygon(arrowShape(getW()/7*2,0));
    }

    public Polygon arrowShape(int x, int y)
    {
        Polygon shape = new Polygon(
                new int[]{x,            x,                  x + getW()/2},
                new int[]{y + getH()/4, y + (getH()/4) * 3, y + getH()/2}, 3);
        return shape;
    }

    public void update()
    {
        super.update();
        repaint();
    }
}