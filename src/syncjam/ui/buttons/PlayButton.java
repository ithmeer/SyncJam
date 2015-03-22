package syncjam.ui.buttons;

import syncjam.NowPlaying;
import syncjam.Playlist;
import syncjam.ui.buttons.base.ButtonUI;

import java.awt.*;

public class PlayButton extends ButtonUI
{
    private final Playlist playlist;

    public PlayButton(int w, int h, Playlist pl)
    {
        super(w, h);
        playlist = pl;
    }

    public PlayButton(int w, int h, Color c, Playlist pl)
    {
        super(w, h, c);
        playlist = pl;
    }

    public void clicked()
    {
        if(NowPlaying.getSong() != null)
            NowPlaying.playToggle();
    }

    public void paintComponent(Graphics g)
    {
        super.paintComponent(g);

        if (!NowPlaying.isPlaying())
        {
            Polygon playShape = new Polygon(
                    new int[]{0, 0,      getW()},
                    new int[]{0, getH(), getH() / 2}, 3);
            g.fillPolygon(playShape);
        }
        else
        {
            g.fillRect((getW() / 32) * 5,  0, getW() / 4, getH());
            g.fillRect((getW() / 32) * 21, 0, getW() / 4, getH());
        }
    }
}
