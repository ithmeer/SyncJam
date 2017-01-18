package syncjam.ui.buttons.base;

import syncjam.ui.Colors;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

public abstract class SliderUI extends JPanel implements MouseListener, MouseMotionListener
{
    protected int value = 0;
    protected int max = 0;
    protected int posOnBar = 0;

    protected int barXOffset = 0;
    protected int barYOffset = 0;

    private int myW, myH;
    private boolean updateWhileDragging = true;
    private boolean dragging = false;

    public SliderUI()
    {
        this(0, 100, true);
    }

    public SliderUI(int maxValue)
    {
        this(0, maxValue, true);
    }

    public SliderUI(int startValue, int maxValue)
    {
        this(startValue, maxValue, true);
    }

    public SliderUI(int startValue, int maxValue, boolean dragUpdate)
    {
        myW = getWidth() - 50;
        myH = 20;

        this.setMinimumSize(new Dimension(myW, myH));
        this.setMaximumSize(new Dimension(myW, myH));

        value = startValue;
        max =   maxValue;

        updateWhileDragging = dragUpdate;

        this.setBackground(Colors.c_Background1);
        this.addMouseListener(this);
        this.addMouseMotionListener(this);
    }

    public void setUpdateWhileDragging(boolean u)
    {
        updateWhileDragging = u;
    }

    public int getW() { return myW; }

    public int getH() { return myH; }

    public int  getPosOnBar()   { return posOnBar; }

    public void setValue(int n)
    {
        value = n;
        if(!dragging) posOnBar = value;
    }
    public int  getValue()      { return value; }

    public void setMaxValue(int n) { max = n; }
    public int  getMaxValue()      { return max; }

    public void paintComponent(Graphics g)
    {
        super.paintComponent(g);

        myW = getWidth() - 50;

        if(value < 0) value = 0;
        if(value > max) value = max;

        barXOffset = getWidth()/2 - myW/2;
        barYOffset = 21;

        g.setColor(Colors.c_Foreground2);
        g.fillRect(barXOffset,barYOffset,myW,3); //Draw Bar

        if(max != 0)
        {
            if(dragging) g.setColor(Colors.c_Foreground1);
            int pob = (int)( ( (float)posOnBar / (float)max ) * myW );
            g.fillRect(barXOffset+pob-1, barYOffset-4, 2, 11); //Draw notch on bar, draw Notch on bar

            drawValue(g);
            Colors.setFont(g, 12);
        }
    }

    protected void drawValue(Graphics g)
    {
        g.setColor(Colors.c_Highlight);
        g.drawString(""+(int)posOnBar,barXOffset+4,barYOffset-10);
    }

    @Override
    public void mouseClicked(MouseEvent e) {}

    @Override
    public void mousePressed(MouseEvent e)
    {
        if(e.getY() > barYOffset + 1 - myH/2 && e.getY() < barYOffset + 1 + myH)
        {
            if(e.getX() > barXOffset && e.getX() < barXOffset+myW)
            {
                posOnBar = (e.getX() - barXOffset) * max / myW;
                dragging = true;

                repaint();
            }
        }
    }

    @Override
    public void mouseReleased(MouseEvent e)
    {
        if(dragging)
        {
            dragging = false;
            setValue(posOnBar);
            //value = posOnBar;
        }

    }

    @Override
    public void mouseEntered(MouseEvent e) {}

    @Override
    public void mouseExited(MouseEvent e) {}

    @Override
    public void mouseDragged(MouseEvent e)
    {
        if(dragging)
        {
            posOnBar = (e.getX() - barXOffset) * max/myW;

            if(posOnBar > max) posOnBar = max;
            else if (posOnBar < 0) posOnBar = 0;

            if(updateWhileDragging) value = posOnBar;

            repaint();
        }
    }

    @Override
    public void mouseMoved(MouseEvent e) {}
}
