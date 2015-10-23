package syncjam.ui.buttons;

import syncjam.NowPlaying;
import syncjam.SongUtilities;
import syncjam.ui.buttons.base.VerticalSliderUI;

/**
 * Created by Marty on 3/15/2015.
 */
public class VolumeSlider extends VerticalSliderUI
{
    public VolumeSlider(int startValue, int maxValue, SongUtilities songUtils)
    {
        super(startValue, maxValue, true, songUtils);
    }
    public void setValue(int n)
    {
        super.setValue(n);
        songUtilities.getPlayer().setVolume(getValue());
    }
}
