package syncjam.ui;

import syncjam.SongUtilities;
import syncjam.SyncJamException;
import syncjam.ui.buttons.NetworkButton;
import syncjam.ui.buttons.base.ButtonUI;

import javax.swing.*;
import java.awt.*;

public class NetworkWindow extends WindowObject
{
    String address = "0.0.0.0";
    String port = "9982";
    String password = "password";
    public NetworkWindow(int width, int height, final SongUtilities songUtils)
    {
        super(width, height);

        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel networkPanel = new JPanel();
        networkPanel.setPreferredSize(new Dimension(200,400));
        networkPanel.setBackground(Colors.c_Background1);

        add(networkPanel);

        GridLayout gl = new GridLayout(4, 2);
        networkPanel.setLayout(gl);

        networkPanel.add(new NetLabel("IP Address"));
        networkPanel.add(new NetTextField(15, address));
        networkPanel.add(new NetLabel("Port"));
        networkPanel.add(new NetTextField(5, port));
        networkPanel.add(new NetLabel("Password"));
        networkPanel.add(new NetTextField(15, password));

        networkPanel.add(new NetButton("Host", songUtils) {
            @Override
            protected void clicked()
            {
                System.out.println(address+"\n"+port+"\n"+password);
                try
                {
                    songUtils.getNetworkController().startServer(Integer.parseInt(port), password);
                }
                catch (SyncJamException e)
                {
                    e.printStackTrace();
                }
            }
        });

        networkPanel.add(new NetButton("Connect", songUtils) {
            @Override
            protected void clicked()
            {
                System.out.println(address+"\n"+port+"\n"+password);
                try
                {
                    songUtils.getNetworkController().connectToServer(address,
                                                                     Integer.parseInt(port),
                                                                     password);
                }
                catch (SyncJamException e)
                {
                    e.printStackTrace();
                }
            }
        });
        repaint();
    }
}

class NetTextField extends TextField
{
    protected NetTextField(int length, String default_text)
    {
        setBackground(Colors.c_Background2);
        setForeground(Colors.c_Foreground1);
        setColumns(length);
        setText(default_text);
    }
}
class NetLabel extends JLabel
{
    protected NetLabel(String text)
    {
        super(text);
        setForeground(Colors.c_Foreground1);
    }
}
class NetButton extends ButtonUI
{
    protected NetButton(String text, SongUtilities songUtils)
    {
        super(0, 0, Colors.c_Background2, songUtils);
        setText(text);
        setMargin(new Insets(0,0,0,0));
    }
    @Override
    protected void clicked()
    {

    }
}