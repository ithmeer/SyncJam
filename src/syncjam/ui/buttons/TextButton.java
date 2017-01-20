package syncjam.ui.buttons;

import syncjam.ui.Colors;
import syncjam.ui.buttons.base.ButtonUI;

import java.awt.*;

/**
 * Created by Marty on 1/5/2016. lol
 */

public class TextButton extends ButtonUI
{
    protected TextButton(String text)
    {
        this(text, 0, 0);
    }
    protected TextButton(String text, int w, int y)
    {
        super(w, y, Colors.get(Colors.Background2));
        setText(text);
        setMargin(new Insets(0,0,0,0));
    }
    @Override
    protected void clicked()
    {

    }
}