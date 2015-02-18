package syncjam.ui;

import syncjam.base.Mouse;
import syncjam.base.Ticker;
import syncjam.base.Updatable;

import javax.swing.*;
import java.awt.*;


public class SyncJamUI extends JPanel implements Updatable
{
    public WindowObject window = null;
    private InfoUI playerUI = null;
    private ControlUI controlUI = null;
    private PlaylistUI playlistUI = null;
    private Mouse mouse = new Mouse();

    public SyncJamUI()
    {
        window = new WindowObject(this, 350, 600);
        window.addMouseListener(mouse);
        window.addMouseMotionListener(mouse);

        this.setBackground(Colors.c_Background1);

        this.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();

        //this.add(tempPanel(350,20), BorderLayout.NORTH);

        playerUI = new InfoUI();
        c.anchor = GridBagConstraints.FIRST_LINE_START;
        c.weightx = 1.0;
        c.weighty = 0.18;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(16,8,8,8);
        c.ipady = 112;
        c.gridx = 0;
        c.gridy = 0;
        this.add(playerUI, c);

        controlUI = new ControlUI();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1.0;
        c.weighty = 0.1;
        //c.insets = new Insets(4,4,4,4);
        c.ipady = 40;
        c.gridx = 0;
        c.gridy = 1;
        this.add(controlUI, c);

        playlistUI = new PlaylistUI();
        c.fill = GridBagConstraints.BOTH;
        c.weightx = 1.0;
        c.weighty = 1.0;
        //c.insets = new Insets(4,4,4,4);
        c.ipady = 440;
        c.gridx = 0;
        c.gridy = 2;
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
    }

    public void update()
    {
        repaint();
        playerUI.update();
    }
}