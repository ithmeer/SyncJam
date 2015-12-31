package syncjam.ui.buttons.base;

import syncjam.SongUtilities;
import syncjam.ui.Colors;

import javax.swing.*;
import javax.swing.plaf.metal.MetalButtonUI;
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
        this.setUI(new ButtonUIStyle());
        this.setBackground(background);

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
        setBackground(background);
        if(e.getActionCommand().equals("clicked"))
        {
            clicked();
        }
    }

    public void paintComponent(Graphics g)
    {
        setBackground(background);
        super.paintComponent(g);
        //g.setColor(background);
        //g.fillRect(0, 0, getWidth(), getHeight());

        if (getModel().isPressed()) {
            g.setColor(Colors.c_Highlight);
            setForeground(Colors.c_Highlight);
        }
        else if (getModel().isRollover()) {
            g.setColor(Colors.c_Foreground1);
            setForeground(Colors.c_Foreground1);
        }
        else {
            g.setColor(Colors.c_Foreground2);
            setForeground(Colors.c_Foreground2);
        }
    }
}
class ButtonUIStyle extends MetalButtonUI
{
    @Override
    public void paintButtonPressed(Graphics g, AbstractButton b) {
        paintText(g, b, b.getBounds(), b.getText());
        g.setColor(Colors.c_Background1);
        g.fillRect(0, 0, b.getSize().width, b.getSize().height);
    }
}
