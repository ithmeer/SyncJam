package syncjam.ui;

import syncjam.NowPlaying;
import syncjam.Song;

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
        Song curSong = NowPlaying.getSong();

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

        g.setColor(Colors.c_Foreground1);
        Colors.setFont(g, 24);
        String songName = song.getSongName();
        if(songName.length() > 16)
        {
            songName = songName.substring(0,14) + "...";
        }
        g.drawString(songName, aaWidth + hOffset, vOffset);

        g.setColor(Colors.c_Foreground2);
        Colors.setFont(g, 23);
        g.drawString(song.getArtistName(), aaWidth + hOffset, vOffset + spacing);
        g.drawString(song.getAlbumName(),        aaWidth + hOffset, vOffset + spacing * 2);
        g.drawString(song.getSongLengthString(), aaWidth + hOffset, vOffset + spacing * 3);
    }
}
