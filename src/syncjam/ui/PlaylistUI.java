package syncjam.ui;

import syncjam.NowPlaying;
import syncjam.Song;
import syncjam.ui.buttons.base.ScrollbarUI;
import syncjam.ui.FileDrop;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;

public class PlaylistUI extends JPanel
{
    private int myW, myH;
    private final int xOffset = 8, yOffset = 8;
    private final int itemHeight = 50;

    private int curItemYPos = 0;

    private ScrollbarUI scrollbar;
    private ArrayList<Song> songs = new ArrayList<Song>();

    public PlaylistUI()
    {
        myW = 350;
        myH = 0;//440;

        setMinimumSize(new Dimension(myW, myH));
        setBackground(Colors.c_Background2);
        this.setLayout(new BorderLayout());

        scrollbar = new ScrollbarUI(Colors.c_Background2);
        this.add(scrollbar, BorderLayout.EAST);

        //for(int i = 1; i <= 20; i++)
        //    songs.add(new Song("Song " + i, "Artist", "Album", 60));
        //songs.add(new Song("05 Jam for Jerry.mp3"));
        //NowPlaying.setSong(songs.get(0));

        this.addMouseWheelListener(new MouseWheelListener() {
            @Override
            public void mouseWheelMoved(MouseWheelEvent e)
            {
                scrollbar.scrollEvent(e);
            }
        });

        Border filedropBorder = BorderFactory.createMatteBorder(2, 2, 2, 2, Colors.c_Highlight);
        new FileDrop(this, filedropBorder, new FileDrop.Listener()
        {
            @Override
            public void filesDropped(File[] files)
            {
                for(int i = 0; i < files.length; i++)
                {
                    songs.add(new Song(files[i]));
                }
            }
        });
    }
    public void paintComponent(Graphics g)
    {
        super.paintComponent(g);

        scrollbar.setMaxValue(songs.size() * itemHeight + yOffset*2);

        for(int i = 0; i < songs.size(); i++)
        {
            curItemYPos = (yOffset + (i * itemHeight)) - scrollbar.getValue();
            if(songs.get(i) != null && (curItemYPos+itemHeight > 0 && curItemYPos < getHeight()))
            {
                drawSongNum(g,i);
                drawAlbumArt(g, i);
                drawArtistName(g,i);
                drawSongName(g, i);
                drawSongLength(g, i);

                if(NowPlaying.getSong() == songs.get(i))
                {
                    g.setColor(Colors.c_Highlight);
                    g.drawRect(xOffset,curItemYPos, getWidth() - scrollbar.getWidth() - xOffset - 3, itemHeight);
                    g.drawRect(xOffset+1,curItemYPos+1, getWidth() - scrollbar.getWidth() - xOffset - 5, itemHeight-2);
                }
            }
        }
    }
    private void drawSongNum(Graphics g, int i)
    {
        g.setColor(Colors.c_Foreground2);
        Colors.setFont(g, 14);
        int textHeight = g.getFontMetrics().getHeight();
        int textWidth = g.getFontMetrics().stringWidth(""+(i+1));
        g.drawString(""+(i+1),
                     xOffset - textWidth+20,
                     curItemYPos + itemHeight/4 + textHeight/2);
    }

    private void drawAlbumArt(Graphics g, int i)
    {
        int itemXPos = xOffset + 10;

        int ins = 5; //album art inset
        g.drawRect(xOffset + ins + itemXPos,
                   curItemYPos + ins,
                   itemHeight - ins*2,
                   itemHeight - ins*2); //frame

        //draw dark color behind art
        g.setColor(Colors.c_Background1);
        int imgsize = itemHeight - ins * 2 - 2;
        g.fillRect(xOffset + ins + itemXPos+1,
                   curItemYPos + ins + 1,
                   imgsize,
                   imgsize);

        //draw art
        BufferedImage albumImg = songs.get(i).getScaledAlbumArt(imgsize+1, imgsize+1);
        if(albumImg != null)
            g.drawImage(albumImg,
                        xOffset + ins + itemXPos+1,
                        curItemYPos + ins + 1, null);
    }

    private void drawArtistName(Graphics g, int i)
    {
        int itemXPos = xOffset + itemHeight + 20;

        g.setColor(Colors.c_Foreground2);
        Colors.setFont(g, 14);
        int textHeight = g.getFontMetrics().getHeight();
        g.drawString(songs.get(i).getArtistName(),
                itemXPos,
                curItemYPos + itemHeight / 4 + textHeight / 2);
    }

    private void drawSongName(Graphics g, int i)
    {
        int itemXPos = xOffset + itemHeight + 20;

        g.setColor(Colors.c_Foreground1);
        Colors.setFont(g, 19);
        int textHeight = g.getFontMetrics().getHeight();

        String songName = cutStringToWidth(
                songs.get(i).getSongName(),
                g.getFontMetrics(),
                getWidth() - itemXPos - scrollbar.getWidth());
        g.drawString(songName,
                     itemXPos,
                     curItemYPos + itemHeight / 2 + textHeight / 2 + 4);
    }

    private void drawSongLength(Graphics g, int i)
    {
        int itemXPos = getWidth() - scrollbar.getWidth() - 10;
        curItemYPos = yOffset + i*itemHeight - (scrollbar.getValue());

        g.setColor(Colors.c_Foreground2);
        Colors.setFont(g, 14);
        int textHeight = g.getFontMetrics().getHeight();
        int textWidth = g.getFontMetrics().stringWidth(songs.get(i).getSongLengthString());
        g.drawString(songs.get(i).getSongLengthString(),
                itemXPos - textWidth,
                curItemYPos + itemHeight / 4 + textHeight / 2);
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
