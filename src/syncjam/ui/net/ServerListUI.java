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
    private ServerInfo connectedServer = null;
    private final Settings _settings;

    ServerListUI(ServiceContainer services)
    {
        super();

        setDraggingEnabled(true);

        _settings = services.getService(Settings.class);
        try
        {
            _settings.getSavedServers().forEach(svr -> add(svr));
        }
        catch(NullPointerException e) { System.out.println("Cannot Load Saved Servers"); }
    }

    void addServer(String name, String ip, int port, String pass)
    {
        ServerInfo server = new ServerInfo(name, ip, port, pass);
        add(server);
        _settings.setSavedServers(items);
    }

    void removeSelected()
    {
        int index = items.indexOf(getSelectedItem());
        if(index > -1)
        {
            if(items.size() > 1)
            {
                if (index + 1 < items.size())
                    moveSelection("down");
                else if (index - 1 >= 0)
                    moveSelection("up");
            }
            remove(index);
            _settings.setSavedServers(items);
        }
    }

    @Override
    protected void drawItem(ServerInfo item, Graphics g, int x, int y)
    {
        super.drawItem(item, g, x, y);

        boolean hovering = _itemHoverIndex > -1 && item == getItem(_itemHoverIndex);


        g.setColor(Colors.get(Colors.Foreground2));
        if(item == connectedServer)
            g.setColor(Colors.get(Colors.Highlight));
        else if(item == getSelectedItem() || hovering) {
            ((Graphics2D)g).setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, .3f));
            g.setColor(Colors.get(Colors.Background1));
            g.fillRect(x+2, y+2, getRight()-4, itemHeight-4);
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
        if(_itemHoverIndex > -1)
            setSelectedItem(getItem(_itemHoverIndex));
    }


    void connect(NetworkController network)
    {
        ServerInfo server = getSelectedItem();
        network.connectToServer(server);
        connectedServer = server;
    }
    void disconnected()
    {
        connectedServer = null;
    }

    //=====================================
}