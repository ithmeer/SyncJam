package syncjam.ui;

import syncjam.base.Updatable;
import syncjam.ui.buttons.NextButton;
import syncjam.ui.buttons.PlayButton;
import syncjam.ui.buttons.PrevButton;
import syncjam.ui.buttons.UIButton;

import javax.swing.*;
import java.awt.*;

public class ControlUI extends JPanel implements Updatable
{
    private int myW, myH;
    private UIButton playButton, prevButton, nexButton;

    public ControlUI()
    {
        myW = 380;
        myH = 90;

        setMinimumSize(new Dimension(myW, myH));
        setBackground(Colors.c_Background1);

        this.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();

        prevButton = new PrevButton(36, 36);
        c.fill = GridBagConstraints.CENTER;
        c.gridx = 0;
        c.gridy = 0;
        c.weightx = 0.1;
        c.weighty = 1.0;
        this.add(prevButton, c);

        playButton = new PlayButton(36, 36);
        c.fill = GridBagConstraints.CENTER;
        c.gridx = 1;
        c.gridy = 0;
        c.weightx = 0.0;
        c.weighty = 1.0;
        this.add(playButton, c);

        nexButton = new NextButton(36, 36);
        c.fill = GridBagConstraints.CENTER;
        c.gridx = 2;
        c.gridy = 0;
        c.weightx = 0.1;
        c.weighty = 1.0;
        this.add(nexButton, c);
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
