package syncjam.ui.buttons;

import syncjam.NowPlaying;
import syncjam.base.Mouse;

import java.awt.*;

public class PlayButton extends UIButton
{
    private Polygon playShape;

    public PlayButton(int x, int y, int w, int h)
    {
        super(x - w / 2, y - h / 2, w, h);
    }

    public void clicked()
    {
        NowPlaying.isPlaying = !NowPlaying.isPlaying;
        System.out.println("("+ Mouse.getX()+","+Mouse.getY()+")");
    }

    public void draw(Graphics g)
    {
        super.draw(g);

        if (!NowPlaying.isPlaying)
            g.fillPolygon(playShape);
        if (NowPlaying.isPlaying)
        {
            g.fillRect(getX() + (getW() / 8), getY(), getW() / 4, getH());
            g.fillRect(getX() + (getW() / 8) * 5, getY(), getW() / 4, getH());
        }
    }

    public void update()
    {
        super.update();
        buildShapes(getX(), getY(), getW(), getH());
    }

    private void buildShapes(int x, int y, int w, int h)
    {
        playShape = new Polygon(
                new int[]{x, x, x + w},
                new int[]{y, y + h, y + h / 2}, 3);
    }
}
