package syncjam.ui.net;

import syncjam.interfaces.NetworkController;
import syncjam.ui.Colors;

import javax.swing.*;
import java.awt.*;

public class NetworkIndicator extends JPanel
{
    private NetworkController network;
    public NetworkIndicator(NetworkController netCon)
    {
        network = netCon;
    }
    @Override
    protected void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        this.setBackground(Colors.c_Background1);

        g.setColor(Colors.c_Foreground1);
        g.drawOval(6,3,13,13);

        if(!network.isClient())
            g.setColor(Color.green);
        else
            g.setColor(Colors.c_Background2);
        g.fillOval(8,5,9,9);
    }
}
