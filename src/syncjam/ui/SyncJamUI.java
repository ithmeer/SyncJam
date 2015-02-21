package syncjam.ui;

import syncjam.base.Updatable;
import syncjam.ui.buttons.base.SliderUI;
import syncjam.ui.buttons.SongPositionSlider;

import javax.swing.*;
import java.awt.*;


public class SyncJamUI extends JPanel
{
    public WindowObject window = null;
    private InfoUI playerUI = null;
    private ControlUI controlUI = null;
    private PlaylistUI playlistUI = null;
    private SliderUI songPosition = null;

    public SyncJamUI()
    {
        window = new WindowObject(this, 360, 500);

        this.setBackground(Colors.c_Background1);

        this.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();

        playerUI = new InfoUI();
        c.anchor = GridBagConstraints.PAGE_START;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(8,8,8,8);
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
        t.setBackground(Colors.c_Background2);
        return t;
    }

    public void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        ((Graphics2D)g).setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        ((Graphics2D)g).setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
    }
}