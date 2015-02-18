package syncjam.ui;

import syncjam.base.Updatable;
import syncjam.ui.buttons.PlayButton;
import syncjam.ui.buttons.UIButton;

import javax.swing.*;
import java.awt.*;

public class ControlUI extends JPanel implements Updatable
{
    private int myW, myH;
    private UIButton playButton;

    public ControlUI()
    {
        myW = 350;
        myH = 60;

        setMinimumSize(new Dimension(myW, myH));
        setBackground(Colors.c_Background1);

        this.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();

        playButton = new PlayButton(35, 35);
        c.fill = GridBagConstraints.CENTER;
        c.gridx = 0;
        c.gridy = 1;
        c.weightx = 1.0;
        c.weighty = 1.0;
        this.add(playButton, c);
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
