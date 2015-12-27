package syncjam.ui;

import syncjam.NowPlaying;
import syncjam.Song;
import syncjam.SongUtilities;

import javax.swing.*;
import java.awt.*;

public class InfoUI extends JPanel
{
    private int myW, myH;
    private int aaWidth = 120; //album art width
    private int aaHeight = 120; //album art height
    private final SongUtilities _songUtilities;

    public InfoUI(SongUtilities songUtils)
    {
        myW = 350;
        myH = 114;

        setMinimumSize(new Dimension(myW, myH));
        setMaximumSize(new Dimension(myW, myH));
        setBackground(Colors.c_Background1);
        _songUtilities = songUtils;
    }

    public int getW() { return myW; }

    public int getH() { return myH; }

    public void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        Song curSong = _songUtilities.getPlayer().getSong();

        if(curSong != null)
        {
            drawAlbumArt(g, curSong);
            drawSongInfo(g, curSong);
        }
    }

    private void drawAlbumArt(Graphics g, Song song)
    {
        g.setColor(Colors.c_Highlight);
        g.drawRect(0, 0, aaWidth + 3, aaHeight + 3);
        g.drawRect(1, 1, aaWidth + 1, aaHeight + 1);
        if (song.getAlbumArt() != null)
        {
            g.drawImage(song.getScaledAlbumArt(aaWidth, aaHeight), 2, 2, null);
        }
    }

    private void drawSongInfo(Graphics g, Song song)
    {
        int hOffset = 12;
        int vOffset = 20;
        int spacing = 32;

        String songName = cutStringToWidth(
                song.getSongName(),
                g.getFontMetrics(),
                getWidth()/2 - aaWidth/2 - hOffset);
        String artistName = cutStringToWidth(
                song.getArtistName(),
                g.getFontMetrics(),
                getWidth()/2 - aaWidth/2 - hOffset);

        g.setColor(Colors.c_Foreground1);
        Colors.setFont(g, 22);
        g.drawString(songName, aaWidth + hOffset, vOffset);

        g.setColor(Colors.c_Foreground2);
        Colors.setFont(g, 18);
        g.drawString(artistName,                 aaWidth + hOffset, vOffset + spacing);
        g.drawString(song.getAlbumName(),        aaWidth + hOffset, vOffset + spacing * 2);
        g.drawString(song.getSongLengthString(), aaWidth + hOffset, vOffset + spacing * 3);
    }

    private String cutStringToWidth(String str, FontMetrics f, int width)
    {
        if(f.stringWidth(str) < width)
        {
            return str;
        }
        else
        {
            while(f.stringWidth(str) + f.stringWidth("...") > width || str.charAt(str.length()-1) == ' ')
            {
                str = str.substring(0,str.length()-1);
            }
            return str + "...";
        }
    }
}
