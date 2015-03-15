package syncjam.ui.buttons;

import syncjam.NowPlaying;
import syncjam.Playlist;
import syncjam.ui.buttons.base.ButtonUI;

import java.awt.*;

public class PrevButton extends ButtonUI
{
    public PrevButton(int w, int h)
    {
        super(w, h);
        setPreferredSize(new Dimension(getW() + 20, getH()));
    }
    public PrevButton(int w, int h, Color c)
    {
        super(w, h, c);
        setPreferredSize(new Dimension(getW() + 20, getH()));
    }

    public void clicked()
    {
        NowPlaying.setSong(Playlist.getPrevSong());
    }

    public void paintComponent(Graphics g)
    {
        super.paintComponent(g);

        g.fillPolygon(arrowShape(0,0));
        g.fillPolygon(arrowShape(-getW()/7*2,0));
    }

    public Polygon arrowShape(int x, int y)
    {
        x = getW()-(-x);

        return new Polygon(
                new int[]{x,            x,                  x - getW()/2},
                new int[]{y + getH()/4, y + (getH()/4) * 3, y + getH()/2}, 3);
    }
}
