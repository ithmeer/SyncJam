package syncjam.ui;

import syncjam.SongUtilities;
import syncjam.net.SocketNetworkController;
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
    public WindowObject window = null;
    private InfoUI playerUI = null;
    private ControlUI controlUI = null;
    private PlaylistUI playlistUI = null;
    private SliderUI songPosition = null;

    private NetworkPanel networkPanel = null;

    public SyncJamUI(SongUtilities songUtils, SocketNetworkController networkCon)
    {
        window = new WindowObject(360, 500);

        //= = = = = = = = = = Window Stuff = = = = = = = = = =//

        window.addKeyListener(this);

        GridBagConstraints constraints;
        window.setLayout(new GridBagLayout());
        window.setBackground(Colors.c_Background1);
        //window.setUndecorated(true);


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
        window.add(playerPanel, constraints);

        playerPanel.setBackground(Colors.c_Background1);

        //Player Components

        playerUI = new InfoUI(songUtils);
        constraints = setGrid(0, 0, 1.0f, 0.0f, 0, 114);
        constraints.anchor = GridBagConstraints.PAGE_START;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.insets = new Insets(8,8,0,8);

        playerPanel.add(playerUI, constraints);

        controlUI = new ControlUI(songUtils);
        constraints = setGrid(0, 1, 1.0f, 0.0f);
        constraints.anchor = GridBagConstraints.PAGE_START;
        constraints.fill = GridBagConstraints.BOTH;

        playerPanel.add(controlUI, constraints);

        songPosition = new SongPositionSlider(songUtils);
        constraints = setGrid(0, 2, 1.0f, 0.0f, 0, 28);
        constraints.anchor = GridBagConstraints.PAGE_START;
        constraints.fill = GridBagConstraints.BOTH;
        constraints.insets = new Insets(0,6,0,6);

        playerPanel.add(songPosition, constraints);

        playlistUI = new PlaylistUI(songUtils);
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


        networkPanel = new NetworkPanel(networkCon);
        tray.add(networkPanel);
        networkPanel.validate();


        constraints.anchor = GridBagConstraints.EAST;
        constraints = setGrid(2, 0, 0.0f, 1.0f);
        constraints.fill = GridBagConstraints.BOTH;
        window.add(tray, constraints);

        //= = = = = = = = = = Side Bar = = = = = = = = = =//

        JPanel sideBar = new JPanel(new GridBagLayout());
        sideBar.setBackground(Colors.c_Background1);

        constraints = setGrid(1, 0, 0.0f, 1.0f);
        constraints.anchor = GridBagConstraints.PAGE_START;
        constraints.fill = GridBagConstraints.BOTH;
        window.add(sideBar, constraints);

        JPanel sideBarItems = new JPanel(new GridBagLayout());
        sideBarItems.setBackground(Colors.c_Background1);

        constraints.fill = GridBagConstraints.NONE;
        constraints.insets = new Insets(12,0,12,0);
        sideBar.add(sideBarItems, constraints);


        //Components
        constraints.anchor = GridBagConstraints.PAGE_START;

        //Network Indicator
        constraints = setGrid(0, 0, 0.0f, 0.0f, 16, 16);
        NetworkIndicator indicator = new NetworkIndicator(networkCon);
        sideBarItems.add(indicator, constraints);

        //Network Button
        constraints = setGrid(0, 1, 0.0f, 0.0f, 16, 16);
        TextButton networkButton = new TextButton("C", 12, 12, null){
            protected void clicked() { togglePanel(networkPanel); }
        };

        sideBarItems.add(networkButton, constraints);

        //Volume Slider
        constraints = setGrid(0, 2, 0.0f, 0.0f, 12, 150);
        constraints.fill = GridBagConstraints.NONE;

        sideBarItems.add(new VolumeSlider(50, 100, songUtils), constraints);

        //= = = = = = = = = = = = = = = = = = = = = = = =//

        window.open();
        togglePanel(networkPanel);
    }

    public void keyTyped(KeyEvent e) {}

    @Override
    public void keyPressed(KeyEvent e)
    {
        /* placeyholder
        if(e.getKeyCode() == KeyEvent.VK_U)
        {
        }
        */
    }

    @Override
    public void keyReleased(KeyEvent e) {

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
        int pWidth = networkPanel.getWidth();
        Dimension min = window.getMinimumSize();
        if(networkPanel.isVisible())
        {
            window.setSize(window.getWidth() - pWidth, window.getHeight());
            window.setMinimumSize(new Dimension((int)min.getWidth() - pWidth, (int)min.getHeight()));
            networkPanel.setVisible(false);
        }
        else if(!networkPanel.isVisible())
        {
            window.setSize(window.getWidth() + pWidth, window.getHeight());
            window.setMinimumSize(new Dimension((int)min.getWidth() + pWidth, (int)min.getHeight()));
            networkPanel.setVisible(true);
        }
        return networkPanel.isVisible();
    }

    public void repaint()
    {
        window.repaint();
    }
}