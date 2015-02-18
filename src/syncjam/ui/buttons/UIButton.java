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
    private int myW, myH;
    private Color background;

    public UIButton(int w, int h)
    {
        this(w,h,Colors.c_Background1);
    }
    public UIButton(int w, int h, Color bg)
    {
        myW = w;
        myH = h;

        validate();
        addActionListener(this);
        this.setActionCommand("clicked");

        this.setPreferredSize(new Dimension(myW, myH));
        this.setBorderPainted(false);
        this.setFocusPainted(false);
        background = bg;

    }

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
        g.setColor(background);
        g.fillRect(0,0,getW(),getH());

        if (getModel().isRollover())
            g.setColor(Colors.c_Highlight);
        else
            g.setColor(Colors.c_Foreground2);
    }

    public void update()
    {
        /*if (hovering && Mouse.pressed(0))
        {
            Mouse.releaseAll();
            clicked();
        } */
        repaint();
    }
}
