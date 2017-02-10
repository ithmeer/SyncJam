package syncjam.ui.buttons;

import syncjam.interfaces.PlayController;
import syncjam.interfaces.ServiceContainer;
import syncjam.ui.buttons.base.VerticalSliderUI;

import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

/**
 * Created by Marty on 3/15/2015.
 */
public class VolumeSlider extends VerticalSliderUI
{
    private final PlayController _player;

    public VolumeSlider(int startValue, int maxValue, ServiceContainer services)
    {
        super(startValue, maxValue, true);
        _player = services.getService(PlayController.class);
        addMouseWheelListener(new MouseWheelListener() {
            @Override
            public void mouseWheelMoved(MouseWheelEvent e) {
                setValue(getValue() - e.getUnitsToScroll());
            }
        });
    }

    public void setValue(int n)
    {
        super.setValue(n);
        _player.setVolume(getValue());
    }
}
