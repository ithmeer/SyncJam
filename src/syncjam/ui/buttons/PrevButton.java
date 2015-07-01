package syncjam.ui.buttons;

import syncjam.NowPlaying;
import syncjam.Playlist;
import syncjam.ui.buttons.base.ButtonUI;

import java.awt.*;

public class PrevButton extends ButtonUI
{
    private final Playlist playlist;

    public PrevButton(int w, int h, Playlist pl)
    {
        super(w, h);
        setPreferredSize(new Dimension(getW() + 20, getH()));
        playlist = pl;
    }
    public PrevButton(int w, int h, Color c, Playlist pl)
    {
        super(w, h, c);
        setPreferredSize(new Dimension(getW() + 20, getH()));
        playlist = pl;
    }

    public void clicked()
    {
        playlist.prevSong();
    }

    public void paintComponent(Graphics g)
    {
        super.paintComponent(g);

        g.fillPolygon(arrowShape(0,0));
        g.fillPolygon(arrowShape(-getW()/5*2,0));
    }

    public Polygon arrowShape(int x, int y)
    {
        x = getW()-(-x);

        return new Polygon(
                new int[]{x,            x,                  x - getW()/2},
                new int[]{y + getH()/4, y + (getH()/4) * 3, y + getH()/2}, 3);
    }
}
