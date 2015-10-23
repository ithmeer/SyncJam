package syncjam.ui.buttons;

import syncjam.SongUtilities;
import syncjam.ui.buttons.base.ButtonUI;

import java.awt.*;

public class PrevButton extends ButtonUI
{
    public PrevButton(int w, int h, SongUtilities songUtils)
    {
        super(w, h, songUtils);
        setPreferredSize(new Dimension(getW() + 20, getH()));
    }

    public PrevButton(int w, int h, Color c, SongUtilities songUtils)
    {
        super(w, h, c, songUtils);
        setPreferredSize(new Dimension(getW() + 20, getH()));
    }

    protected void clicked()
    {
        songUtilities.getPlaylist().prevSong();
    }

    public void paintComponent(Graphics g)
    {
        super.paintComponent(g);

        g.fillPolygon(makeArrow(0, 0));
        g.fillPolygon(makeArrow(-getW() / 5 * 2, 0));
    }

    public Polygon makeArrow(int x, int y)
    {
        x = getW()-(-x);

        return new Polygon(
                new int[]{x,            x,                  x - getW()/2},
                new int[]{y + getH()/4, y + (getH()/4) * 3, y + getH()/2}, 3);
    }
}
