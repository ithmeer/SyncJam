package syncjam.ui;

import syncjam.NowPlaying;
import syncjam.Song;
import syncjam.base.Updatable;
import syncjam.ui.buttons.PlayButton;
import syncjam.ui.buttons.UIButton;

import javax.swing.*;
import java.awt.*;

public class InfoUI extends JPanel implements Updatable
{
    private int myW, myH;
    private int aaWidth = 100; //album art width
    private int aaHeight = 100; //album art height

    public InfoUI()
    {
        myW = 350;
        myH = 100;

        setMinimumSize(new Dimension(myW, myH));
        setBackground(Colors.c_Background1);
    }

    public int getW() { return myW; }

    public int getH() { return myH; }

    public void paintComponent(Graphics g)
    {
        super.paintComponent(g);

        if(NowPlaying.getSong() != null)
        {
            drawAlbumArt(g);
            drawSongInfo(g);
        }
    }

    private void drawAlbumArt(Graphics g)
    {
        g.setColor(Colors.c_Highlight);
        g.drawRect(0, 0, aaWidth + 4, aaHeight + 4);
        g.drawRect(1, 1, aaWidth + 2, aaHeight + 2);
        if (NowPlaying.getAlbumArt() != null)
        {
            g.drawImage(NowPlaying.getScaledAlbumArt(aaWidth, aaHeight), 2, 2, null);
        }
    }

    private void drawSongInfo(Graphics g)
    {
        Colors.setFont(g, 23);

        int spacing = 28;

        g.setColor(Colors.c_Foreground1);
        g.drawString(NowPlaying.getSongName(), aaWidth + 12, 18);
        g.setColor(Colors.c_Foreground2);
        g.drawString(NowPlaying.getArtistName(),       aaWidth + 12, 18 + spacing);
        g.drawString(NowPlaying.getAlbumName(),        aaWidth + 12, 18 + spacing * 2);
        g.drawString(NowPlaying.getSongLengthString(), aaWidth + 12, 18 + spacing * 3);
    }

    public void update()
    {
        this.repaint();
    }
}
