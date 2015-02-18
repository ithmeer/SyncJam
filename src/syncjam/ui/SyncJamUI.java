package syncjam.ui;

import syncjam.base.Mouse;
import syncjam.base.Ticker;
import syncjam.base.Updatable;

import javax.swing.*;
import java.awt.*;


public class SyncJamUI extends JPanel implements Updatable
{
    public WindowObject window = null;
    private PlayerUI playerUI = null;
    private Mouse mouse = new Mouse();

    public SyncJamUI()
    {
        window = new WindowObject(this, 350, 600);
        window.addMouseListener(mouse);
        window.addMouseMotionListener(mouse);

        this.setBackground(Colors.c_Background1);
        this.setLayout(new BorderLayout());

        this.add(tempPanel(350,20), BorderLayout.NORTH);

        playerUI = new PlayerUI(350, 200);
        this.add(playerUI, BorderLayout.WEST);

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
    }

    public void update()
    {
        repaint();
        playerUI.update();
    }
}