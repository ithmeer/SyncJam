package syncjam.ui;

import syncjam.ConnectionStatus;
import syncjam.interfaces.NetworkController;
import syncjam.interfaces.ServiceContainer;
import syncjam.interfaces.Settings;
import syncjam.ui.base.CustomFrame;
import syncjam.ui.buttons.SongPositionSlider;
import syncjam.ui.buttons.TextButton;
import syncjam.ui.buttons.VolumeSlider;
import syncjam.ui.buttons.base.SliderUI;
import syncjam.ui.net.NetworkIndicator;
import syncjam.ui.net.NetworkPanel;
import syncjam.ui.net.UserListPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;


public class SyncJamUI implements KeyListener
{
    private CustomFrame _window;
    private final InfoUI _infoUI;
    private final ControlUI _controlUI;
    private final PlaylistUI _playlistUI;
    private final SliderUI _songPosition;
    private final JPanel _tray;

    private final SettingsPanel _settingsPanel;
    private final NetworkPanel _networkPanel;
    private final UserListPanel _userListPanel;
    private JPanel _lastPanel = null;
    private int _colorToggle = 0;

    public SyncJamUI(ServiceContainer services)
    {

        //new WindowObject(360, 500, services);
        _window = new CustomFrame(340, 500) {
            private JFrame _infoWindow = null;
            @Override
            protected void close()
            {
                NetworkController network = services.getService(NetworkController.class);
                if(network.getStatus() != ConnectionStatus.Unconnected && network.getStatus() != ConnectionStatus.Disconnected)
                    network.disconnect();
                services.getService(Settings.class).saveToDisk();
            }

            @Override
            protected void clickedInfoButton() {
                if(_infoWindow == null || !_infoWindow.isValid())
                    _infoWindow = DialogWindow.showErrorMessage("<About not yet implemented>", "SyncJam"); //TODO: About SyncJam
            }
        };
        _window.setPreferredSize(new Dimension(360, 620));
        UIServices.setMainWindow(_window);
        UIServices.setSyncJamUI(this);

        //= = = = = = = = = = Window Stuff = = = = = = = = = =//

        _window.setFocusTraversalKeysEnabled(false);
        _window.addKeyListener(this);
        _window.setFocusable(true);

        GridBagConstraints constraints;
        _window.getContentPanel().setLayout(new GridBagLayout());
        _window.setBackground(Colors.get(Colors.Background1));
        //_window.setUndecorated(true);


        //= = = = = = = = = = Player = = = = = = = = = =//

        JPanel playerPanel = new JPanel(){
            public void paintComponent(Graphics g)
            {
                super.paintComponent(g);
                setBackground(Colors.get(Colors.Background1));
                ((Graphics2D)g).setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                ((Graphics2D)g).setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            }
        };

        playerPanel.setLayout(new GridBagLayout());
        constraints = setGrid(0, 0, 1.0f, 1.0f);
        constraints.anchor = GridBagConstraints.PAGE_START;
        constraints.fill = GridBagConstraints.BOTH;
        _window.getContentPanel().add(playerPanel, constraints);

        //Player Components

        //InfoUI
        _infoUI = new InfoUI(services);
        constraints = setGrid(0, 0, 1.0f, 0.0f, 0, 114);
        constraints.anchor = GridBagConstraints.PAGE_START;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.insets = new Insets(8,8,0,8);

        _window.cm.registerComponent(_infoUI);
        playerPanel.add(_infoUI, constraints);

        //ControlUI
        _controlUI = new ControlUI(services);
        constraints = setGrid(0, 1, 1.0f, 0.0f);
        constraints.anchor = GridBagConstraints.PAGE_START;
        constraints.fill = GridBagConstraints.HORIZONTAL;

        _window.cm.registerComponent(_controlUI);
        playerPanel.add(_controlUI, constraints);

        //Song Position Slider
        _songPosition = new SongPositionSlider(services);
        constraints = setGrid(0, 2, 1.0f, 0.0f, 0, 28);
        constraints.anchor = GridBagConstraints.PAGE_START;
        constraints.fill = GridBagConstraints.BOTH;
        constraints.insets = new Insets(0,6,0,6);

        playerPanel.add(_songPosition, constraints);

        //PlaylistUI
        _playlistUI = new PlaylistUI(services);
        constraints = setGrid(0, 3, 1.0f, 1.0f);
        constraints.anchor = GridBagConstraints.PAGE_END;
        constraints.fill = GridBagConstraints.BOTH;
        constraints.insets = new Insets(8,8,8,8);

        playerPanel.add(_playlistUI, constraints);

        playerPanel.validate();
        playerPanel.repaint();

        //= = = = = = = = = = Side Bar = = = = = = = = = =//

        JPanel sideBar = new JPanel(new GridBagLayout()){
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                setBackground(Colors.get(Colors.Background1));
            }
        };

        constraints = setGrid(1, 0, 0.0f, 1.0f);
        constraints.anchor = GridBagConstraints.PAGE_START;
        constraints.fill = GridBagConstraints.BOTH;
        _window.cm.registerComponent(sideBar);
        _window.getContentPanel().add(sideBar, constraints);

        JPanel sideBarItems = new JPanel(new GridBagLayout()){
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                setBackground(Colors.get(Colors.Background1));
            }
        };

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
            protected void clicked() { togglePanel(_networkPanel); }
        };

        sideBarItems.add(networkButton, constraints);

        //Volume Slider
        constraints = setGrid(0, 2, 0.0f, 0.0f, 12, 150);
        constraints.fill = GridBagConstraints.NONE;

        sideBarItems.add(new VolumeSlider(50, 100, services), constraints);

        //= = = = = = = = = = Side Panels = = = = = = = = = =//

        _tray = new JPanel(new GridBagLayout()){
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                setBackground(Colors.get(Colors.Background1));
            }
        };
        GridBagConstraints trayConstraints = new GridBagConstraints();
        trayConstraints.anchor = GridBagConstraints.SOUTH;
        trayConstraints.fill = GridBagConstraints.VERTICAL;
        trayConstraints.weightx = 1.0;
        trayConstraints.weighty = 1.0;

        _networkPanel = new NetworkPanel(services);
        trayConstraints.gridx = 0;
        _tray.add(_networkPanel, trayConstraints);
        _networkPanel.validate();

        _settingsPanel = new SettingsPanel(services);
        trayConstraints.gridx = 1;
        trayConstraints.insets = new Insets(8,8,8,8);
        _tray.add(_settingsPanel, trayConstraints);
        _settingsPanel.validate();

        _userListPanel = new UserListPanel(services);
        trayConstraints.gridx = 2;
        trayConstraints.insets = new Insets(8,8,8,8);
        _tray.add(_userListPanel, trayConstraints);
        _userListPanel.validate();

        constraints.anchor = GridBagConstraints.EAST;
        constraints = setGrid(2, 0, 0.0f, 1.0f);
        constraints.fill = GridBagConstraints.BOTH;
        _window.cm.registerComponent(_tray);
        _window.getContentPanel().add(_tray, constraints);


        //= = = = = = = = = = = = = = = = = = = = = = = =//

        _window.open();

        _networkPanel.setVisible(false);
        _settingsPanel.setVisible(false);
        _userListPanel.setVisible(false);
    }

    public void keyTyped(KeyEvent e) {}

    @Override
    public void keyPressed(KeyEvent e) { }

    @Override
    public void keyReleased(KeyEvent e)
    {
        switch(e.getKeyCode())
        {
            case KeyEvent.VK_SPACE:
                _controlUI.pressPlayButton();
                break;
            case KeyEvent.VK_TAB:
                if(_lastPanel == null) _lastPanel = _networkPanel;
                if(!_lastPanel.isVisible()) togglePanel(_lastPanel);
                break;
            case KeyEvent.VK_U:
                _colorToggle++;
                switch(_colorToggle % 3) {
                    case 0: Colors.setColorScheme(Colors.defaultColors); break;
                    case 1: Colors.setColorScheme(Colors.lightColors); break;
                    case 2: Colors.setColorScheme(Colors.test); break;
                }
                break;
            case KeyEvent.VK_E:
                DialogWindow.showErrorMessage("SUUUUUPER BUUGGGGGSSSS!!!!! \nHELP THE BUGS????\nAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
                break;
            case KeyEvent.VK_T:
                togglePanel(_userListPanel);
                break;
            case KeyEvent.VK_S:
                togglePanel(_settingsPanel);
                break;
        }
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
        if(_lastPanel != null && _lastPanel != panel) closePanel(_lastPanel);
        _lastPanel = panel;
        int pWidth = (int)panel.getPreferredSize().getWidth();
        Dimension min = _window.getMinimumSize();
        if(panel.isVisible())
        {
            closePanel(panel);
        }
        else if(!panel.isVisible())
        {
            _window.setSize(_window.getWidth() + pWidth, _window.getHeight());
            _window.setMinimumSize(new Dimension((int)min.getWidth() + pWidth, (int)min.getHeight()));
            panel.setVisible(true);
            panel.repaint();
        }
        return panel.isVisible();
    }

    private void closePanel(JPanel panel)
    {
        int pWidth = (int)panel.getPreferredSize().getWidth();
        Dimension min = _window.getMinimumSize();
        if(panel.isVisible())
        {
            _window.setMinimumSize(new Dimension((int)min.getWidth() - pWidth, (int)min.getHeight()));
            _window.setSize(_window.getWidth() - pWidth, _window.getHeight());
            panel.setVisible(false);
        }
    }


    public void repaint()
    {
        _window.repaint();
    }
}