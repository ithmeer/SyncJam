package syncjam.ui;

import syncjam.ConnectionStatus;
import syncjam.interfaces.NetworkController;
import syncjam.interfaces.ServiceContainer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;


public class WindowObject extends JFrame
{
    public NetworkController _network;
    public WindowObject(int minW, int minH, ServiceContainer services)
    {
        this.setTitle("SyncJam");
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        this.setUndecorated(false);

        //this.add(panel);
        this.setMinimumSize(new Dimension(minW, minH));
        this.setPreferredSize(new Dimension(minW + 20, minH + 150));
        _network = services.getService(NetworkController.class);

        this.addWindowListener(new WindowAdapter(){
            public void windowClosing(WindowEvent e){
                close();
                System.exit(0);//
            }
        });
    }

    public void open()
    {
        this.pack();
        this.setLocationRelativeTo(null);
        this.setResizable(true);
        this.setVisible(true);
    }

    private void close()
    {
        if(_network.getStatus() != ConnectionStatus.Unconnected)
            _network.disconnect();
    }
}
