package syncjam.ui.buttons.base;

import syncjam.ui.Colors;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ButtonUI extends JButton implements ActionListener
{
    private int myW, myH;
    private Color background;

    public ButtonUI(int w, int h)
    {
        this(w,h,Colors.c_Background1);
    }
    public ButtonUI(int w, int h, Color bg)
    {
        myW = w;
        myH = h;

        validate();
        addActionListener(this);
        this.setActionCommand("clicked");

        this.setPreferredSize(new Dimension(myW, myH));
        this.setBorderPainted(false);
        this.setFocusPainted(false);
        this.setFocusable(false);
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
        g.fillRect(0,0,getWidth(),getHeight());

        if (getModel().isPressed())
            g.setColor(Colors.c_Highlight);
        else if (getModel().isRollover())
            g.setColor(Colors.c_Foreground1);
        else
            g.setColor(Colors.c_Foreground2);
    }
}
