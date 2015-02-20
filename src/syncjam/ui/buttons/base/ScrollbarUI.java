package syncjam.ui.buttons.base;

import syncjam.ui.Colors;

import javax.swing.*;
import javax.swing.plaf.basic.BasicSliderUI;
import java.awt.*;
import java.awt.event.*;

public class ScrollbarUI extends JPanel implements MouseListener, MouseMotionListener, ComponentListener, MouseWheelListener
{
    protected int myW, myH;
    private int inset = 5;
    //private int scrollMultiplier = 501;
    protected int value = 0;
    protected int max = 0;

    private int pos = 0, length = 0, target = value;

    protected boolean dragging = false;

    public ScrollbarUI() { this(Colors.c_Background1); }
    public ScrollbarUI(Color bg)
    {
        myW = 8;
        myH = 0;

        this.setPreferredSize(new Dimension(myW+inset*2, myH));
        this.setBackground(bg);
        this.addMouseListener(this);
        this.addMouseMotionListener(this);
        this.addMouseWheelListener(this);
        this.addComponentListener(this);
    }

    public void setTargetValue(int v)
    {
        target = v;
    }
    public void setValue(int v)
    {
        value = v;
    }
    public int  getValue()      { return value; }

    public void setMaxValue(int n) { max = n; }
    public int  getMaxValue()      { return max; }

    public void paintComponent(Graphics g)
    {
        super.paintComponent(g);

        if(max > getHeight())
        {
            int SNAPSPEED = 4;

            if(!dragging) //allows dragging past the edges for snapback effect
            {
                if(target < 0) target = 0;
                else if(target > max) target = max;
            }

            //smooth bar movement stuff
            if (value < target)
            {
                value = value - (value - target) / SNAPSPEED + 1;
            } else if (value > target)
            {
                value = value - (value - target) / SNAPSPEED - 1;
            }

            if (dragging)
                g.setColor(Colors.c_Foreground1);
            else
                g.setColor(Colors.c_Foreground2);

            length = (int)((float)myH / ((float)max / (float)getHeight())); //a lot of complicated mumbor jumbor
            pos = (int) (((float) value / (float) max) * (myH - length));   //to get the scrool bar length and pos

            g.fillRect(inset, inset + pos, myW, length);
        }
    }

    @Override
    public void mousePressed(MouseEvent e)
    {
        if(e.getX() > 0 && e.getX() < getWidth())
        {
            if(e.getY() > 0 && e.getY() < getHeight())
            {
                dragging = true;
                //math for getting the scrollbar value of the mouse pos
                float  p = (e.getY() * max) / (getHeight()/2);
                setTargetValue((int)p - getHeight());
            }
        }
    }
    public void mouseClicked(MouseEvent e) {}
    @Override
    public void mouseReleased(MouseEvent e)
    {
        dragging = false;
    }
    public void mouseEntered(MouseEvent e) {}
    public void mouseExited(MouseEvent e) {}
    @Override
    public void mouseDragged(MouseEvent e)
    {
        if(dragging)
        {
            //math for getting the scrollbar value of the mouse pos
            float  p = (e.getY() * max) / (getHeight()/2);
            setTargetValue((int) p - getHeight());
        }
    }
    public void mouseMoved(MouseEvent e) {}

    @Override
    public void componentResized(ComponentEvent e)
    {
        //update with JPanel size changes
        myH = (int)getSize().getHeight() - inset*2;
    }
    public void componentMoved(ComponentEvent e) {}
    public void componentShown(ComponentEvent e) {}
    public void componentHidden(ComponentEvent e) {}

    @Override
    public void mouseWheelMoved(MouseWheelEvent e)
    {
        if(e.getScrollType() == MouseWheelEvent.WHEEL_UNIT_SCROLL)
        {
            setTargetValue(value + e.getUnitsToScroll() * 150);
        }
    }
}
