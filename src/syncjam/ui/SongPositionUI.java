package syncjam.ui;

import syncjam.NowPlaying;

import javax.swing.*;
import java.awt.*;
import java.text.Format;
import java.text.SimpleDateFormat;

public class SongPositionUI extends SliderBarUI
{
    public SongPositionUI()
    {
        super(0, 0, false);
    }

    public void paintComponent(Graphics g)
    {
        if(NowPlaying.getSong()!= null) {
            setMaxValue(NowPlaying.getSongLength());
        }

        if(NowPlaying.isPlaying && getValue() < getMaxValue()) {
            setValue(getValue() + 1);
        }

        super.paintComponent(g);
    }

    @Override
    protected void drawValue(Graphics g)
    {
        g.setColor(Colors.c_Highlight);
        g.drawString(getTimeStamp(this.getPosOnBar()), barXOffset, barYOffset - 9);

        g.setColor(Colors.c_Highlight);
        String timeToMax = "-" + getTimeStamp(this.getMaxValue() - this.getPosOnBar());
        int strWidth = g.getFontMetrics().stringWidth(timeToMax);
        g.drawString(timeToMax,barXOffset+getW()-strWidth,barYOffset-9);
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
