package syncjam.ui.buttons;

import syncjam.SongUtilities;
import syncjam.ui.buttons.base.ButtonUI;

import java.awt.*;

public class NextButton extends ButtonUI
{
    public NextButton(int w, int h, SongUtilities songUtils)
    {
        super(w, h, songUtils);
        setPreferredSize(new Dimension(getW() + 19, getH()));
    }

    public NextButton(int w, int h, Color c, SongUtilities songUtils)
    {
        super(w, h, c, songUtils);
        setPreferredSize(new Dimension(getW() + 19, getH()));
    }

    protected void clicked()
    {
        songUtilities.getPlaylist().nextSong();
    }

    public void paintComponent(Graphics g)
    {
        super.paintComponent(g);

        g.fillPolygon(makeArrow(getWidth() - getW(), 0));
        g.fillPolygon(makeArrow(getWidth() - getW() + getW() / 5 * 2, 0));
    }

    public Polygon makeArrow(int x, int y)
    {
        return new Polygon(
                new int[]{x,            x,                  x + getW()/2},
                new int[]{y + getH()/4, y + (getH()/4) * 3, y + getH()/2}, 3);
    }
}