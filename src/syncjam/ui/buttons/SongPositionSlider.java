package syncjam.ui.buttons;

import syncjam.interfaces.CommandQueue;
import syncjam.interfaces.PlayController;
import syncjam.interfaces.ServiceContainer;
import syncjam.ui.Colors;
import syncjam.ui.buttons.base.SliderUI;

import java.awt.*;
import java.text.Format;
import java.text.SimpleDateFormat;

public class SongPositionSlider extends SliderUI
{
    private final CommandQueue _cmdQueue;
    private final PlayController _player;

    public SongPositionSlider(ServiceContainer services)
    {
        super(0, 0, false);
        _player = services.getService(PlayController.class);
        _cmdQueue = services.getService(CommandQueue.class);
    }

    public void paintComponent(Graphics g)
    {
        if(_player.getSong() != null)
        {
            setMaxValue(_player.getSongLength());
        }

        if(_player.isPlaying())
        {
            setValue(_player.getSongPosition(), false);
        }

        super.paintComponent(g);
    }

    @Override
    protected void drawValue(Graphics g)
    {
        if(_player.getSong() != null)
        {
            g.setColor(Colors.get(Colors.Highlight));
            g.drawString(getTimeStamp(this.getPosOnBar()), barXOffset, barYOffset - 9);

            g.setColor(Colors.get(Colors.Highlight));
            String timeToMax = "-" + getTimeStamp(this.getMaxValue() - this.getPosOnBar());
            int strWidth = g.getFontMetrics().stringWidth(timeToMax);
            g.drawString(timeToMax, barXOffset + getW() - strWidth, barYOffset - 9);
        }
    }

    public void setValue(int n, boolean wasClicked)
    {
        super.setValue(n, wasClicked);
        if (wasClicked)
        {
            _cmdQueue.seek(Math.round((n / (float) _player.getSongLength()) * 100.0f));
        }
    }

    public String getTimeStamp(int seconds)
    {
        String ts = "";

        if (seconds > 3600)
            ts += (int) Math.floor(seconds / 3600) + ":"; //if longer or equal to an hour, include hour digit

        Format timeFormat = new SimpleDateFormat("m:ss");
        ts += timeFormat.format(seconds * 1000); //format minutes:seconds

        return ts;
    }
}
