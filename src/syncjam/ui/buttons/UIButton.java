package syncjam.ui.buttons;

import syncjam.base.Mouse;
import syncjam.base.Updatable;
import syncjam.ui.Colors;

import java.awt.*;

public class UIButton implements Updatable
{
    public int myX, myY, myW, myH;
    public boolean hovering = false;

    public UIButton(int x, int y, int w, int h)
    {
        myX = x;
        myY = y;
        myW = w;
        myH = h;
    }

    public int getX() { return myX; }

    public int getY() { return myY; }

    public int getW() { return myW; }

    public int getH() { return myH; }

    public void clicked()
    {
    }

    public void draw(Graphics g)
    {
        if (hovering)
            g.setColor(Colors.c_Foreground1);
        else
            g.setColor(Colors.c_Foreground2);
    }

    public void update()
    {
        hovering = Mouse.liesWithin(myX, myY, myW, myH);
        if (hovering && Mouse.pressed(0))
        {
            Mouse.releaseAll();
            clicked();
        }
    }
}
