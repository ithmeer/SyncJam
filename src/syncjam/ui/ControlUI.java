package syncjam.ui;

import syncjam.interfaces.ServiceContainer;
import syncjam.ui.buttons.NextButton;
import syncjam.ui.buttons.PlayButton;
import syncjam.ui.buttons.PrevButton;
import syncjam.ui.buttons.base.ButtonUI;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class ControlUI extends JPanel
{
    private int myW, myH;
    private ButtonUI playButton, prevButton, nextButton;

    public ControlUI(ServiceContainer services)
    {
        myW = 380;
        myH = 36;

        setMinimumSize(new Dimension(myW, myH));
        setMaximumSize(new Dimension(myW, myH));
        this.setBorder(new EmptyBorder(10,0,0,0));


        prevButton = new PrevButton(36, 36, services);
        this.add(prevButton);

        playButton = new PlayButton(36, 36, services);
        this.add(playButton);

        nextButton = new NextButton(36, 36, services);
        this.add(nextButton);
    }
    public void pressPlayButton() { playButton.doClick(); }
    public void pressNextButton() { nextButton.doClick(); }
    public void pressPrevButton() { prevButton.doClick(); }
    
    public void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        setBackground(Colors.get(Colors.Background1));
    }
}
