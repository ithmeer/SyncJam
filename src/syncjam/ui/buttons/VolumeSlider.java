package syncjam.ui.buttons;

import syncjam.SongUtilities;
import syncjam.interfaces.PlayController;
import syncjam.ui.buttons.base.VerticalSliderUI;

/**
 * Created by Marty on 3/15/2015.
 */
public class VolumeSlider extends VerticalSliderUI
{
    private final PlayController _player;

    public VolumeSlider(int startValue, int maxValue, SongUtilities songUtils)
    {
        super(startValue, maxValue, true, songUtils);
        _player = songUtils.getPlayController();
    }

    public void setValue(int n)
    {
        super.setValue(n);
        _player.setVolume(getValue());
    }
}
