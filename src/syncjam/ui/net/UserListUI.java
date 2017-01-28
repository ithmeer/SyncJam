package syncjam.ui.net;

import syncjam.ui.Colors;
import syncjam.ui.base.ItemList;
import syncjam.utilities.UserInfo;

import java.awt.*;
import java.util.Collections;


public class UserListUI extends ItemList<UserInfo>
{
    public UserListUI()
    {
        super();
        setDraggingEnabled(false);
        itemHeight = 24;
    }

    @Override
    public void add(UserInfo i) {
        super.add(i);
        Collections.sort(items);
    }

    @Override
    protected void drawItem(UserInfo i, Graphics g, int x, int y) {
        super.drawItem(i, g, x, y);
        int fontSize = g.getFontMetrics().getHeight();

        drawUserIcon(g, x + 8, y + itemHeight/2);
        g.setColor(i.getUserLevelColor());
        g.drawString(i._userName, x + 24, y + itemHeight - fontSize/2 + 1);
    }

    private void drawUserIcon(Graphics g, int x, int y)
    {
        int size = itemHeight/2;

        g.setColor(Colors.get(Colors.Foreground1));
        g.fillRect(x - size/2, y - size/2, size, size);
    }
}
