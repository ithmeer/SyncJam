package syncjam.ui.net;

import syncjam.SongUtilities;
import syncjam.ui.Colors;
import syncjam.ui.buttons.base.ButtonUI;

import javax.swing.*;
import java.awt.*;

/**
 * Created by Marty on 6/12/2016.
 */
public class NetworkPanel extends JPanel
{
    private final ServerList serverList;
    private final JPanel mainPanel;
    private JPanel visiblePanel;

    public NetworkPanel(SongUtilities songUtils)
    {
        mainPanel = new JPanel();
        mainPanel.setBackground(Colors.c_Background1);
        this.add(mainPanel);
        visiblePanel = mainPanel;


        GridBagConstraints constraints = new GridBagConstraints();
        mainPanel.setLayout(new GridBagLayout());

        constraints.gridwidth = 1;

        NetLabel title = new NetLabel("Connection Settings", JLabel.CENTER);
        constraints.gridy = 0;
        constraints.insets = new Insets(8,8,8,8);
        constraints.ipadx = 120;
        constraints.ipady = 30;
        constraints.weightx = 1;
        constraints.weighty = .5;
        mainPanel.add(title, constraints);

        serverList = new ServerList();
        constraints.gridy = 1;
        constraints.insets = new Insets(8,8,8,8);
        constraints.ipadx = 200;
        constraints.ipady = 300;
        constraints.weightx = 1;
        constraints.weighty = .5;
        mainPanel.add(serverList, constraints);

        ButtonUI addButton = new ButtonUI(0, 0, Colors.c_Background2, null) {
            @Override
            protected void clicked() {
                openPanel(new AddServerPanel(NetworkPanel.this));
            }
        };
        addButton.setText("Add");
        addButton.setMargin(new Insets(0,0,0,0));
        constraints.gridy = 2;
        constraints.insets = new Insets(8,8,8,8);
        constraints.ipadx = 200;
        constraints.ipady = 30;
        constraints.weightx = 1;
        constraints.weighty = .5;
        mainPanel.add(addButton, constraints);
    }

    private void openPanel(JPanel panel)
    {
        this.remove(visiblePanel);
        this.add(panel);
        visiblePanel = panel;
        visiblePanel.repaint();
        this.repaint();
    }
    public void back()
    {
        openPanel(mainPanel);
    }

    public void addServer(String ip, int port, String pass)
    {
        serverList.addServer(ip, ip, port, pass);
    }
}
