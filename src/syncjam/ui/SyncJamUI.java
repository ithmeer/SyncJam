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
    private SliderBarUI songPosition = null;
    private Mouse mouse = new Mouse();

    public SyncJamUI()
    {
        window = new WindowObject(this, 380, 600);
        window.addMouseListener(mouse);
        window.addMouseMotionListener(mouse);

        this.setBackground(Colors.c_Background1);

        this.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();

        //this.add(tempPanel(350,20), BorderLayout.NORTH);


        controlUI = new ControlUI();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1.0;
        c.weighty = 0.02;
        c.gridx = 0;
        c.gridy = 1;
        this.add(controlUI, c);

        playerUI = new InfoUI();
        c.anchor = GridBagConstraints.FIRST_LINE_START;
        c.weightx = 1.0;
        c.weighty = 0.18;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(3,3,3,3);
        c.ipady = 114;
        c.gridx = 0;
        c.gridy = 0;
        this.add(playerUI, c);

        songPosition = new SliderBarUI(350, 40);
        c.fill = GridBagConstraints.BOTH;
        c.gridx = 0;
        c.gridy = 2;
        c.ipadx = 250;
        c.ipady = 16;
        c.weightx = 1.0;
        c.weighty = 0.0;
        this.add(songPosition, c);

        playlistUI = new PlaylistUI();
        c.fill = GridBagConstraints.BOTH;
        c.weightx = 1.0;
        c.weighty = 1.0;
        c.ipady = 420;
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
    }

    public void update()
    {
        repaint();
        playerUI.update();
    }
}