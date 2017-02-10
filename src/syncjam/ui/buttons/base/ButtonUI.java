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
    protected Colors _background, _foreground, _rollover, _pressed;
    protected boolean _drawOutline = false;

    public ButtonUI(int w, int h)
    {
        this(w, h, Colors.Background1);
    }

    public ButtonUI(int w, int h, Colors bg)
    {
        myW = w;
        myH = h;
        _background = bg;
        _foreground = Colors.Foreground2;
        _rollover = Colors.Foreground1;
        _pressed = Colors.Highlight;

        validate();
        addActionListener(this);
        this.setActionCommand("clicked");

        this.setPreferredSize(new Dimension(myW, myH));
        this.setBorderPainted(false);
        this.setFocusPainted(false);
        this.setFocusable(false);
        this.setUI(new ButtonUIStyle());
        super.setBackground(Colors.get(_background));

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
        super.setBackground(Colors.get(_background));

        if (getModel().isPressed()) {
            g.setColor(Colors.get(_pressed));
            setForeground(Colors.get(_pressed));
        }
        else if (getModel().isRollover()) {
            g.setColor(Colors.get(_rollover));
            setForeground(Colors.get(_rollover));
        }
        else
        {
            g.setColor(Colors.get(_foreground));
            setForeground(Colors.get(_foreground));
        }

        if(_drawOutline) {
            g.setColor(Colors.get(_background).brighter());
            g.drawRect(0, 0, getWidth()-1, getHeight()-1);
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
