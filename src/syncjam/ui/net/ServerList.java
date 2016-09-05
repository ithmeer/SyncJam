package syncjam.ui.net;

import syncjam.ui.Colors;
import syncjam.ui.base.ItemList;

import javax.swing.*;
import java.awt.*;

/**
 * Created by Marty on 6/9/2016. lol
 */
public class ServerList extends ItemList<ServerList.ServerItem>
{
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

        Colors.setFont(g, 14);
        g.setColor(Colors.c_Foreground1);
        g.drawString(item.getServerName(), x + 8, y + itemHeight/2);
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