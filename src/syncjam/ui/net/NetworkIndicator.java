package syncjam.ui.net;

import syncjam.interfaces.NetworkController;
import syncjam.interfaces.ServiceContainer;
import syncjam.ui.Colors;

import javax.swing.*;
import java.awt.*;

public class NetworkIndicator extends JPanel
{
    private NetworkController _network;
    public NetworkIndicator(ServiceContainer services)
    {
        _network = services.getService(NetworkController.class);
    }
    @Override
    protected void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        this.setBackground(Colors.c_Background1);

        g.setColor(Colors.c_Foreground1);
        g.drawOval(6,3,13,13);

        if(!_network.isClient())
            g.setColor(Color.green);
        else
            g.setColor(Colors.c_Background2);
        g.fillOval(8,5,9,9);
    }
}
