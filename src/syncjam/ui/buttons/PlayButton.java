package syncjam.ui.buttons;

import syncjam.NowPlaying;
import syncjam.base.Mouse;

import java.awt.*;

public class PlayButton extends UIButton
{
    private Polygon playShape;

    public PlayButton(int w, int h) { super(w, h); }
    public PlayButton(int w, int h, Color c)
    {
        super(w, h, c);
    }

    public void clicked()
    {
        NowPlaying.isPlaying = !NowPlaying.isPlaying;
    }

    public void paintComponent(Graphics g)
    {
        super.paintComponent(g);

        if (!NowPlaying.isPlaying) {
            Polygon playShape = new Polygon(
                    new int[]{0, 0, 0 + getW()},
                    new int[]{0, 0 + getH(), 0 + getH() / 2}, 3);
            g.fillPolygon(playShape);
        }
        if (NowPlaying.isPlaying)
        {
            g.fillRect(0 + (getW() / 32) * 5, 0, getW() / 4, getH());
            g.fillRect(0 + (getW() / 32) * 21, 0, getW() / 4, getH());
        }
    }

    public void update()
    {
        super.update();
        repaint();
    }
}
