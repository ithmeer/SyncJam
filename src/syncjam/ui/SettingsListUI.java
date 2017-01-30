package syncjam.ui;

import syncjam.ui.base.ItemList;

import java.awt.*;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;

/**
 * Created by Marty on 1/28/2017.
 * Settings List UI
 */
public class SettingsListUI extends ItemList<SettingsPanel.SettingsItem>
{
    SettingsListUI()
    {
        super();
        setDraggingEnabled(false);
        setOpaque(false);
        itemHeight = 28;
    }
    @Override
    protected void drawItem(SettingsPanel.SettingsItem i, Graphics g, int x, int y) {
        super.drawItem(i, g, x, y);
        g.setColor(Colors.get(Colors.Background2));
        if((_itemHoverIndex == items.indexOf(i) || items.indexOf(getSelectedItem()) == items.indexOf(i)) && i.highlightItem())
            g.fillRect(x, y, getRight(), itemHeight);
        i.draw(g, x, y);
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        super.mouseReleased(e);
        for(KeyListener l : getKeyListeners())
            removeKeyListener(l);
        for(SettingsPanel.SettingsItem i : items)
            if(i instanceof SettingsPanel.SettingsString)
                i.output();
        if(e.getButton() == MouseEvent.BUTTON1 && _itemHoverIndex > -1) {
            getItem(_itemHoverIndex).clicked();
        }
    }
}
