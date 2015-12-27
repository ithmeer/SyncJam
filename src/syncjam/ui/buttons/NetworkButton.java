package syncjam.ui.buttons;

import syncjam.SongUtilities;
import syncjam.ui.NetworkWindow;
import syncjam.ui.WindowObject;
import syncjam.ui.buttons.base.ButtonUI;

import javax.swing.*;
import java.awt.*;

public class NetworkButton extends ButtonUI
{
    private NetworkWindow netwindow = null;
    private SongUtilities songUtilities;
    public NetworkButton(int w, int h, SongUtilities songUtils)
    {
        super(w, h, songUtils);
        setPreferredSize(new Dimension(getW() + 19, getH()));
        songUtilities = songUtils;
    }

    public NetworkButton(int w, int h, Color c, SongUtilities songUtils)
    {
        super(w, h, c, songUtils);
        setPreferredSize(new Dimension(getW() + 19, getH()));
        songUtilities = songUtils;
    }

    protected void clicked()
    {
        netwindow = new NetworkWindow(220, 25, songUtilities);
        netwindow.open();
        netwindow.validate();
        netwindow.setResizable(false);
    }

    public void paintComponent(Graphics g)
    {
        super.paintComponent(g);

        g.drawString("Connect", 0, getH()/2);
    }
}