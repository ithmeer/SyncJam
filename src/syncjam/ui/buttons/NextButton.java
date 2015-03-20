package syncjam.ui.buttons;

import syncjam.NowPlaying;
import syncjam.Playlist;
import syncjam.ui.buttons.base.ButtonUI;

import java.awt.*;

public class NextButton extends ButtonUI
{
    private final Playlist playlist;

    public NextButton(int w, int h, Playlist pl)
    {
        super(w, h);
        setPreferredSize(new Dimension(getW() + 19, getH()));
        playlist = pl;
    }
    public NextButton(int w, int h, Color c, Playlist pl)
    {
        super(w, h, c);
        setPreferredSize(new Dimension(getW() + 19, getH()));
        playlist = pl;
    }

    public void clicked()
    {
        playlist.nextSong();
    }

    public void paintComponent(Graphics g)
    {
        super.paintComponent(g);

        g.fillPolygon(arrowShape(getWidth()-getW(),0));
        g.fillPolygon(arrowShape(getWidth()-getW() + getW()/7*2,0));
    }

    public Polygon arrowShape(int x, int y)
    {
        return new Polygon(
                new int[]{x,            x,                  x + getW()/2},
                new int[]{y + getH()/4, y + (getH()/4) * 3, y + getH()/2}, 3);
    }
}