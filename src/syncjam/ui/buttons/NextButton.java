package syncjam.ui.buttons;

import syncjam.interfaces.CommandQueue;
import syncjam.interfaces.Playlist;
import syncjam.interfaces.ServiceContainer;
import syncjam.ui.Colors;
import syncjam.ui.buttons.base.ButtonUI;

import java.awt.*;

public class NextButton extends ButtonUI
{
    private final CommandQueue _cmdQueue;

    public NextButton(int w, int h, ServiceContainer services)
    {
        super(w, h);
        setPreferredSize(new Dimension(getW() + 19, getH()));
        _cmdQueue = services.getService(CommandQueue.class);
    }

    public NextButton(int w, int h, Colors c, ServiceContainer services)
    {
        super(w, h, c);
        setPreferredSize(new Dimension(getW() + 19, getH()));
        _cmdQueue = services.getService(CommandQueue.class);
    }

    protected void clicked()
    {
        _cmdQueue.nextSong();
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