package syncjam.ui;

import syncjam.SongUtilities;
import syncjam.ui.buttons.TextButton;
import syncjam.ui.buttons.VolumeSlider;
import syncjam.ui.buttons.base.SliderUI;
import syncjam.ui.buttons.SongPositionSlider;

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

    private NetworkWindow networkPanel = null;

    public SyncJamUI(SongUtilities songUtils)
    {
        window = new WindowObject(360, 500);

        //= = = = = = = = = = Window Stuff = = = = = = = = = =//

        window.addKeyListener(this);

        GridBagConstraints constraints;
        window.setLayout(new GridBagLayout());
        window.setBackground(Colors.c_Background1);


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


        networkPanel = new NetworkWindow(300,500, songUtils);
        tray.add(networkPanel);
        networkPanel.validate();


        constraints.anchor = GridBagConstraints.EAST;
        constraints = setGrid(2, 0, 0.0f, 1.0f);
        constraints.fill = GridBagConstraints.BOTH;
        window.add(tray, constraints);

        //= = = = = = = = = = Side Bar = = = = = = = = = =//

        JPanel sideBar = tempPanel(40,window.getHeight());
        sideBar.setLayout(new GridBagLayout());

        constraints = setGrid(0, 0, 1.0f, 1.0f, 12, 150);
        constraints.anchor = GridBagConstraints.PAGE_START;
        constraints.fill = GridBagConstraints.NONE;
        sideBar.add(new VolumeSlider(40, 100, songUtils), constraints);
        constraints.gridy = 1;
        TextButton networkButton = new TextButton("C", 36, 36, null){
            protected void clicked()
            {
                togglePanel(networkPanel);
            }
        };
        sideBar.add(networkButton, constraints);

        constraints = setGrid(1, 0, 0.0f, 1.0f);
        constraints.anchor = GridBagConstraints.PAGE_START;
        constraints.fill = GridBagConstraints.VERTICAL;
        window.add(sideBar, constraints);

        //= = = = = = = = = = = = = = = = = = = = = = = =//

        window.open();
        togglePanel(networkPanel);
    }

    public JPanel tempPanel(int w, int h)
    {
        JPanel t = new JPanel();
        t.setMinimumSize(new Dimension(w, h));
        t.setBackground(Colors.c_Background1);
        t.validate();
        t.repaint();
        return t;
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

    public void togglePanel(JPanel panel)
    {
        int pWidth = networkPanel.getWidth();
        Dimension min = window.getMinimumSize();
        if(networkPanel.isVisible())
        {
            window.setSize(window.getWidth() - pWidth, window.getHeight());
            //window.setMinimumSize(new Dimension((int)min.getWidth() - pWidth, (int)min.getHeight()));
            networkPanel.setVisible(false);
        }
        else if(!networkPanel.isVisible())
        {
            window.setSize(window.getWidth() + pWidth, window.getHeight());
            //window.setMinimumSize(new Dimension((int)min.getWidth() + pWidth, (int)min.getHeight()));
            networkPanel.setVisible(true);
        }
    }

    public void repaint()
    {
        window.repaint();
    }
}