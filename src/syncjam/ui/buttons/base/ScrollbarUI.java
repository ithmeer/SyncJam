package syncjam.ui.buttons.base;

import syncjam.ui.Colors;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class ScrollbarUI extends JPanel implements MouseListener, MouseMotionListener, ComponentListener, MouseWheelListener
{
    protected int _myW, _myH;
    private final Colors _background;
    private int _inset = 5;
    protected int _value = 0;
    protected final int _snapspeed = 6;
    protected int _rawmax = 100, _max = 100;

    private int pos = 0, length = 0, target = _value;

                protected boolean _dragging = false, _scrolling = false;
    private double _marker = -1;
    private int _markedItemNumber = -1;
    private boolean _markerMoved = false;

    public ScrollbarUI() { this(Colors.Background1); }

    public ScrollbarUI(Colors bg)
    {
        _myW = 8;
        _myH = 0;

        _background = bg;
        this.setPreferredSize(new Dimension(_myW + _inset *2, _myH));
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
        _value = v;
        target = v;
    }

    public int getValue() { return _value; }

    public void setMaxValue(int n)
    {
        _rawmax = n;
        _max = _rawmax - getHeight();
    }

    public int getMaxValue() { return _max; }

    public void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        setBackground(Colors.get(_background));

        double viewRatio = (float)getHeight() / (float)(_rawmax);

        if(viewRatio < 1)
        {
            drawMarker(g);

            //only turn off scrolling when half a scroll off the edge of list
            if(_scrolling && _value < target / 4 || _value - _max > (target- _max) / 4)
                _scrolling = false;

            //smooth bar movement
            if (_value != target)
            {
                _value -= (_value - target) / _snapspeed + (int)Math.signum(_value - target);
            }

            //allows scrolling past the edges for snapback effect
            if(!_dragging && !_scrolling)
            {
                if(target < 0)
                    target = 0;
                else if(target > _max)
                    target = _max;
            }

            if (_dragging)
            {
                if (_value < 0)
                    _value = (_value / 4);
                else if (_value > _max)
                    _value = _max + (_value - _max) / 4;

                g.setColor(Colors.get(Colors.Foreground1));
            }
            else
                g.setColor(Colors.get(Colors.Foreground2));

            length = Math.max(20, (int)(_myH * viewRatio));

            pos = (int) (((float) _value / (float) _max) * (_myH - length));   //to get the scroll bar length and pos
            g.fillRect(_inset, _inset + pos, _myW, length);
        }
        else
        {
            _value = 0;
        }
    }

    public void setMarker(int item, int numItems)
    {
        if(_markedItemNumber != item)
            _markerMoved = true;
        else
            _markerMoved = false;
        _markedItemNumber = item;

        if(item == -1) //item -1 means no marker
            _marker = -1;
        else if(numItems > item) //as long as there are songs
            _marker = (double) item / (double) (numItems - 1);
    }
    public void drawMarker(Graphics g)
    {
        if(_marker > -1) {
            double viewRatio = (float)getHeight() / (float)(_rawmax);
            g.setColor(Colors.get(Colors.Highlight));
            g.fillRect(_inset, _inset + (int)(_marker * (_myH - _inset)), _myW, 4);
        }
    }
    public void moveToItem(int item, int itemHeight)
    {
        int itemPosition = item*itemHeight;

        int dist = -1;
        if (itemPosition < _value) {
            dist = 0;
        }
        else if (itemPosition + itemHeight > _value + getHeight()) {
            dist = -(getHeight() - itemHeight) + 11;
        }

        if(dist != -1) setTargetValue(itemPosition + dist);
    }
    public void adjustMarker() //i don't know how to else make it not think the marker moved when removing an item above it
    {
        _markedItemNumber--;
    }
    public void moveToMarker(){
        if(_markerMoved) {
            setTargetValue((int) (_marker * _max));
            _markerMoved = false;
        }
    }
    public boolean is_markerMoved()
    {
        return _markerMoved;
    }

    @Override
    public void mousePressed(MouseEvent e)
    {
        if(e.getX() > 0 && e.getX() < getWidth())
        {
            if(e.getY() > 0 && e.getY() < getHeight())
            {
                _dragging = true;
                //weird math for getting the scrollbar _value of the mouse pos
                float  p = (e.getY() - length/2) * _max / (_myH - length);
                setTargetValue((int) p);
            }
        }
    }
    @Override
    public void mouseReleased(MouseEvent e)
    {
        _dragging = false;

        if (target < 0)
            target = _value;
        else if (target > _max)
            target = _value;
    }
    @Override
    public void mouseDragged(MouseEvent e)
    {
        if(_dragging)
        {
            //weird math for getting the scrollbar _value of the mouse pos
            float  p = (e.getY() - length/2) * _max / (_myH - length);
            setTargetValue((int) p);
        }
    }

    @Override
    public void componentResized(ComponentEvent e)
    {
        //update with JPanel size changes
        int pu = _myH; //pre-update _value
        _myH = (int)getSize().getHeight() - _inset *2;
        _max = _rawmax - getHeight();

        int temp = (pu- _myH); //the change in size, positive or negative (i think they're reversed? idk)

        while(_value > 0 && temp != 0) //Makes resizing make sense!
        {
            if(temp < 0)
            {
                setValue(_value -1); //as the window gets larger, the position shifts down
                temp += 1;
            }
            else if(temp > 0)
            {
                setValue(_value +1); //as the window gets smaller, the position shifts up
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
            int sc = target + e.getUnitsToScroll() * getHeight() / _snapspeed;

            setTargetValue(sc);
            _scrolling = true;
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
