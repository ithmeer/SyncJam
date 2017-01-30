package syncjam.ui.net;

import syncjam.ConnectionStatus;
import syncjam.interfaces.NetworkController;
import syncjam.interfaces.ServiceContainer;
import syncjam.ui.Colors;
import syncjam.ui.UIServices;
import syncjam.ui.buttons.base.ButtonUI;
import syncjam.ui.buttons.base.TextLabelUI;
import syncjam.utilities.ServerInfo;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

/**
 * Created by Marty on 6/12/2016
 */
public class NetworkPanel extends JPanel
{
    private final ServerListUI serverList;
    private final JPanel mainPanel;
    private JPanel visiblePanel;
    private final NetworkController _network;
    private ButtonUI connectButton, disconnectButton, hostButton, addButton, removeButton;

    private KeyAdapter keys = new KeyAdapter() {
        @Override
        public void keyReleased(KeyEvent e) {
            super.keyReleased(e);
            if(visiblePanel == mainPanel && isVisible())
            {
                switch (e.getKeyCode())
                {
                    case KeyEvent.VK_A:
                        addButton.doClick();
                        break;
                    case KeyEvent.VK_R:
                        removeButton.doClick();
                        break;
                    case KeyEvent.VK_ENTER:
                    case KeyEvent.VK_C:
                        connectButton.doClick();
                        break;
                    case KeyEvent.VK_D:
                        disconnectButton.doClick();
                        break;
                    case KeyEvent.VK_H:
                        hostButton.doClick();
                        break;
                    case KeyEvent.VK_UP:
                        serverList.moveSelection("up");
                        break;
                    case KeyEvent.VK_DOWN:
                        serverList.moveSelection("down");
                        break;
                    case KeyEvent.VK_TAB:
                        UIServices.getSyncJamUI().togglePanel(NetworkPanel.this);
                        break;
                }
            }
        }
    };

    @Override
    public void setVisible(boolean aFlag) {
        super.setVisible(aFlag);
        if(aFlag)
            UIServices.getMainWindow().addKeyListener(keys);
        else {
            UIServices.getMainWindow().removeKeyListener(keys);
            if(mainPanel != visiblePanel)
                back();
        }
    }

    public NetworkPanel(ServiceContainer services)
    {
        _network = services.getService(NetworkController.class);
        this.setOpaque(false);

        mainPanel = new JPanel();
        mainPanel.setOpaque(false);
        this.add(mainPanel);
        visiblePanel = mainPanel;

        GridBagConstraints constraints = new GridBagConstraints();
        mainPanel.setLayout(new GridBagLayout());

        constraints.gridwidth = 2;

        TextLabelUI title = new TextLabelUI("Connection Settings", JLabel.CENTER);
        constraints.gridy = 0;
        constraints.insets = new Insets(4,4,4,4);
        constraints.ipadx = 120;
        constraints.ipady = 30;
        constraints.weightx = 1;
        constraints.weighty = .5;
        mainPanel.add(title, constraints);

        serverList = new ServerListUI(services);
        constraints.gridy = 1;
        constraints.insets = new Insets(4,4,8,4);
        constraints.ipadx = 200;
        constraints.ipady = 250;
        constraints.weightx = 1;
        constraints.weighty = .5;
        mainPanel.add(serverList, constraints);

        //----- Add / Remove -----//

        constraints.gridwidth = 1;
        addButton = new ButtonUI(0, 0, Colors.Background2) {
            @Override
            protected void clicked() {
                openPanel(new AddServerPanel(NetworkPanel.this, services));
            }
        };
        addButton.setText("Add");
        addButton.setMargin(new Insets(0,0,0,0));
        constraints.insets = new Insets(4,20,4,4);
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.gridx = 0;
        constraints.gridy = 2;
        constraints.ipadx = 70;
        constraints.ipady = 35;
        constraints.weightx = 1;
        constraints.weighty = .5;
        mainPanel.add(addButton, constraints);

        removeButton = new ButtonUI(0, 0, Colors.Background2) {
            @Override
            protected void clicked() { serverList.removeSelected(); }
        };
        removeButton.setText("Remove");
        removeButton.setMargin(new Insets(0,0,0,0));
        constraints.insets = new Insets(4,4,4,20);
        constraints.gridx = 1;
        constraints.gridy = 2;
        mainPanel.add(removeButton, constraints);

        //----- Connect / Disconnect -----//

        constraints.gridwidth = 1;
        connectButton = new ButtonUI(0, 0, Colors.Background2) {
            @Override
            protected void clicked() { connect(); }
        };
        connectButton.setText("Connect");
        connectButton.setMargin(new Insets(0,0,0,0));
        constraints.insets = new Insets(4,20,4,4);
        constraints.gridx = 0;
        constraints.gridy = 3;
        mainPanel.add(connectButton, constraints);
        connectButton.setEnabled(false);

        disconnectButton = new ButtonUI(0, 0, Colors.Background2) {
            @Override
            protected void clicked() { disconnect(); }
        };
        disconnectButton.setText("Disconnect");
        disconnectButton.setMargin(new Insets(0,0,0,0));
        constraints.insets = new Insets(4,4,4,20);
        constraints.gridx = 1;
        constraints.gridy = 3;
        mainPanel.add(disconnectButton, constraints);
        disconnectButton.setEnabled(false);

        //----- Host  -----//

        constraints.gridwidth = 2;
        hostButton = new ButtonUI(0, 0, Colors.Background2) {
            @Override
            protected void clicked() { openPanel(new HostServerPanel(NetworkPanel.this, services)); }
        };
        hostButton.setText("Host");
        hostButton.setMargin(new Insets(0,0,0,0));
        constraints.insets = new Insets(4,20,4,20);
        constraints.gridx = 0;
        constraints.gridy = 4;
        mainPanel.add(hostButton, constraints);
    }

    private void openPanel(JPanel panel)
    {
        this.remove(visiblePanel);
        this.add(panel);
        visiblePanel = panel;
        visiblePanel.repaint();
        this.updateUI();
        this.repaint();
    }
    void back()
    {
        openPanel(mainPanel);
    }

    void addServer(String name, String ip, int port, String pass)
    {
        serverList.addServer(name, ip, port, pass);
    }

    private void connect()
    {
        serverList.connect(_network);
        statusEnableButtons();
    }
    private void disconnect()
    {
        _network.disconnect();
        serverList.disconnected();
        statusEnableButtons();
    }

    void hostServer(int port, String password)
    {
        _network.startServer(new ServerInfo(port, password));
        statusEnableButtons();
    }

    private void statusEnableButtons()
    {
        ConnectionStatus status = _network.getStatus();
        switch (status)
        {
            case Hosted:
            case Connected:
                connectButton.setEnabled(false);
                disconnectButton.setEnabled(true);
                hostButton.setEnabled(false);
                break;
            case Unconnected:
            case Disconnected:
                connectButton.setEnabled(true);
                disconnectButton.setEnabled(false);
                hostButton.setEnabled(true);
                break;
        }
    }

    public void paintComponent(Graphics g)
    {
        if(serverList.getSelectedItem() == null)
        {
            removeButton.setEnabled(false);
            connectButton.setEnabled(false);
        }
        else
        {
            removeButton.setEnabled(true);
            connectButton.setEnabled(true);
        }
    }
}
