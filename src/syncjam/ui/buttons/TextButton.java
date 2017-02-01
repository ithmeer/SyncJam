package syncjam.ui.buttons;

import syncjam.ui.Colors;
import syncjam.ui.buttons.base.ButtonUI;

import java.awt.*;

/**
 * Created by Marty on 1/5/2016. lol
 */

public class TextButton extends ButtonUI
{
    protected TextButton(int w, int y, String text){
        this(w, y, text, Colors.Background2);
    }
    protected TextButton(int w, int y, String text, Colors bg)
    {
        super(w, y);
        background = Colors.Background2;
        setText(text);
        setMargin(new Insets(0,0,0,0));
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(Colors.get(background).brighter());
        g.drawRect(0, 0, getWidth(), getHeight());
    }

    @Override
    protected void clicked()
    {

    }
}