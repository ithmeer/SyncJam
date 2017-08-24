package syncjam.ui.buttons.base;

import syncjam.ui.Colors;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class ScrollbarUI extends JPanel implements MouseListener, MouseMotionListener, ComponentListener, MouseWheelListener
{
    private int _myW, _myH;
    private int _inset = 5;
    private int _value = 0;
    private final int _snapspeed = 6;
    private int _rawmax = 100, _max = 100;

    private int pos = 0, length = 0, target = _value;

    private boolean _dragging = false, _scrolling = false;
    private boolean _drawMarker = true;
    private double _marker = -1;
    private int _markedItemNumber = -1;
    private boolean _markerMoved = false;

    public ScrollbarUI()
    {
        _myW = 8;
        _myH = 0;

        this.setOpaque(false);
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
                int move = (_value - target) / _snapspeed + (int)Math.signum(_value - target);
                _value -= move;
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

            //Keep edges of bar within the panel/inset bounds
            int finalPos = pos;
            int finalLength = length;
            if(pos < 0)
            {
                finalPos = 0; //don't let the pos be above the top of the bar
                finalLength = Math.max(4, finalLength+pos);
            }
            else if(pos + length > _myH)
            {
                finalLength = finalLength - ((pos + length) - _myH);
                if(finalLength <= 4) //if the bar is below the bottom of the list, constrain to 4 pixels at bottom
                {
                    finalPos = _myH - 4;
                    finalLength = 4;
                }
            }
            //===============================================

            g.fillRect(_inset, _inset + finalPos, _myW, finalLength);
        }
        else
        {
            _value = 0;
        }
    }

    public void setMarker(int item, int numItems)
    {
        _markerMoved = _markedItemNumber != item;
        _markedItemNumber = item;

        if(item == -1) //item -1 means no marker
            _marker = -1;
        else if(numItems > item) //as long as there are songs
            _marker = (double) item / (double) (numItems - 1);
    }
    public void setDrawMarker(boolean draw) {
        _drawMarker = draw;
    }
    private void drawMarker(Graphics g)
    {
        if(_marker > -1 && _drawMarker) {
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
            dist = 2;
        }
        else if (itemPosition + itemHeight > _value + getHeight()) {
            dist = -(getHeight() - itemHeight) + 11;
        }

        if(dist != -1) setTargetValue(itemPosition + dist);
    }
    public void adjustMarker(int dir) //i don't know how to else make it not think the marker moved when removing an item above it
    {
        _markedItemNumber += (int)Math.signum(dir);
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
                //weird math for getting the _scrollbar _value of the mouse pos
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
            //weird math for getting the _scrollbar _value of the mouse pos
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
            int sc = (e.getUnitsToScroll() * getHeight() / _snapspeed)/2;

            if((_value <= 0 && target + sc < 0) || (_value >= _max && target + sc > _max))
                sc /= 4;

            setTargetValue(target + sc);
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
