package syncjam.ui;

import syncjam.ConnectionStatus;
import syncjam.interfaces.NetworkController;
import syncjam.interfaces.ServiceContainer;
import syncjam.interfaces.Settings;
import syncjam.ui.base.CustomFrame;
import syncjam.ui.buttons.ImageButton;
import syncjam.ui.buttons.SongPositionSlider;
import syncjam.ui.buttons.TextButton;
import syncjam.ui.buttons.VolumeSlider;
import syncjam.ui.buttons.base.SliderUI;
import syncjam.ui.net.NetworkIndicator;
import syncjam.ui.net.NetworkPanel;
import syncjam.ui.net.UserListPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;


public class SyncJamUI implements KeyListener
{
    private CustomFrame _window;
    private Settings _syncJamSettings;
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
        _syncJamSettings = services.getService(Settings.class);
        _window = new CustomFrame(340, 500) {
            private JFrame _infoWindow = null;
            @Override
            protected void exit()
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

        _window.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        _window.setFocusTraversalKeysEnabled(false);
        _window.addKeyListener(this);
        _window.setFocusable(true);

        GridBagConstraints constraints;
        _window.getContentPanel().setLayout(new GridBagLayout());
        _window.setBackground(Colors.get(Colors.Background1));
        //_window.setUndecorated(true);

        prepareSystemTray();


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
        constraints.fill = GridBagConstraints.HORIZONTAL;

        _window.cm.registerComponent(_controlUI);
        playerPanel.add(_controlUI, constraints);

        //Song Position Slider
        _songPosition = new SongPositionSlider(services);
        constraints = setGrid(0, 2, 1.0f, 0.0f, 0, 28);
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
        constraints.insets = new Insets(12,0,12,0);
        _window.getContentPanel().add(sideBar, constraints);
        _window.cm.registerComponent(sideBar);

        //Sidebar Items Panel
        JPanel sideBarItems = new JPanel(new GridBagLayout()){
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                setBackground(Colors.get(Colors.Background1));
            }
        };

        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.anchor = GridBagConstraints.NORTH;
        constraints.fill = GridBagConstraints.NONE;
        sideBar.add(sideBarItems, constraints);


        //Components
        constraints.anchor = GridBagConstraints.PAGE_START;

        //Network Indicator
        NetworkIndicator indicator = new NetworkIndicator(services);
        constraints = setGrid(0, 0, 0.0f, 0.0f, 16, 16);
        sideBarItems.add(indicator, constraints);

        //Network Button
        ImageButton networkButton = new ImageButton(12, 12, "net_button.png", Colors.Foreground1, Colors.Background2){
            protected void clicked() { togglePanel(_networkPanel); }
        };
        constraints = setGrid(0, 1, 0.0f, 0.0f, 16, 16);
        sideBarItems.add(networkButton, constraints);

        //Volume Slider
        constraints = setGrid(0, 2, 0.0f, 0.0f, 12, 150);
        constraints.fill = GridBagConstraints.NONE;

        sideBarItems.add(new VolumeSlider(50, 100, services), constraints);

        //Clear Playlist Button
        TextButton clearPlaylistButton = new TextButton(11, 11, "", Colors.Background2){
            @Override public void paintComponent(Graphics g) {
                super.paintComponent(g);
                Colors col = getModel().isRollover() ? Colors.Foreground2 : Colors.Foreground1;
                g.setColor(Colors.get(col));
                int cX = getWidth()/2;  //center xPos
                int cY = getHeight()/2; //center yPos
                int xS = 3;             //size of 'X'
                g.drawLine(cX - xS, cY - xS, cX + xS, cY + xS);
                g.drawLine(cX - xS, cY + xS, cX + xS, cY - xS);
            }

            @Override protected void clicked() {
                _playlistUI.clear();
            }
        };
        constraints = setGrid(0, 3, 0.0f, 0.0f, 10, 10);
        sideBarItems.add(clearPlaylistButton, constraints);

        //Add Songs Button
        TextButton addSongsButton = new TextButton(11, 11, "", Colors.Background2){
            @Override public void paintComponent(Graphics g) {
                super.paintComponent(g);
                Colors col = getModel().isRollover() ? Colors.Foreground2 : Colors.Foreground1;
                g.setColor(Colors.get(col));
                int cX = getWidth()/2;  //center xPos
                int cY = getHeight()/2; //center yPos
                int xS = 3;             //size of 'X'
                g.drawLine(cX, cY - xS, cX, cY + xS);
                g.drawLine(cX - xS, cY, cX + xS, cY);
            }
            @Override protected void clicked() {
                super.clicked();
                JFileChooser fileChooser = DialogWindow.openFileChooser();
                fileChooser.addActionListener(new AbstractAction() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        _playlistUI.addAll(fileChooser.getSelectedFiles());
                    }
                });
            }
        };
        constraints = setGrid(0, 4, 0.0f, 0.0f, 10, 10);
        constraints.insets = new Insets(8,0,0,0);
        sideBarItems.add(addSongsButton, constraints);

        //South-Oriented Buttons
        JPanel southSideBarItems = new JPanel(new GridLayout(2, 1, 0, 8));
        southSideBarItems.setOpaque(false);
        southSideBarItems.setPreferredSize(new Dimension(18, 55));
        constraints.anchor = GridBagConstraints.PAGE_END;
        constraints.fill = GridBagConstraints.NONE;
        constraints.gridy = 1;
        sideBar.add(southSideBarItems, constraints);

        //UserList Button
        ImageButton userListButton = new ImageButton(28, 28, "userlist_button2.png", Colors.Foreground1, Colors.Background2){
            protected void clicked() { togglePanel(_userListPanel); }
        };

        //Settings Button
        ImageButton settingsButton = new ImageButton(28, 28, "settings_button.png", Colors.Foreground1, Colors.Background2){
            protected void clicked() { togglePanel(_settingsPanel); }
        };

        /*constraints = setGrid(0, 0, 0.0f, 0.0f, 28, 28);
        constraints.anchor = GridBagConstraints.SOUTH;
        constraints.gridx = 1;
        constraints.gridy = 0;*/
        southSideBarItems.add(userListButton);//, constraints);

        southSideBarItems.add(settingsButton);//, constraints);

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
        _tray.add(_settingsPanel, trayConstraints);
        _settingsPanel.validate();

        _userListPanel = new UserListPanel(services);
        trayConstraints.gridx = 2;
        _tray.add(_userListPanel, trayConstraints);
        _userListPanel.validate();

        constraints.anchor = GridBagConstraints.EAST;
        constraints = setGrid(2, 0, 0.0f, 1.0f);
        constraints.fill = GridBagConstraints.BOTH;
        _window.cm.registerComponent(_tray);
        _window.getContentPanel().add(_tray, constraints);


        //= = = = = = = = = = = = = = = = = = = = = = = =//

        _window.open();
        UIServices.updateLookAndFeel();

        _networkPanel.setVisible(false);
        _settingsPanel.setVisible(false);
        _userListPanel.setVisible(false);

        networkButton.setHeldWithComponentVisible(_networkPanel);
        settingsButton.setHeldWithComponentVisible(_settingsPanel);
        userListButton.setHeldWithComponentVisible(_userListPanel);
    }

    private void prepareSystemTray() {
        if(SystemTray.isSupported())
        {
            SystemTray sysTray = SystemTray.getSystemTray();
            PopupMenu sysTrayMenu = new PopupMenu();


            TrayIcon trayIcon = new TrayIcon(UIServices.loadImage("SJLogo14.png"), "SyncJam", sysTrayMenu);

            ActionListener deIconifyListener = e -> {
                _window.setVisible(true);
                _window.setExtendedState(JFrame.NORMAL);
                sysTray.remove(trayIcon);
            };

            trayIcon.addActionListener(deIconifyListener);

            //System Tray Menu Items
            MenuItem menuItem = new MenuItem("Open");
            menuItem.addActionListener(deIconifyListener);
            sysTrayMenu.add(menuItem);
            //----------------------
            menuItem = new MenuItem("Close");
            menuItem.addActionListener(e -> _window.close());
            sysTrayMenu.add(menuItem);
            //======================

            _window.addWindowStateListener(e -> {
                if(e.getNewState() == 1 && _syncJamSettings.getMinimizeToTray())
                {
                    try {
                        sysTray.add(trayIcon);
                        _window.setVisible(false);
                    } catch (AWTException e1) {
                        System.out.println("Could not add to System Tray.");
                    }
                }
            });
            trayIcon.setImageAutoSize(false);
        }
    }

    @Override public void keyTyped(KeyEvent e) {}

    @Override public void keyPressed(KeyEvent e) { }

    @Override public void keyReleased(KeyEvent e)
    {
        if(!(e.isControlDown() || e.isShiftDown() || e.isAltDown())) {
            switch (e.getKeyCode()) {
                case KeyEvent.VK_SPACE:
                    _controlUI.pressPlayButton();
                    break;
                case KeyEvent.VK_TAB:
                    if (_lastPanel == null) _lastPanel = _networkPanel;
                    if (!_lastPanel.isVisible()) togglePanel(_lastPanel);
                    break;

                case KeyEvent.VK_U:
                    _colorToggle++;
                    switch (_colorToggle %= 4) {
                        case 0:
                            Colors.setColorScheme(Colors.defaultColors);
                            break;
                        case 1:
                            Colors.setColorScheme(Colors.lightColors);
                            break;
                        case 2:
                            Colors.setColorScheme(Colors.blueberry);
                            break;
                        case 3:
                            Colors.setColorScheme(Colors.plum);
                            break;
                    }
                    break;
                case KeyEvent.VK_1:
                    DialogWindow.openColorPicker(Colors.Background1);
                    break;
                case KeyEvent.VK_2:
                    DialogWindow.openColorPicker(Colors.Background2);
                    break;
                case KeyEvent.VK_3:
                    DialogWindow.openColorPicker(Colors.Foreground1);
                    break;
                case KeyEvent.VK_4:
                    DialogWindow.openColorPicker(Colors.Foreground2);
                    break;
                case KeyEvent.VK_5:
                    DialogWindow.openColorPicker(Colors.Highlight);
                    break;
                case KeyEvent.VK_6:
                    DialogWindow.openColorPicker(Colors.Highlight2);
                    break;

                case KeyEvent.VK_T:
                    togglePanel(_userListPanel);
                    break;
                case KeyEvent.VK_S:
                    togglePanel(_settingsPanel);
                    break;
                case KeyEvent.VK_N:
                    togglePanel(_networkPanel);
                    break;
            }
        }
    }

    private GridBagConstraints setGrid(int gridX, int gridY, float weightX, float weightY)
    {
        return setGrid(gridX, gridY, weightX, weightY, -1, -1);
    }
    private GridBagConstraints setGrid(int gridX, int gridY, float weightX, float weightY, int padX, int padY)
    {
        GridBagConstraints gbc = new GridBagConstraints();

        gbc.gridx = gridX;
        gbc.gridy = gridY;
        gbc.weightx = weightX;
        gbc.weighty = weightY;
        if(padX != -1)
            gbc.ipadx = padX;
        if(padY != -1)
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