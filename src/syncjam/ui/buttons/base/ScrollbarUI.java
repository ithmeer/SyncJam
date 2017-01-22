package syncjam.ui.buttons.base;

import syncjam.ui.Colors;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class ScrollbarUI extends JPanel implements MouseListener, MouseMotionListener, ComponentListener, MouseWheelListener
{
    protected int myW, myH;
    private final Colors background;
    private int inset = 5;
    protected int value = 0;
    protected final int snapspeed = 8;
    protected int rawmax = 100, max = 100;

    private int pos = 0, length = 0, target = value;

    protected boolean dragging = false, scrolling = false;
    private double marker = -1;

    public ScrollbarUI() { this(Colors.Background1); }

    public ScrollbarUI(Colors bg)
    {
        myW = 8;
        myH = 0;

        background = bg;
        this.setPreferredSize(new Dimension(myW+inset*2, myH));
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
        target = v;
    }

    public int getValue() { return value; }

    public void setMaxValue(int n)
    {
        rawmax = n;
        max = rawmax - getHeight();
    }

    public int getMaxValue() { return max; }

    public void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        setBackground(Colors.get(background));

        double viewRatio = (float)getHeight() / (float)( rawmax );

        if(viewRatio < 1)
        {
            drawMarker(g);
            //System.out.println("" + myH + " - " + max + " - " + length);

            //only turn off scrolling when half a scroll off the edge of list
            if(scrolling && value < target / 4 || value-max > (target-max) / 4)
                scrolling = false;

            //smooth bar movement
            if (value != target)
            {
                value -= (value - target) / snapspeed + (int)Math.signum(value - target);
            }

            //allows scrolling past the edges for snapback effect
            if(!dragging && !scrolling)
            {
                if(target < 0)
                    target = 0;
                else if(target > max)
                    target = max;
            }

            if (dragging)
            {
                if (value < 0)
                    value = (value / 4);
                else if (value > max)
                    value = max + (value - max) / 4;

                g.setColor(Colors.get(Colors.Foreground1));
            }
            else
                g.setColor(Colors.get(Colors.Foreground2));

            length = Math.max(20, (int)(myH * viewRatio));

            pos = (int) (((float) value / (float) max) * (myH - length));   //to get the scroll bar length and pos
            g.fillRect(inset, inset + pos, myW, length);
        }
        else
        {
            value = 0;
        }
    }

    public void setMarker(int item, int numItems)
    {
        if(item == -1) //item -1 means no marker
            marker = -1;
        else if(numItems > item) //as long as there are songs
            marker = (double)item/(double)(numItems-1);
    }
    public void drawMarker(Graphics g)
    {
        if(marker > -1) {
            double viewRatio = (float)getHeight() / (float)( rawmax );
            g.setColor(Colors.get(Colors.Highlight));
            g.fillRect(inset, inset + (int)(marker * (myH-inset)), myW, 4);
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
                //weird math for getting the scrollbar value of the mouse pos
                float  p = (e.getY() - length/2) * max / (myH - length);
                setTargetValue((int) p);
            }
        }
    }
    @Override
    public void mouseReleased(MouseEvent e)
    {
        dragging = false;

        if (target < 0)
            target = value;
        else if (target > max)
            target = value;
    }
    @Override
    public void mouseDragged(MouseEvent e)
    {
        if(dragging)
        {
            //weird math for getting the scrollbar value of the mouse pos
            float  p = (e.getY() - length/2) * max / (myH - length);
            setTargetValue((int) p);
        }
    }

    @Override
    public void componentResized(ComponentEvent e)
    {
        //update with JPanel size changes
        int pu = myH; //pre-update value
        myH = (int)getSize().getHeight() - inset*2;
        max = rawmax - getHeight();

        int temp = (pu-myH); //the change in size, positive or negative (i think they're reversed? idk)

        while(value > 0 && temp != 0) //Makes resizing make sense!
        {
            if(temp < 0)
            {
                setValue(value-1); //as the window gets larger, the position shifts down
                temp += 1;
            }
            else if(temp > 0)
            {
                setValue(value+1); //as the window gets smaller, the position shifts up
                temp -= 1;
            }
        } //these do not take effect if the scroll position is at the top, this is good!
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e)
    {
        scrollEvent(e);
    }

    public void scrollEvent(MouseWheelEvent e)
    {
        if (e.getScrollType() == MouseWheelEvent.WHEEL_UNIT_SCROLL) {
            int sc = target + e.getUnitsToScroll() * getHeight() / snapspeed;

            setTargetValue(sc);
            scrolling = true;
        }
    }

    public void mouseClicked(MouseEvent e) {}
    public void mouseEntered(MouseEvent e) {}
    public void mouseExited(MouseEvent e) {}
    public void mouseMoved(MouseEvent e) {}
    public void componentMoved(ComponentEvent e) {}
    public void componentShown(ComponentEvent e) {}
    public void componentHidden(ComponentEvent e) {}
}
