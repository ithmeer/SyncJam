package syncjam.ui;

import syncjam.interfaces.ServiceContainer;
import syncjam.ui.buttons.NextButton;
import syncjam.ui.buttons.PlayButton;
import syncjam.ui.buttons.PrevButton;
import syncjam.ui.buttons.base.ButtonUI;

import javax.swing.*;
import java.awt.*;

public class ControlUI extends JPanel
{
    private int myW, myH;
    private ButtonUI playButton, prevButton, nexButton;

    public ControlUI(ServiceContainer services)
    {
        myW = 380;
        myH = 36;

        setMinimumSize(new Dimension(myW, myH));
        setMaximumSize(new Dimension(myW, myH));
        setBackground(Colors.c_Background1);


        prevButton = new PrevButton(36, 36, services);
        this.add(prevButton);

        playButton = new PlayButton(36, 36, services);
        this.add(playButton);

        nexButton = new NextButton(36, 36, services);
        this.add(nexButton);
    }
    public void paintComponent(Graphics g)
    {
        super.paintComponent(g);
    }
}
