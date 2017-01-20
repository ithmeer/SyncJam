package syncjam.ui;

import syncjam.ConnectionStatus;
import syncjam.interfaces.NetworkController;
import syncjam.interfaces.ServiceContainer;
import syncjam.ui.base.DialogWindow;
import syncjam.ui.buttons.TextButton;
import syncjam.ui.buttons.VolumeSlider;
import syncjam.ui.buttons.base.SliderUI;
import syncjam.ui.buttons.SongPositionSlider;
import syncjam.ui.net.NetworkIndicator;
import syncjam.ui.net.NetworkPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;


public class SyncJamUI implements KeyListener
{
    public CustomFrame window = null;
    private InfoUI infoUI = null;
    private ControlUI controlUI = null;
    private PlaylistUI playlistUI = null;
    private SliderUI songPosition = null;

    private NetworkPanel networkPanel = null;

    public SyncJamUI(ServiceContainer services)
    {

        //new WindowObject(360, 500, services);
        window = new CustomFrame(340, 500) {
            @Override
            protected void close()
            {
                super.close();
                NetworkController network = services.getService(NetworkController.class);
                if(network != null && network.getStatus() != ConnectionStatus.Unconnected)
                    network.disconnect();
                System.exit(0);
            }
        };
        window.setPreferredSize(new Dimension(360, 620));

        //= = = = = = = = = = Window Stuff = = = = = = = = = =//

        window.addKeyListener(this);
        window.setFocusable(true);

        GridBagConstraints constraints;
        window.getContentPanel().setLayout(new GridBagLayout());
        window.setBackground(Colors.c_Background1);
        //window.setUndecorated(true);
        UIManager.put("Button.disabledText", Colors.c_Background1);


        //= = = = = = = = = = Player = = = = = = = = = =//

        JPanel playerPanel = new JPanel(){
            public void paintComponent(Graphics g)
            {
                super.paintComponent(g);
                ((Graphics2D)g).setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                ((Graphics2D)g).setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            }
        };

        playerPanel.setLayout(new GridBagLayout());
        constraints = setGrid(0, 0, 1.0f, 1.0f);
        constraints.anchor = GridBagConstraints.PAGE_START;
        constraints.fill = GridBagConstraints.BOTH;
        window.getContentPanel().add(playerPanel, constraints);

        playerPanel.setBackground(Colors.c_Background1);

        //Player Components

        infoUI = new InfoUI(services);
        constraints = setGrid(0, 0, 1.0f, 0.0f, 0, 114);
        constraints.anchor = GridBagConstraints.PAGE_START;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.insets = new Insets(8,8,0,8);

        window.cm.registerComponent(infoUI);
        playerPanel.add(infoUI, constraints);

        controlUI = new ControlUI(services);
        constraints = setGrid(0, 1, 1.0f, 0.0f);
        constraints.anchor = GridBagConstraints.PAGE_START;
        constraints.fill = GridBagConstraints.BOTH;

        playerPanel.add(controlUI, constraints);

        songPosition = new SongPositionSlider(services);
        constraints = setGrid(0, 2, 1.0f, 0.0f, 0, 28);
        constraints.anchor = GridBagConstraints.PAGE_START;
        constraints.fill = GridBagConstraints.BOTH;
        constraints.insets = new Insets(0,6,0,6);

        playerPanel.add(songPosition, constraints);

        playlistUI = new PlaylistUI(services);
        constraints = setGrid(0, 3, 1.0f, 1.0f);
        constraints.anchor = GridBagConstraints.PAGE_END;
        constraints.fill = GridBagConstraints.BOTH;
        constraints.insets = new Insets(8,8,8,8);

        playerPanel.add(playlistUI, constraints);

        playerPanel.validate();
        playerPanel.repaint();


        //= = = = = = = = = = Side Panels = = = = = = = = = =//

        JPanel tray = new JPanel();
        tray.setBackground(Colors.c_Background1);


        networkPanel = new NetworkPanel(services);
        tray.add(networkPanel);
        networkPanel.validate();


        constraints.anchor = GridBagConstraints.EAST;
        constraints = setGrid(2, 0, 0.0f, 1.0f);
        constraints.fill = GridBagConstraints.BOTH;
        window.getContentPanel().add(tray, constraints);

        //= = = = = = = = = = Side Bar = = = = = = = = = =//

        JPanel sideBar = new JPanel(new GridBagLayout());
        sideBar.setBackground(Colors.c_Background1);

        constraints = setGrid(1, 0, 0.0f, 1.0f);
        constraints.anchor = GridBagConstraints.PAGE_START;
        constraints.fill = GridBagConstraints.BOTH;
        window.getContentPanel().add(sideBar, constraints);

        JPanel sideBarItems = new JPanel(new GridBagLayout());
        sideBarItems.setBackground(Colors.c_Background1);

        constraints.fill = GridBagConstraints.NONE;
        constraints.insets = new Insets(12,0,12,0);
        sideBar.add(sideBarItems, constraints);


        //Components
        constraints.anchor = GridBagConstraints.PAGE_START;

        //Network Indicator
        constraints = setGrid(0, 0, 0.0f, 0.0f, 16, 16);
        NetworkIndicator indicator = new NetworkIndicator(services);
        sideBarItems.add(indicator, constraints);

        //Network Button
        constraints = setGrid(0, 1, 0.0f, 0.0f, 16, 16);
        TextButton networkButton = new TextButton("C", 12, 12){
            protected void clicked() { togglePanel(networkPanel); }
        };

        sideBarItems.add(networkButton, constraints);

        //Volume Slider
        constraints = setGrid(0, 2, 0.0f, 0.0f, 12, 150);
        constraints.fill = GridBagConstraints.NONE;

        sideBarItems.add(new VolumeSlider(50, 100, services), constraints);

        //= = = = = = = = = = = = = = = = = = = = = = = =//

        window.open();
        networkPanel.setVisible(false);
    }

    public void keyTyped(KeyEvent e) {}

    @Override
    public void keyPressed(KeyEvent e) { }

    @Override
    public void keyReleased(KeyEvent e)
    {
        int key = e.getKeyCode();
        if(key == KeyEvent.VK_U)
            Colors.setColorScheme(Colors.lightColors);
        else if(key == KeyEvent.VK_E)
            DialogWindow.showErrorMessage("BUGS!!!!!", window);
    }

    private GridBagConstraints setGrid(int gridX, int gridY, float weightX, float weightY)
    {
        return setGrid(gridX, gridY, weightX, weightY, 0, 0);
    }
    private GridBagConstraints setGrid(int gridX, int gridY, float weightX, float weightY, int padX, int padY)
    {
        GridBagConstraints gbc = new GridBagConstraints();

        gbc.gridx = gridX;
        gbc.gridy = gridY;
        gbc.weightx = weightX;
        gbc.weighty = weightY;
        gbc.ipadx = padX;
        gbc.ipady = padY;

        return gbc;
    }

    public boolean togglePanel(JPanel panel)
    {
        int pWidth = panel.getWidth();
        Dimension min = window.getMinimumSize();
        if(panel.isVisible())
        {
            window.setMinimumSize(new Dimension((int)min.getWidth() - pWidth, (int)min.getHeight()));
            window.setSize(window.getWidth() - pWidth, window.getHeight());
            panel.setVisible(false);
        }
        else if(!panel.isVisible())
        {
            window.setSize(window.getWidth() + pWidth, window.getHeight());
            window.setMinimumSize(new Dimension((int)min.getWidth() + pWidth, (int)min.getHeight()));
            panel.setVisible(true);
        }
        return panel.isVisible();
    }

    public void repaint()
    {
        window.repaint();
    }
}