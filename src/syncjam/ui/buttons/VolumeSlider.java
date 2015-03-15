package syncjam.ui.buttons;

import syncjam.NowPlaying;
import syncjam.ui.buttons.base.VerticalSliderUI;

/**
 * Created by Marty on 3/15/2015.
 */
public class VolumeSlider extends VerticalSliderUI
{
    public VolumeSlider(int startValue, int maxValue)
    {
        super(startValue, maxValue, true);
    }
    public void setValue(int n)
    {
        super.setValue(n);
        NowPlaying.setVolume(getValue());
    }
}
