package syncjam.ui;

import syncjam.NowPlaying;
import syncjam.Playlist;
import syncjam.Song;
import syncjam.ui.buttons.base.ScrollbarUI;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Iterator;

public class PlaylistUI extends JPanel
{
    private int myW, myH;
    private final int xOffset = 8, yOffset = 8;
    private final int itemHeight = 50;

    private int curItemYPos = 0;

    private final ScrollbarUI scrollbar;
    private final Playlist playlist;

    public PlaylistUI(Playlist pList)
    {
        myW = 350;
        myH = 0;//440;

        setMinimumSize(new Dimension(myW, myH));
        setBackground(Colors.c_Background2);
        this.setLayout(new BorderLayout());

        playlist = pList;
        scrollbar = new ScrollbarUI(Colors.c_Background2);
        this.add(scrollbar, BorderLayout.EAST);

        this.addMouseWheelListener(new MouseWheelListener() {
            @Override
            public void mouseWheelMoved(MouseWheelEvent e)
            {
                scrollbar.scrollEvent(e);
            }
        });

        Border fileDropBorder = BorderFactory.createMatteBorder(2, 2, 2, 2, Colors.c_Highlight);
        new FileDrop(this, fileDropBorder, new FileDrop.Listener()
        {
            @Override
            public void filesDropped(File[] files)
            {
                Song[] songs = new Song[files.length];
                for(int i = 0; i < files.length; i++)
                {
                    songs[i] = new Song(files[i]);
                }
                playlist.addAll(songs);
            }
        });
    }
    public void paintComponent(Graphics g)
    {
        super.paintComponent(g);

        scrollbar.setMaxValue(playlist.size() * itemHeight + yOffset*2);

        int i = 0;
        Iterator<Song> songIter = playlist.iterator();
        while (songIter.hasNext())
        {
            Song curSong = songIter.next();
            curItemYPos = (yOffset + (i * itemHeight)) - scrollbar.getValue();
            if(curItemYPos+itemHeight > 0 && curItemYPos < getHeight())
            {
                drawSong(g, i, curSong);

                if(NowPlaying.getSong() == curSong)
                {
                    g.setColor(Colors.c_Highlight);
                    g.drawRect(xOffset,curItemYPos, getWidth() - scrollbar.getWidth() - xOffset - 3, itemHeight);
                    g.drawRect(xOffset+1,curItemYPos+1, getWidth() - scrollbar.getWidth() - xOffset - 5, itemHeight-2);
                }
            }
            i++;
        }
    }

    private void drawSong(Graphics g, int index, Song song)
    {
        drawSongNum(g, index);
        drawAlbumArt(g, song);
        drawArtistName(g, song);
        drawSongName(g, song);
        drawSongLength(g, song);
    }

    private void drawSongNum(Graphics g, int i)
    {
        int textHeight = g.getFontMetrics().getHeight();
        int textWidth = g.getFontMetrics().stringWidth(""+(i+1));

        int thisItemXPos = xOffset - textWidth + 20;
        int thisItemYPos = curItemYPos + itemHeight/4 + textHeight/2;

        g.setColor(Colors.c_Foreground2);
        Colors.setFont(g, 14);
        g.drawString(""+(i+1),
                     thisItemXPos,
                     thisItemYPos);
    }

    private void drawAlbumArt(Graphics g, Song song)
    {
        int ins = 5; //album art inset

        int thisItemXPos = xOffset + ins + 18;
        int thisItemYPos = curItemYPos + ins;

        g.drawRect(thisItemXPos,
                   thisItemYPos,
                   itemHeight - ins*2,
                   itemHeight - ins*2); //frame

        //draw dark color behind art
        g.setColor(Colors.c_Background1);
        int imgsize = itemHeight - ins * 2 - 2;
        g.fillRect(thisItemXPos+1,
                   thisItemYPos + 1,
                   imgsize,
                   imgsize);

        //draw art
        BufferedImage albumImg = song.getScaledAlbumArt(imgsize+1, imgsize+1);
        if(albumImg != null)
            g.drawImage(albumImg,
                        thisItemXPos+1,
                        thisItemYPos + 1, null);
    }

    private void drawArtistName(Graphics g, Song song)
    {
        int textHeight = g.getFontMetrics().getHeight();

        int thisItemXPos = xOffset + itemHeight + 20;
        int thisItemYPos = curItemYPos + itemHeight / 4 + textHeight / 2 - 2;

        g.setColor(Colors.c_Foreground2);
        Colors.setFont(g, 14);
        g.drawString(song.getArtistName(), thisItemXPos, thisItemYPos);
    }

    private void drawSongName(Graphics g, Song song)
    {
        int textHeight = g.getFontMetrics().getHeight();

        int thisItemXPos = xOffset + itemHeight + 18;
        int thisItemYPos = curItemYPos + itemHeight / 2 + textHeight / 2 + 4;

        g.setColor(Colors.c_Foreground1);
        Colors.setFont(g, 19);

        String songName = cutStringToWidth(
                song.getSongName(),
                g.getFontMetrics(),
                getWidth() - thisItemXPos - scrollbar.getWidth());

        g.drawString(songName, thisItemXPos, thisItemYPos);
    }

    private void drawSongLength(Graphics g, Song song)
    {
        int textHeight = g.getFontMetrics().getHeight();
        int textWidth = g.getFontMetrics().stringWidth(song.getSongLengthString());

        int thisItemXPos = getWidth() - scrollbar.getWidth() - textWidth - 10;
        int thisItemYPos = curItemYPos + itemHeight / 4 + textHeight / 2 - 2;

        g.setColor(Colors.c_Foreground2);
        Colors.setFont(g, 14);
        g.drawString(song.getSongLengthString(), thisItemXPos, thisItemYPos);
    }

    private String cutStringToWidth(String str, FontMetrics f, int width)
    {
        if(f.stringWidth(str) < width)
        {
            return str;
        }
        else
        {
            while(f.stringWidth(str) + f.stringWidth("...") > width)
            {
                str = str.substring(0,str.length()-1);
            }
            return str + "...";
        }
    }
}
