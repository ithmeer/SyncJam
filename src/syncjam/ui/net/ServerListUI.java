package syncjam.ui.net;

import syncjam.interfaces.NetworkController;
import syncjam.interfaces.ServiceContainer;
import syncjam.interfaces.Settings;
import syncjam.ui.Colors;
import syncjam.ui.base.ItemList;
import syncjam.utilities.ServerInfo;

import java.awt.*;
import java.awt.event.MouseEvent;

/**
 * Created by Marty on 6/9/2016. lol
 */
public class ServerListUI extends ItemList<ServerInfo>
{
    private ServerInfo selectedItem = null;
    private ServerInfo connectedServer = null;
    private final Settings _settings;

    public ServerListUI(ServiceContainer services)
    {
        super();

        setDraggingEnabled(true);

        _settings = services.getService(Settings.class);
        _settings.getSavedServers().forEach(svr -> add(svr));
    }

    public void addServer(String name, String ip, int port, String pass)
    {
        ServerInfo server = new ServerInfo(name, ip, port, pass);
        add(server);
        _settings.saveToDisk();
    }

    @Override
    protected void drawItem(ServerInfo item, Graphics g, int x, int y)
    {
        super.drawItem(item, g, x, y);

        boolean hovering = itemHoverIndex > -1 && item == getItem(itemHoverIndex);


        g.setColor(Colors.get(Colors.Foreground2));
        if(item == connectedServer)
            g.setColor(Colors.get(Colors.Highlight));
        else if(item == selectedItem || hovering) {
            ((Graphics2D)g).setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, .3f));
            g.setColor(Colors.get(Colors.Background1));
            g.fillRect(x+2,y+2, getWidth()-4,itemHeight-4);
            ((Graphics2D)g).setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
            g.setColor(Colors.get(Colors.Foreground1));
        }

        Colors.setFont(g, 14);
        g.drawString(item.serverName, x + 8, y + itemHeight/2 - 4);

        Colors.setFont(g, 12);
        g.drawString(item.ipAddress,  x + 8, y + itemHeight/2 + 12);
    }

    @Override
    public void mouseClicked(MouseEvent e)
    {
        if(itemHoverIndex > -1)
            selectedItem  = getItem(itemHoverIndex);
    }


    public void connect(NetworkController network)
    {
        ServerInfo server = selectedItem;
        network.connectToServer(server);
        connectedServer = server;
    }
    public void disconnected()
    {
        connectedServer = null;
    }

    public int getSelectedItemIndex()
    {
        if(selectedItem != null)
            return items.indexOf(selectedItem);
        return -1;
    }

    public void removeSelected()
    {
        int index = getSelectedItemIndex();
        if(index > -1)
        {
            if(items.size() > 1)
            {
                if (index + 1 < items.size())
                    selectedItem = getItem(index+1);
                else if (index - 1 >= 0)
                    selectedItem = getItem(index-1);
            }
            else selectedItem = null;
            remove(index);
        }
    }

    public void moveSelection(String dir)
    {
        if(items.size() > 0) {
            int cur = getSelectedItemIndex();
            if (cur == -1)
                selectedItem = getItem(0);
            else if (dir.equals("up") && cur-1 > -1)
                selectedItem = getItem(cur-1);
            else if (dir.equals("down") && cur+1 < items.size())
                selectedItem = getItem(cur+1);
        }
    }
    //=====================================
}