package syncjam.ui;

import syncjam.interfaces.PlayController;
import syncjam.interfaces.ServiceContainer;
import syncjam.interfaces.Song;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

public class InfoUI extends JPanel
{
    private final int _aaWidth = 120; //album art width
    private final int _aaHeight = 120; //album art height
    private final BufferedImage sjLogo;
    private final PlayController _playCon;
    private boolean _shouldScroll = false;
    private boolean _scrolling = false;
    private float _scrollValue = 0f;
    private final float _scrollSpeed = .0075f;
    private final float _scrollStop = 1f;
    private final float _scrollReset = 2f;

    private final int _hOffset = 12;
    private final int _vOffset = 20;
    private final int _spacing = 32;

    public InfoUI(ServiceContainer services)
    {
        int _myW = 350;
        int _myH = 114;

        setOpaque(false);
        setMinimumSize(new Dimension(_myW, _myH));
        setMaximumSize(new Dimension(_myW, _myH));
        _playCon = services.getService(PlayController.class);
        sjLogo = UIServices.loadBufferedImage("SJLogo64.png");

        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                super.mouseEntered(e);
                _shouldScroll = true;
            }
        });
        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseExited(MouseEvent e) {
                super.mouseExited(e);
                _shouldScroll = false;
            }
        });
    }

    public void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        Song curSong = _playCon.getSong();

        if(curSong != null)
        {
            drawAlbumArt(g, curSong);
            drawSongInfo(g, curSong);
        }
    }

    private void drawAlbumArt(Graphics g, Song song)
    {
        g.setColor(Colors.get(Colors.Highlight));
        g.drawRect(0, 0, _aaWidth + 3, _aaHeight + 3);
        g.drawRect(1, 1, _aaWidth + 1, _aaHeight + 1);
        if (song.getAlbumArt() != null)
        {
            //g.drawImage(song.getScaledAlbumArt(_aaWidth, _aaHeight), 2, 2, null);
            g.drawImage(song.getPrescaledAlbumArt(0), 2, 2, null);
        }
    }

    private void drawSongInfo(Graphics g, Song song)
    {
        if(_scrolling || _shouldScroll) updateScrolling();

        drawScrollingString(g, song.getTitle(), 22, Colors.Foreground1, _aaWidth + _hOffset, _vOffset);
        drawScrollingString(g, song.getArtistName(), 18, Colors.Foreground2, _aaWidth + _hOffset, _vOffset + _spacing);
        drawScrollingString(g, song.getAlbumName(), 18, Colors.Foreground2, _aaWidth + _hOffset, _vOffset + _spacing*2);
        g.drawString(song.getLengthString(), _aaWidth + _hOffset, _vOffset + _spacing * 3);
    }

    private void updateScrolling()
    {
        if(_scrollValue > -_scrollSpeed-.1f && _scrollValue < 0 && !_shouldScroll) {
            _scrollValue = 0;
            _scrolling = false;
        }
        else if(_scrollValue < _scrollReset) {
            _scrollValue += _scrollSpeed;
            _scrolling = true;
        }
        else {
            _scrollValue = -2f;
        }
    }

    private void drawScrollingString(Graphics g, String string, int fontSize, Colors c, int x, int y)
    {
        int availableSpace = getWidth() - _aaWidth - _hOffset;
        g.setColor(Colors.get(c));
        Colors.setFont(g, fontSize);
        
        if(!_scrolling)// || _scrollValue < 0)
        {
            String cutString = cutStringToWidth(
                    string,
                    g.getFontMetrics(),
                    availableSpace);
            g.drawString(cutString, x, y);
        }
        else
        {
            float scrollVal = Math.min(_scrollValue, _scrollStop);
            if(_scrollValue < -1)                         //from -1 to 0 is moving left, but i needed a pause before it
                scrollVal = Math.max(scrollVal+1, -1);    //starts moving from 0 to 1, so reset it to -2 and add 1
            else if(_scrollValue < 0)                     //to make it so from -1 to 0 is a pause
                scrollVal = Math.max(scrollVal, 0);       //this was the easiest way i could think of, pleas forgive me
            int stringLength = g.getFontMetrics().stringWidth(string);

            int scrollAmount = stringLength - availableSpace;
            if (scrollAmount < 0) scrollAmount = 0;

            double smoothVal = (Math.sin((scrollVal-.5)*Math.PI)+1)/2; //some maths to set the movement to a sine curve
                                                                       //gotta love that smooth, smooth move

            BufferedImage stringImg = renderTextToImage(string, (int) (scrollAmount * smoothVal),
                    new Dimension(availableSpace, fontSize), fontSize, c);
            g.drawImage(stringImg, x, y - (fontSize - 4), null);
        }
    }

    public BufferedImage renderTextToImage(String string, int offset, Dimension d, int fontSize, Colors color) {
        BufferedImage returnImage = new BufferedImage(
                Math.max((int)d.getWidth(),  1),
                Math.max((int)d.getHeight(), 1),
                Transparency.TRANSLUCENT);
        Graphics2D returnGraphics = returnImage.createGraphics();
        returnGraphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        Colors.setFont(returnGraphics, fontSize);
        returnGraphics.setColor(Colors.get(color));
        returnGraphics.drawString(string, -offset, returnImage.getHeight() * 0.8f);
        returnGraphics.dispose();
        return returnImage;
    }

    private String cutStringToWidth(String str, FontMetrics f, int width)
    {
        if(f.stringWidth(str) < width)
        {
            return str;
        }
        else
        {
            while(str.length() > 0 && f.stringWidth(str) + f.stringWidth("...") > width || str.charAt(str.length()-1) == ' ')
            {
                str = str.substring(0,str.length()-1);
            }
            return str + "...";
        }
    }
}
