package syncjam.ui.buttons.base;

import syncjam.ui.Colors;

import javax.swing.*;
import javax.swing.plaf.metal.MetalButtonUI;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public abstract class ButtonUI extends JButton implements ActionListener
{
    private int myW, myH;
    protected Colors background;

    public ButtonUI(int w, int h)
    {
        this(w, h, Colors.Background1);
    }

    public ButtonUI(int w, int h, Colors bg)
    {
        myW = w;
        myH = h;
        background = bg;

        validate();
        addActionListener(this);
        this.setActionCommand("clicked");

        this.setPreferredSize(new Dimension(myW, myH));
        this.setBorderPainted(false);
        this.setFocusPainted(false);
        this.setFocusable(false);
        this.setUI(new ButtonUIStyle());
        super.setBackground(Colors.get(background));

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
        super.paintComponent(g);
        super.setBackground(Colors.get(background));

        if (getModel().isPressed()) {
            g.setColor(Colors.get(Colors.Highlight));
            setForeground(Colors.get(Colors.Highlight));
        }
        else if (getModel().isRollover()) {
            g.setColor(Colors.get(Colors.Foreground1));
            setForeground(Colors.get(Colors.Foreground1));
        }
        else
        {
            g.setColor(Colors.get(Colors.Foreground2));
            setForeground(Colors.get(Colors.Foreground2));
        }
    }
}
class ButtonUIStyle extends MetalButtonUI
{
    @Override
    public void paintButtonPressed(Graphics g, AbstractButton b) {
        paintText(g, b, b.getBounds(), b.getText());
        g.setColor(Colors.get(Colors.Background1));
        g.fillRect(0, 0, b.getSize().width, b.getSize().height);
    }
}
