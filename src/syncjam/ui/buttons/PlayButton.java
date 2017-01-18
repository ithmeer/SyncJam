package syncjam.ui.buttons;

import syncjam.interfaces.PlayController;
import syncjam.interfaces.ServiceContainer;
import syncjam.ui.buttons.base.ButtonUI;

import java.awt.*;

public class PlayButton extends ButtonUI
{
    private final PlayController _player;

    public PlayButton(int w, int h, ServiceContainer services)
    {
        super(w, h);
        _player = services.getService(PlayController.class);
    }

    public PlayButton(int w, int h, Color c, ServiceContainer services)
    {
        super(w, h, c);
        _player = services.getService(PlayController.class);
    }

    protected void clicked()
    {
        _player.playToggle();
    }

    public void paintComponent(Graphics g)
    {
        super.paintComponent(g);

        if (!_player.isPlaying())
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
