package syncjam.ui;

import syncjam.SongUtilities;
import syncjam.SyncJamException;
import syncjam.ui.buttons.base.ButtonUI;

import javax.swing.*;
import java.awt.*;

public class NetworkWindow extends JPanel
{
    private String defaultPort = "9982";
    private final NetTextField addressField, portField, passField;

    public NetworkWindow(int width, int height, final SongUtilities songUtils)
    {
        super();

        //this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        //JPanel networkPanel = new JPanel();
        this.setPreferredSize(new Dimension(200,400));
        this.setBackground(Colors.c_Background1);

        GridLayout gl = new GridLayout(4, 2);
        this.setLayout(gl);

        addressField = new NetTextField(15, "");
        portField = new NetTextField(5, defaultPort);
        passField = new NetTextField(15, "");

        this.add(new NetLabel("IP Address"));
        this.add(addressField);
        this.add(new NetLabel("Port"));
        this.add(portField);
        this.add(new NetLabel("Password"));
        this.add(passField);

        this.add(new NetButton("Host", songUtils) {
            @Override
            protected void clicked()
            {
                String address = addressField.getText();
                int port = Integer.parseInt(portField.getText());
                String password = passField.getText();

                System.out.println(address+"\n"+port+"\n"+password);
                try
                {
                    songUtils.getNetworkController().startServer(port, password);
                }
                catch (SyncJamException e)
                {
                    e.printStackTrace();
                }
            }
        });

        this.add(new NetButton("Connect", songUtils) {
            @Override
            protected void clicked()
            {
                String address = addressField.getText();
                int port = Integer.parseInt(portField.getText());
                String password = passField.getText();

                System.out.println(address+"\n"+port+"\n"+password);
                try
                {
                    songUtils.getNetworkController().connectToServer(address, port, password);
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