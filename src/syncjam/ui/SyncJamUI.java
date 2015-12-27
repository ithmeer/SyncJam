package syncjam.ui;

import syncjam.Playlist;
import syncjam.SongUtilities;
import syncjam.ui.buttons.NetworkButton;
import syncjam.ui.buttons.VolumeSlider;
import syncjam.ui.buttons.base.SliderUI;
import syncjam.ui.buttons.SongPositionSlider;
import syncjam.ui.buttons.base.VerticalSliderUI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;


public class SyncJamUI extends JPanel implements KeyListener
{
    public WindowObject window = null;
    private InfoUI playerUI = null;
    private ControlUI controlUI = null;
    private PlaylistUI playlistUI = null;
    private SliderUI songPosition = null;

    public SyncJamUI(SongUtilities songUtils)
    {
        window = new WindowObject(360, 500);

        //Window Stuff

        window.addKeyListener(this);

        GridBagConstraints windowConstraints = new GridBagConstraints();
        window.setLayout(new GridBagLayout());

        windowConstraints.anchor = GridBagConstraints.PAGE_START;
        windowConstraints.fill = GridBagConstraints.BOTH;
        windowConstraints.weightx = 1.0;
        windowConstraints.weighty = 1.0;
        windowConstraints.gridx = 0;
        windowConstraints.gridy = 0;
        window.add(this, windowConstraints);

        JPanel sidepanel = tempPanel(40,window.getHeight());
        sidepanel.setLayout(new GridBagLayout());
        windowConstraints.fill = GridBagConstraints.NONE;
        windowConstraints.weightx = 1.0;
        windowConstraints.weighty = 1.0;
        windowConstraints.ipadx = 12;
        windowConstraints.ipady = 150;
        //w.insets = new Insets(0,0,window.getHeight()-200,0);
        sidepanel.add(new VolumeSlider(50, 100, songUtils), windowConstraints);
        windowConstraints.gridy = 1;
        sidepanel.add(new NetworkButton(36, 36, songUtils), windowConstraints);

        //w.anchor = GridBagConstraints.PAGE_START;
        windowConstraints.fill = GridBagConstraints.VERTICAL;
        windowConstraints.weightx = 0.0;
        windowConstraints.weighty = 1.0;
        windowConstraints.gridx = 1;
        windowConstraints.gridy = 0;
        window.add(sidepanel, windowConstraints);

        window.open();

        //Player Section

        this.setBackground(Colors.c_Background1);

        this.setLayout(new GridBagLayout());
        GridBagConstraints controlConstraints = new GridBagConstraints();

        playerUI = new InfoUI(songUtils);
        controlConstraints.anchor = GridBagConstraints.PAGE_START;
        controlConstraints.fill = GridBagConstraints.HORIZONTAL;
        controlConstraints.insets = new Insets(8,8,0,8);
        controlConstraints.weightx = 1.0;
        controlConstraints.weighty = 0.0;
        controlConstraints.ipadx = 0;
        controlConstraints.ipady = 114;
        controlConstraints.gridx = 0;
        controlConstraints.gridy = 0;
        this.add(playerUI, controlConstraints);

        controlUI = new ControlUI(songUtils);
        controlConstraints.anchor = GridBagConstraints.PAGE_START;
        controlConstraints.fill = GridBagConstraints.BOTH;
        controlConstraints.weightx = 1.0;
        controlConstraints.weighty = 0.0;
        controlConstraints.ipadx = 0;
        controlConstraints.ipady = 0;
        controlConstraints.gridx = 0;
        controlConstraints.gridy = 1;
        this.add(controlUI, controlConstraints);

        songPosition = new SongPositionSlider(songUtils);
        controlConstraints.anchor = GridBagConstraints.PAGE_START;
        controlConstraints.fill = GridBagConstraints.BOTH;
        controlConstraints.insets = new Insets(0,6,0,6);
        controlConstraints.weightx = 1.0;
        controlConstraints.weighty = 0.0;
        controlConstraints.ipadx = 0;
        controlConstraints.ipady = 28;
        controlConstraints.gridx = 0;
        controlConstraints.gridy = 2;
        this.add(songPosition, controlConstraints);

        playlistUI = new PlaylistUI(songUtils);
        controlConstraints.anchor = GridBagConstraints.PAGE_END;
        controlConstraints.fill = GridBagConstraints.BOTH;
        controlConstraints.insets = new Insets(8,8,8,8);
        controlConstraints.weightx = 1.0;
        controlConstraints.weighty = 1.0;
        controlConstraints.ipadx = 0;
        controlConstraints.ipady = 0;
        controlConstraints.gridx = 0;
        controlConstraints.gridy = 3;
        this.add(playlistUI, controlConstraints);

        validate();
        repaint();
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

    public void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        ((Graphics2D)g).setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        ((Graphics2D)g).setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
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
}