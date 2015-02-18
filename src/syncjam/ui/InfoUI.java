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
    private int aaWidth = 120; //album art width
    private int aaHeight = 120; //album art height

    public InfoUI()
    {
        myW = 350;
        myH = 112;

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
        g.drawRect(0, 0, aaWidth + 3, aaHeight + 3);
        g.drawRect(1, 1, aaWidth + 1, aaHeight + 1);
        if (NowPlaying.getAlbumArt() != null)
        {
            g.drawImage(NowPlaying.getScaledAlbumArt(aaWidth, aaHeight), 2, 2, null);
        }
    }

    private void drawSongInfo(Graphics g)
    {
        int spacing = 28;

        g.setColor(Colors.c_Foreground1);
        Colors.setFont(g, 25);
        g.drawString(NowPlaying.getSongName(), aaWidth + 12, 18);
        g.setColor(Colors.c_Foreground2);
        Colors.setFont(g, 20);
        g.drawString(NowPlaying.getArtistName(),       aaWidth + 12, 18 + spacing);
        g.drawString(NowPlaying.getAlbumName(),        aaWidth + 12, 18 + spacing * 2);
        g.drawString(NowPlaying.getSongLengthString(), aaWidth + 12, 18 + spacing * 3);
    }

    public void update()
    {
        this.repaint();
    }
}
