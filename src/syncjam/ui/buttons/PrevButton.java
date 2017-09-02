package syncjam.ui.buttons;

import syncjam.interfaces.CommandQueue;
import syncjam.interfaces.Playlist;
import syncjam.interfaces.ServiceContainer;
import syncjam.ui.Colors;
import syncjam.ui.buttons.base.ButtonUI;

import java.awt.*;

public class PrevButton extends ButtonUI
{
    private final CommandQueue _cmdQueue;

    public PrevButton(int w, int h, ServiceContainer services)
    {
        super(w, h);
        setPreferredSize(new Dimension(getW() + 20, getH()));
        _cmdQueue = services.getService(CommandQueue.class);
    }

    public PrevButton(int w, int h, Colors c, ServiceContainer services)
    {
        super(w, h, c);
        setPreferredSize(new Dimension(getW() + 20, getH()));
        _cmdQueue = services.getService(CommandQueue.class);
    }

    protected void clicked()
    {
        _cmdQueue.prevSong();
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
