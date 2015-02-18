package syncjam.ui.buttons;

import syncjam.base.Mouse;
import syncjam.base.Updatable;
import syncjam.ui.Colors;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class UIButton extends JButton implements Updatable, ActionListener
{
    public int myX, myY, myW, myH;
    public boolean hovering = false;

    public UIButton(int x, int y, int w, int h)
    {
        myX = x;
        myY = y;
        myW = w;
        myH = h;

        validate();
        addActionListener(this);
        this.setActionCommand("clicked");
    }

    public int getX() { return myX; }

    public int getY() { return myY; }

    public int getW() { return myW; }

    public int getH() { return myH; }


    @Override
    public void actionPerformed(ActionEvent e)
    {
        if("clicked".equals(e.getActionCommand()))
        {
            clicked();
        }
    }
    public void clicked() {}

    public void paintComponent(Graphics g)
    {
        if (hovering)
            g.setColor(Colors.c_Foreground1);
        else
            g.setColor(Colors.c_Foreground2);
    }

    public void update()
    {
        hovering = Mouse.liesWithin(getX(), getY(), getW(), getH());
        if (hovering && Mouse.pressed(0))
        {
            Mouse.releaseAll();
            clicked();
        }
        repaint();
    }
}
