package syncjam.ui;

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

    public SyncJamUI()
    {
        window = new WindowObject(360, 500);

        //Window Stuff

        window.addKeyListener(this);

        GridBagConstraints w = new GridBagConstraints();
        window.setLayout(new GridBagLayout());

        w.anchor = GridBagConstraints.PAGE_START;
        w.fill = GridBagConstraints.BOTH;
        w.weightx = 1.0;
        w.weighty = 1.0;
        w.gridx = 0;
        w.gridy = 0;
        window.add(this, w);

        JPanel sidepanel = tempPanel(40,window.getHeight());
        sidepanel.setLayout(new GridBagLayout());
        w.fill = GridBagConstraints.NONE;
        w.weightx = 1.0;
        w.weighty = 1.0;
        w.ipadx = 12;
        w.ipady = 150;
        //w.insets = new Insets(0,0,window.getHeight()-200,0);
        sidepanel.add(new VolumeSlider(50, 100), w);

        //w.anchor = GridBagConstraints.PAGE_START;
        w.fill = GridBagConstraints.VERTICAL;
        w.weightx = 0.0;
        w.weighty = 1.0;
        w.gridx = 1;
        w.gridy = 0;
        window.add(sidepanel, w);

        window.open();

        //Player Section

        this.setBackground(Colors.c_Background1);

        this.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();

        playerUI = new InfoUI();
        c.anchor = GridBagConstraints.PAGE_START;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(8,8,0,8);
        c.weightx = 1.0;
        c.weighty = 0.0;
        c.ipadx = 0;
        c.ipady = 114;
        c.gridx = 0;
        c.gridy = 0;
        this.add(playerUI, c);

        controlUI = new ControlUI();
        c.anchor = GridBagConstraints.PAGE_START;
        c.fill = GridBagConstraints.BOTH;
        c.weightx = 1.0;
        c.weighty = 0.0;
        c.ipadx = 0;
        c.ipady = 0;
        c.gridx = 0;
        c.gridy = 1;
        this.add(controlUI, c);

        songPosition = new SongPositionSlider();
        c.anchor = GridBagConstraints.PAGE_START;
        c.fill = GridBagConstraints.BOTH;
        c.insets = new Insets(0,6,0,6);
        c.weightx = 1.0;
        c.weighty = 0.0;
        c.ipadx = 0;
        c.ipady = 28;
        c.gridx = 0;
        c.gridy = 2;
        this.add(songPosition, c);

        playlistUI = new PlaylistUI();
        c.anchor = GridBagConstraints.PAGE_END;
        c.fill = GridBagConstraints.BOTH;
        c.insets = new Insets(8,8,8,8);
        c.weightx = 1.0;
        c.weighty = 1.0;
        c.ipadx = 0;
        c.ipady = 0;
        c.gridx = 0;
        c.gridy = 3;
        this.add(playlistUI, c);

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