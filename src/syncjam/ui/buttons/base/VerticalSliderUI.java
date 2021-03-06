package syncjam.ui.buttons.base;

import syncjam.ui.Colors;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

public class VerticalSliderUI extends JPanel implements MouseListener, MouseMotionListener
{
    protected int value = 0;
    private int max = 0;
    private int posOnBar = 0;

    private int barXOffset = 0;
    private int barYOffset = 0;

    private int myW, myH;
    private boolean updateWhileDragging = true;
    private boolean dragging = false;

    public VerticalSliderUI()
    {
        this(0, 100, true);
    }

    public VerticalSliderUI(int maxValue)
    {
        this(0, maxValue, true);
    }

    public VerticalSliderUI(int startValue, int maxValue)
    {
        this(startValue, maxValue, true);
        /*
        startValue is the default _value the slider starts on
         */
    }

    public VerticalSliderUI(int startValue, int maxValue, boolean dragUpdate)
    {
        myW = 20;
        myH = getHeight() - 50;

        this.setMinimumSize(new Dimension(myW, myH));
        this.setMaximumSize(new Dimension(myW, myH));
        setOpaque(false);

        value = startValue;
        posOnBar = value;
        max =   maxValue;

        updateWhileDragging = dragUpdate;

        this.addMouseListener(this);
        this.addMouseMotionListener(this);

        this.validate();
        this.repaint();
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
        value = Math.max(n, 0);
        value = Math.min(value, max);
        if(!dragging) posOnBar = value;
    }
    public int  getValue()      { return value; }

    public void setMaxValue(int n) { max = n; }
    public int  getMaxValue()      { return max; }

    public void paintComponent(Graphics g)
    {
        super.paintComponent(g);

        myH = getHeight() - 50;

        if(value < 0) value = 0;
        if(value > max) value = max;

        barXOffset = 10;
        barYOffset = getHeight()/2 - myH/2;

        g.setColor(Colors.get(Colors.Foreground2));
        g.fillRect(barXOffset,barYOffset,3,myH); //Draw Bar

        if(max != 0)
        {
            if(dragging) g.setColor(Colors.get(Colors.Foreground1));
            int pob = myH - (int)( ( (float)posOnBar / (float)max ) * myH );
            g.fillRect(barXOffset-4, barYOffset+pob-1, 11, 2); //Draw notch on bar, draw Notch on bar

            drawValue(g);
            Colors.setFont(g, 12);
        }
    }

    private void drawValue(Graphics g)
    {
        g.setColor(Colors.get(Colors.Highlight));
        g.drawString("" + posOnBar,
                     barXOffset + 1 - g.getFontMetrics().stringWidth(""+value)/2,
                     barYOffset - 8);
    }

    @Override
    public void mousePressed(MouseEvent e)
    {
        if(e.getY() > barYOffset && e.getY() < barYOffset + myH)
        {
            if(e.getX() > barXOffset + 1 - myW/2 && e.getX() < barXOffset + 1 + myW/2)
            {
                posOnBar = max-((e.getY() - barYOffset) * max / myH);
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
        }

    }

    @Override
    public void mouseDragged(MouseEvent e)
    {
        if(dragging)
        {
            posOnBar = max-((e.getY() - barYOffset) * max / myH);

            if(posOnBar > max) posOnBar = max;
            else if (posOnBar < 0) posOnBar = 0;

            if(updateWhileDragging) setValue(posOnBar);

            repaint();
        }
    }

    public void mouseClicked(MouseEvent e) {}
    public void mouseEntered(MouseEvent e) {}
    public void mouseExited(MouseEvent e) {}
    public void mouseMoved(MouseEvent e) {}
}
