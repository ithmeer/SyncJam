package syncjam.ui.base;

import syncjam.ui.Colors;
import syncjam.ui.buttons.base.ScrollbarUI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

/**
 * Created by Marty on 6/7/2016
 */
public class ItemList extends JPanel implements MouseListener, MouseMotionListener
{
    private int myW, myH;
    protected int mouseX = -1, mouseY = -1;

    protected final int xOffset = 4, yOffset = 6;

    protected int itemHeight = 60;

    protected final ScrollbarUI scrollbar = new ScrollbarUI(Colors.c_Background2);

    protected int itemHoverIndex = -1;
    protected int itemDragIndex = -1;
    protected int itemDropIndex = -1;
    private boolean allowDragging = true;

    protected int lastDropIndex = 0;
    protected ArrayList<Object> items = new ArrayList<Object>();
    protected int[] splits;

    public ItemList()
    {

        setBackground(Colors.c_Background2);
        this.setLayout(new BorderLayout());
        this.add(scrollbar, BorderLayout.EAST);

        this.addMouseWheelListener(new MouseWheelListener() {
            @Override
            public void mouseWheelMoved(MouseWheelEvent e)
            {
                scrollbar.scrollEvent(e);
            }
        });
        this.addMouseListener(this);
        this.addMouseMotionListener(this);
    }
    public ItemList(int w, int h)
    {
        this();
        myW = 350;
        myH = 0;
        setMinimumSize(new Dimension(myW, myH));
    }
    public ItemList(int itemH)
    {
        itemHeight = itemH;
    }


    public void paintComponent(Graphics g)
    {
        super.paintComponent(g);

        if (itemDragIndex >= 0)
            scrollNearEdges();
    }

    //====  UTILITY METHODS  ====

    protected int getYPosInUI(int i)
    {
        int yValue = yOffset + (i * itemHeight) - scrollbar.getValue();

        if (itemDragIndex != -1 && i >= itemDragIndex)
            yValue -= itemHeight;

        return yValue + splits[i];
    }

    protected void checkHoverIndex(int i)
    {
        Rectangle itemRect = new Rectangle(
                0,
                getYPosInUI(i),
                (getWidth() - scrollbar.getWidth()),
                itemHeight);

        if(itemHoverIndex == i && !itemRect.contains(mouseX,mouseY))
            itemHoverIndex = -1;
        else if(itemDragIndex != i && mouseY > itemRect.getY() && mouseY < itemRect.getY()+itemRect.getHeight())
            itemHoverIndex = i;

        if(itemDragIndex >= 0)
        {
            if(mouseY > getHeight() && i < items.size()-1)
                itemHoverIndex = items.size()-1;
            else if(mouseY < 0 && i > 0)
                itemHoverIndex = 0;
        }
    }

    protected void updateSplit(int i)
    {
        if (itemDropIndex != -1 && i >= itemDropIndex)
            splits[i] = slerp(splits[i], itemHeight);
        else
            splits[i] = slerp(splits[i], 0);
    }

    private void buildSplitArray()
    {
        splits = new int[items.size()+1];
    }

    protected int slerp(int start, int target)
    {
        int t = 6;
        float value = start + (target-start)/t;
        value = Math.abs(Math.round(value));

        return (int)value;
    }

    protected void scrollNearEdges()
    {
        int distFromEdge = Math.round(itemHeight*1.5f);

        if(mouseY > getHeight()-distFromEdge &&
                scrollbar.getValue() < scrollbar.getMaxValue())
        {
            int mvSpeed = (distFromEdge-(getHeight()-mouseY))/2;
            scrollbar.setTargetValue((scrollbar.getValue() + Math.max(0, mvSpeed)));
        }
        else if(mouseY < distFromEdge &&
                scrollbar.getValue() > 0)
        {
            int mvSpeed = (distFromEdge-mouseY)/2;
            scrollbar.setTargetValue((scrollbar.getValue() - Math.max(0, mvSpeed)));
        }
    }

    protected void setDraggingEnabled(boolean set)  { allowDragging = set; }
    public boolean isDraggingEnabled()              { return allowDragging; }

    //====  LISTENERS  ====

    @Override
    public void mouseClicked(MouseEvent e) {}

    @Override
    public void mousePressed(MouseEvent e){}

    @Override
    public void mouseReleased(MouseEvent e)
    {
        if(itemDragIndex >= 0)
        {
            if(itemDropIndex == -1)
                itemDropIndex = itemDragIndex;

            if(itemDragIndex != itemDropIndex-1)
            {
                Object o = items.remove(itemDragIndex);
                items.add(itemDropIndex, o);
            }
            buildSplitArray();
            splits[itemDragIndex] = 0;
            itemDragIndex  = -1;
            itemDropIndex  = -1;
            itemHoverIndex = -1;
            mouseX = -1;
            mouseY = -itemHeight;
        }
    }

    @Override
    public void mouseEntered(MouseEvent e){}

    @Override
    public void mouseExited(MouseEvent e)
    {
        mouseX = -1;
        mouseY = -1;
    }

    @Override
    public void mouseDragged(MouseEvent e)
    {
        mouseX = e.getX();
        mouseY = e.getY();
        if(isDraggingEnabled())
        {
            if(itemDragIndex == -1 && itemHoverIndex != -1)
            {
                itemDragIndex = itemHoverIndex;
                for(int i = itemDragIndex; i < splits.length-1; i++)
                {
                    splits[i] = itemHeight+6;
                }
            }
        }
    }

    @Override
    public void mouseMoved(MouseEvent e)
    {
        mouseX = e.getX();
        mouseY = e.getY();
    }
}
