package syncjam.ui.buttons.base;

import syncjam.SongUtilities;
import syncjam.ui.Colors;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public abstract class ButtonUI extends JButton implements ActionListener
{
    private int myW, myH;
    private Color background;
    protected final SongUtilities songUtilities;

    public ButtonUI(int w, int h, SongUtilities utils)
    {
        this(w, h, Colors.c_Background1, utils);
    }

    public ButtonUI(int w, int h, Color bg, SongUtilities utils)
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
        songUtilities = utils;
    }

    public int getW() { return myW; }

    public int getH() { return myH; }

    /**
     * Action to perform when clicked.
     */
    protected abstract void clicked();

    @Override
    public void actionPerformed(ActionEvent e)
    {
        if(e.getActionCommand().equals("clicked"))
        {
            clicked();
        }
    }

    public void paintComponent(Graphics g)
    {
        g.setColor(background);
        g.fillRect(0, 0, getWidth(), getHeight());

        if (getModel().isPressed())
            g.setColor(Colors.c_Highlight);
        else if (getModel().isRollover())
            g.setColor(Colors.c_Foreground1);
        else
            g.setColor(Colors.c_Foreground2);
    }
}
