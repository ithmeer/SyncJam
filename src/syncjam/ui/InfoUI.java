package syncjam.ui;

import syncjam.NowPlaying;
import syncjam.base.Updatable;

import javax.swing.*;
import java.awt.*;

public class InfoUI extends JPanel
{
    private int myW, myH;
    private int aaWidth = 120; //album art width
    private int aaHeight = 120; //album art height

    public InfoUI()
    {
        myW = 350;
        myH = 114;

        setMinimumSize(new Dimension(myW, myH));
        setMaximumSize(new Dimension(myW, myH));
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
        int hOffset = 12;
        int vOffset = 20;
        int spacing = 32;

        g.setColor(Colors.c_Foreground1);
        Colors.setFont(g, 24);
        String songname = NowPlaying.getSongName();
        if(songname.length() > 16)
        {
            songname = songname.substring(0,14) + "...";
        }
        g.drawString(songname, aaWidth + hOffset, vOffset);

        g.setColor(Colors.c_Foreground2);
        Colors.setFont(g, 23);
        g.drawString(NowPlaying.getArtistName(), aaWidth + hOffset, vOffset + spacing);
        g.drawString(NowPlaying.getAlbumName(),        aaWidth + hOffset, vOffset + spacing * 2);
        g.drawString(NowPlaying.getSongLengthString(), aaWidth + hOffset, vOffset + spacing * 3);
    }
}
