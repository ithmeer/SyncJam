package syncjam.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

public class SliderBarUI extends JPanel implements MouseListener, MouseMotionListener
{
    protected float value = 0;
    protected float max = 100;
    protected float posOnBar = 0;

    protected int barXOffset = 0;
    protected int barYOffset = 0;

    private int myW, myH;
    private int mouseX = 0, mouseY = 0;
    private boolean updateWhileDragging = true;
    private boolean dragging = false;

    public SliderBarUI(int length, int startValue)
    {
        myW = length;
        myH = 20;

        value = startValue;

        this.setPreferredSize(new Dimension(myW, myH));
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

    public void setW(int w) { myW = w; }

    public void paintComponent(Graphics g)
    {
        super.paintComponent(g);

        setW(getWidth() - 50);
        if(value < 0) value = 0;
        if(value > max) value = max;

        barXOffset = getWidth()/2 - getW()/2;
        barYOffset = 16;
        g.setColor(Colors.c_Foreground2);
        g.fillRect(barXOffset,barYOffset,getW(),4);

        int pob = (int)((posOnBar/max)*getW());
        g.fillRect(barXOffset+pob, barYOffset-4, 3, 10);

        Colors.setFont(g,12);
        g.setColor(Colors.c_Highlight);
        g.drawString(""+(int)posOnBar,8+barXOffset,8);
    }

    @Override
    public void mouseClicked(MouseEvent e) {}

    @Override
    public void mousePressed(MouseEvent e)
    {
        if(mouseY > barYOffset-4 && mouseY < barYOffset+10)
        {
            if(mouseX > barXOffset && mouseX < barXOffset+getW())
            {
                posOnBar = (mouseX-barXOffset)*max/getW();
                dragging = true;
            }
        }
    }

    @Override
    public void mouseReleased(MouseEvent e)
    {
        if(dragging)
        {
            dragging = false;
            value = posOnBar;
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
            mouseX = e.getX();
            mouseY = e.getY();
            posOnBar = (mouseX-barXOffset)*max/getW();

            if(posOnBar > max) posOnBar = max;
            else if (posOnBar < 0) posOnBar = 0;

            if(updateWhileDragging) value = posOnBar;
        }
    }

    @Override
    public void mouseMoved(MouseEvent e)
    {
        mouseX = e.getX();
        mouseY = e.getY();
    }
}
