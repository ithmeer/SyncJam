package syncjam.ui.net;

import syncjam.interfaces.NetworkController;
import syncjam.ui.Colors;
import syncjam.ui.base.ItemList;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;

/**
 * Created by Marty on 6/9/2016. lol
 */
public class ServerList extends ItemList<ServerList.ServerItem>
{
    private ServerItem selectedItem = null;
    private ServerItem connectedServer = null;

    public ServerList()
    {
        super();

        setDraggingEnabled(true);
    }

    public void addServer(String name, String ip, int port, String pass)
    {
        ServerItem server = new ServerItem(name, ip, port, pass);
        add(server);
    }

    @Override
    protected void drawItem(ServerItem item, Graphics g, int x, int y)
    {
        super.drawItem(item, g, x, y);

        boolean hovering = itemHoverIndex > -1 && item == getItem(itemHoverIndex);


        g.setColor(Colors.get(Colors.Foreground2));
        if(item == connectedServer)
            g.setColor(Colors.get(Colors.Highlight));
        else if(item == selectedItem || hovering)
            g.setColor(Colors.get(Colors.Foreground1));

        Colors.setFont(g, 14);
        g.drawString(item.getServerName(), x + 8, y + itemHeight/2 - 8);

        Colors.setFont(g, 12);
        g.drawString(item.getIpAddress(),  x + 8, y + itemHeight/2 + 8);
    }

    @Override
    public void mouseClicked(MouseEvent e)
    {
        if(itemHoverIndex > -1)
            selectedItem  = getItem(itemHoverIndex);
    }


    public void connect(NetworkController network)
    {
        ServerItem server = selectedItem;
        network.connectToServer(server.getIpAddress(), server.getPort(), server.getPassword());
        connectedServer = server;
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
    //=====================================

    class ServerItem
    {
        private String serverName = "";
        private String ipAddress  = "";
        private int    port       = 0;
        private String password   = "";

        public ServerItem(String serverName, String ipAddress, int port, String password) {
            this.serverName = serverName;
            this.ipAddress = ipAddress;
            this.port = port;
            this.password = password;
        }

        public ServerItem(String ipAddress, int port, String password) {
            this.serverName = ipAddress;
            this.ipAddress = ipAddress;
            this.port = port;
            this.password = password;
        }

        public String getServerName() { return serverName; }
        public void setServerName(String serverName) { this.serverName = serverName; }

        public String getIpAddress() { return ipAddress; }
        public void setIpAddress(String ipAddress) { this.ipAddress = ipAddress; }

        public int getPort() { return port; }
        public void setPort(int port) { this.port = port; }

        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
    }
}