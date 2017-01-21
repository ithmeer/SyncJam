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
public class ItemList<Item> extends JPanel implements MouseListener, MouseMotionListener
{
    private int myW, myH;
    protected int mouseX = -1, mouseY = -1;
    protected int clickStartX, clickStartY;

    protected final int xOffset = 4, yOffset = 6;

    protected int itemHeight = 60;

    protected final ScrollbarUI scrollbar = new ScrollbarUI(Colors.Background2);

    protected int itemHoverIndex = -1;
    protected int itemDragIndex = -1;
    protected int itemDropIndex = -1;
    private boolean allowDragging = true;

    protected int lastDropIndex = 0;
    protected ArrayList<Item> items = new ArrayList<>();
    protected int[] splits;

    private boolean enableCustomDrawing = false;

    public ItemList()
    {
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
        setBackground(Colors.get(Colors.Background2));

        if (itemDragIndex >= 0)
            scrollNearEdges();
        else
            buildSplitArray();

        if(!enableCustomDrawing)
        {
            for(int i = 0; i < items.size(); i++)
            {
                if(itemDragIndex == i)
                {
                    continue;
                }

                updateSplit(i);

                int x = getLeft();
                int y = getYPosInUI(i);
                if(y+itemHeight > 0 && y < getHeight())
                {
                    checkHoverIndex(i);
                    drawItem(getItem(i), g, x, y);
                }
            }

            //Draw Dragged Item & Determine Drop Position
            if(itemDragIndex != -1)
            {
                int dragY = mouseY-itemHeight/2;

                ((Graphics2D)g).setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, .5f));
                drawItem(getItem(itemDragIndex), g, getLeft(), dragY);
                ((Graphics2D)g).setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));

                if(itemHoverIndex != -1)
                {
                    int hoverItemYPos = getYPosInUI(itemHoverIndex);
                    if(mouseY < hoverItemYPos + itemHeight/2)
                        itemDropIndex = itemHoverIndex;
                    else if(mouseY >= hoverItemYPos - itemHeight/2)
                        itemDropIndex = itemHoverIndex+1;
                }
                if(lastDropIndex != itemDropIndex)
                {
                    lastDropIndex = itemDropIndex;
                }
            }
        }
    }

    protected void drawItem(Item i, Graphics g, int x, int y) {
    }

    //====  UTILITY METHODS  ====

    protected int getYPosInUI(int i)
    {
        int yValue = getTop() + (i * itemHeight) - scrollbar.getValue();

        if (itemDragIndex != -1 && i >= itemDragIndex)
            yValue -= itemHeight;

        if(splits.length > i)
            yValue += splits[i];
        return yValue;
    }

    public int getTop() { return yOffset; }
    public int getBottom() { return getHeight() - yOffset; }
    public int getLeft() { return xOffset; }
    public int getRight() { return getWidth() - xOffset - scrollbar.getWidth(); }

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
        if(splits.length > i) {
            if (itemDropIndex != -1 && i >= itemDropIndex)
                splits[i] = slerp(splits[i], itemHeight);
            else
                splits[i] = slerp(splits[i], 0);
        }
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
    public void setEnableCustomDrawing(boolean set) { enableCustomDrawing = set; }

    //====  LIST METHODS ====

    protected void updateScrollbar()
    {
        scrollbar.setMaxValue(items.size() * itemHeight + yOffset*2);
    }
    public void add(Item i)
    {
        items.add(i);
        updateScrollbar();
    }
    public void remove(int index)
    {
        items.remove(index);
        updateScrollbar();
    }
    public Item getItem(int index) { return items.get(index); }

    //====  LISTENERS  ====

    @Override
    public void mouseClicked(MouseEvent e){}

    @Override
    public void mousePressed(MouseEvent e)
    {
        clickStartX = e.getX();
        clickStartY = e.getY();
    }

    @Override
    public void mouseReleased(MouseEvent e)
    {
        if(itemDragIndex >= 0)
        {
            if(itemDropIndex == -1)
                itemDropIndex = itemDragIndex;

            if(itemDragIndex != itemDropIndex-1)
            {
                Item o = items.remove(itemDragIndex);
                if (itemDropIndex > itemDragIndex)
                    itemDropIndex--;
                items.add(itemDropIndex, o);
            }
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
        double dist = Math.hypot(clickStartX-mouseX, clickStartY-mouseY);
        if(isDraggingEnabled() && dist > 8)
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
