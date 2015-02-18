package syncjam.ui;

import syncjam.base.Updatable;
import syncjam.ui.buttons.PlayButton;
import syncjam.ui.buttons.UIButton;

import javax.swing.*;
import java.awt.*;

public class PlaylistUI extends JPanel implements Updatable
{
    private int myW, myH;

    public PlaylistUI()
    {
        myW = 350;
        myH = 440;

        setMinimumSize(new Dimension(myW, myH));
        setBackground(Colors.c_Background2);
        this.setLayout(new BorderLayout());
    }
    public void paintComponent(Graphics g)
    {
        super.paintComponent(g);
    }
    @Override
    public void update()
    {
        //playButton.update();
    }
}
