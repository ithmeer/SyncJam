package syncjam.ui.net;

import syncjam.ConnectionStatus;
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


        ConnectionStatus status = _network.getStatus();
        switch(status)
        {
            case Hosted:
                g.setColor(Color.green.brighter());
                break;
            case Connected:
                g.setColor(Color.green);
                break;
            case Intermediate:
                g.setColor(Color.orange);
                break;
            case Disconnected:
                g.setColor(new Color(209, 72, 58));
                break;
            case Unconnected:
                g.setColor(Colors.c_Background2);
                break;
            default:
                g.setColor(Colors.c_Background2);
                break;
        }
        g.fillOval(7,4,11,11);
    }
}
