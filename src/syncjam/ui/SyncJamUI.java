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

        playerUI = new PlayerUI(8, 24, this.getWidth() - 8, 100);
        //this.add(playerUI);
    }

    public void paintComponent(Graphics g)
    {
        super.paintComponent(g);

        g.setColor(Colors.c_Background1);
        g.fillRect(0, 0, this.getWidth(), this.getHeight());

        //playerUI.draw(g);
    }

    public void update()
    {
        playerUI.update();
        repaint();
    }
}
