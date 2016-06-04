package syncjam.ui.buttons;

import syncjam.SongUtilities;
import syncjam.ui.Colors;
import syncjam.ui.buttons.base.ButtonUI;

import java.awt.*;

/**
 * Created by Marty on 1/5/2016. lol
 */

public class TextButton extends ButtonUI
{
    protected TextButton(String text, SongUtilities songUtils)
    {
        this(text, 0, 0, songUtils);
    }
    protected TextButton(String text, int w, int y, SongUtilities songUtils)
    {
        super(w, y, Colors.c_Background2, songUtils);
        setText(text);
        setMargin(new Insets(0,0,0,0));
    }
    @Override
    protected void clicked()
    {

    }
}