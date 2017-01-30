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
public abstract class ItemList<Item> extends JPanel implements MouseListener, MouseMotionListener
{
    private int _myW, _myH;
    protected int _mouseX = -1, _mouseY = -1;
    protected int _clickStartX, _clickStartY;

    protected final int _xOffset = 4, _yOffset = 6;

    protected int itemHeight = 60;

    protected final ScrollbarUI _scrollbar = new ScrollbarUI();

    private int _selectedItem = -1;
    protected int _itemHoverIndex = -1;
    protected int _itemDragIndex = -1;
    protected int _itemDropIndex = -1;

    private boolean _allowDragging = true;
    protected int _lastDropIndex = 0;
    protected ArrayList<Item> items = new ArrayList<>();

    protected int[] _splits;
    private boolean _enableCustomDrawing = false;

    public ItemList()
    {
        this.setLayout(new BorderLayout());
        this.add(_scrollbar, BorderLayout.EAST);

        this.addMouseWheelListener(new MouseWheelListener() {
            @Override
            public void mouseWheelMoved(MouseWheelEvent e)
            {
                _scrollbar.scrollEvent(e);
            }
        });
        this.addMouseListener(this);
        this.addMouseMotionListener(this);
    }
    public ItemList(int w, int h)
    {
        this();
        _myW = 350;
        _myH = 0;
        setMinimumSize(new Dimension(_myW, _myH));
    }
    public ItemList(int itemH)
    {
        itemHeight = itemH;
    }


    public void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        setBackground(Colors.get(Colors.Background2));

        if (_itemDragIndex >= 0)
            scrollNearEdges();
        else
            buildSplitArray();

        if(!_enableCustomDrawing)
        {
            for(int i = 0; i < items.size(); i++)
            {
                if(_itemDragIndex == i)
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
            if(_itemDragIndex != -1)
            {
                int dragY = _mouseY -itemHeight/2;

                ((Graphics2D)g).setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, .5f));
                drawItem(getItem(_itemDragIndex), g, getLeft(), dragY);
                ((Graphics2D)g).setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));

                if(_itemHoverIndex != -1)
                {
                    int hoverItemYPos = getYPosInUI(_itemHoverIndex);
                    if(_mouseY < hoverItemYPos + itemHeight/2)
                        _itemDropIndex = _itemHoverIndex;
                    else if(_mouseY >= hoverItemYPos - itemHeight/2)
                        _itemDropIndex = _itemHoverIndex +1;
                }
                if(_lastDropIndex != _itemDropIndex)
                {
                    _lastDropIndex = _itemDropIndex;
                }
            }
        }
    }

    protected void drawItem(Item i, Graphics g, int x, int y) {
    }

    //====  UTILITY METHODS  ====

    public int getItemHeight() {
        return itemHeight;
    }
    
    protected int getYPosInUI(int i)
    {
        int yValue = getTop() + (i * itemHeight) - _scrollbar.getValue();

        if (_itemDragIndex != -1 && i >= _itemDragIndex)
            yValue -= itemHeight;

        if(_splits.length > i)
            yValue += _splits[i];
        return yValue;
    }

    public int getTop() { return _yOffset; }
    public int getBottom() { return getHeight() - _yOffset; }
    public int getLeft() { return _xOffset; }
    public int getRight() { return getWidth() - _xOffset - _scrollbar.getWidth(); }

    protected void checkHoverIndex(int i)
    {
        Rectangle itemRect = new Rectangle(
                0,
                getYPosInUI(i),
                (getWidth() - _scrollbar.getWidth()),
                itemHeight);

        if(_itemHoverIndex == i && !itemRect.contains(_mouseX, _mouseY))
            _itemHoverIndex = -1;
        else if(_itemDragIndex != i && _mouseY > itemRect.getY() && _mouseY < itemRect.getY()+itemRect.getHeight())
            _itemHoverIndex = i;

        if(_itemDragIndex >= 0)
        {
            if(_mouseY > getHeight() && i < items.size()-1)
                _itemHoverIndex = items.size()-1;
            else if(_mouseY < 0 && i > 0)
                _itemHoverIndex = 0;
        }
    }

    protected void updateSplit(int i)
    {
        if(_splits.length > i) {
            if (_itemDropIndex != -1 && i >= _itemDropIndex)
                _splits[i] = slerp(_splits[i], itemHeight);
            else
                _splits[i] = slerp(_splits[i], 0);
        }
    }

    private void buildSplitArray()
    {
        _splits = new int[items.size()+1];
    }

    private int slerp(int start, int target)
    {
        int t = 6;
        float value = start + (target-start)/t;
        value = Math.abs(Math.round(value));

        return (int)value;
    }

    private void scrollNearEdges()
    {
        int distFromEdge = Math.round(itemHeight*1.5f);

        if(_mouseY > getHeight()-distFromEdge &&
                _scrollbar.getValue() < _scrollbar.getMaxValue())
        {
            int mvSpeed = (distFromEdge-(getHeight()- _mouseY))/2;
            _scrollbar.setTargetValue((_scrollbar.getValue() + Math.max(0, mvSpeed)));
        }
        else if(_mouseY < distFromEdge &&
                _scrollbar.getValue() > 0)
        {
            int mvSpeed = (distFromEdge- _mouseY)/2;
            _scrollbar.setTargetValue((_scrollbar.getValue() - Math.max(0, mvSpeed)));
        }
    }

    protected void setDraggingEnabled(boolean set)     { _allowDragging = set; }
    protected boolean isDraggingEnabled()              { return _allowDragging; }
    protected void setEnableCustomDrawing(boolean set) { _enableCustomDrawing = set; }

    //====  LIST METHODS ====

    protected void updateScrollbar()
    {
        _scrollbar.setMaxValue(items.size() * itemHeight + _yOffset *2);
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

    protected Item getItem(int index) { return items.get(index); }

    protected void setSelectedItem(Item i) {
        _selectedItem = items.indexOf(i);
    }
    public Item getSelectedItem() {
        if(_selectedItem > -1)
            return getItem(_selectedItem);
        else return null;
    }
    public void moveSelection(String dir)
    {
        if(items.size() > 0) {
            int cur = _selectedItem;
            if (dir.equals("up") && cur > -1)
                _selectedItem--;
            else if (dir.equals("down") && cur < items.size()-1)
                _selectedItem++;
        }
    }

    //====  LISTENERS  ====

    @Override
    public void mouseClicked(MouseEvent e){}
    @Override
    public void mousePressed(MouseEvent e)
    {
        if(e.getButton() == MouseEvent.BUTTON1) {
            _clickStartX = e.getX();
            _clickStartY = e.getY();
        }
    }
    @Override
    public void mouseReleased(MouseEvent e)
    {
        if(e.getButton() == MouseEvent.BUTTON1) {
            if (_itemDragIndex >= 0) {
                if (_itemDropIndex == -1)
                    _itemDropIndex = _itemDragIndex;

                if (_itemDragIndex != _itemDropIndex - 1) {
                    Item o = items.remove(_itemDragIndex);
                    if (_itemDropIndex > _itemDragIndex)
                        _itemDropIndex--;
                    items.add(_itemDropIndex, o);
                }
                _splits[_itemDragIndex] = 0;
                _itemDragIndex = -1;
                _itemDropIndex = -1;
                _itemHoverIndex = -1;
                _mouseX = -1;
                _mouseY = -itemHeight;
            }
        }
    }
    @Override
    public void mouseEntered(MouseEvent e){}
    @Override
    public void mouseExited(MouseEvent e)
    {
        _mouseX = -1;
        _mouseY = -1;
    }
    @Override
    public void mouseDragged(MouseEvent e)
    {
        if(SwingUtilities.isLeftMouseButton(e)) {
            _mouseX = e.getX();
            _mouseY = e.getY();
            double dist = Math.hypot(_clickStartX - _mouseX, _clickStartY - _mouseY);
            if (isDraggingEnabled() && dist > 8) {
                if (_itemDragIndex == -1 && _itemHoverIndex != -1) {
                    _itemDragIndex = _itemHoverIndex;
                    for (int i = _itemDragIndex; i < _splits.length - 1; i++) {
                        _splits[i] = itemHeight + 6;
                    }
                }
            }
        }
    }
    @Override
    public void mouseMoved(MouseEvent e)
    {
        _mouseX = e.getX();
        _mouseY = e.getY();
        if(_itemHoverIndex != -1)
            _selectedItem = -1;
    }
}
