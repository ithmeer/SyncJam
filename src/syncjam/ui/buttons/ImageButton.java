package syncjam.ui.buttons;

import syncjam.ui.Colors;
import syncjam.ui.UIServices;
import syncjam.ui.buttons.base.ButtonUI;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Created by Marty on 1/5/2016. lol
 */

public class ImageButton extends ButtonUI
{
    private final BufferedImage _image;

    protected ImageButton(int w, int y, String image){
        this(w, y, image, null, Colors.Background2);
    }
    public ImageButton(int w, int y, String image, Colors fg, Colors bg)
    {
        super(w, y);
        setMargin(new Insets(0,0,0,0));

        _image = UIServices.loadBufferedImage(image);
        _background = bg;
        _foreground = fg;
        _rollover = Colors.Foreground2;
        _pressed = Colors.Highlight;
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        int centerX = getWidth()/2, centerY = getHeight()/2;
        int imgCX = _image.getWidth()/2, imgCY = _image.getHeight()/2;

        Color color = getForeground();
        BufferedImage colorizedImage = _foreground == null ? _image : UIServices.colorImage(_image, color);
        g.drawImage(colorizedImage, centerX - imgCX, centerY - imgCY, null);
    }

    @Override
    protected void clicked()
    {

    }
}