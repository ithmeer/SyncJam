package syncjam.ui;

import syncjam.NowPlaying;
import syncjam.Song;
import syncjam.base.Updatable;
import syncjam.ui.buttons.base.ScrollbarUI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;

public class PlaylistUI extends JPanel
{
    private int myW, myH;
    private int xOffset = 8, yOffset = 8;
    private int itemHeight = 50;

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

        for(int i = 1; i <= 15; i++)
            songs.add(new Song("Song " + i, "Artist", "Album", 60));
        //songs.set(0, new Song("05 Jam for Jerry.mp3"));

        this.addMouseWheelListener(new MouseWheelListener() {
            @Override
            public void mouseWheelMoved(MouseWheelEvent e)
            {
                scrollbar.scrollEvent(e);
            }
        });
    }
    public void paintComponent(Graphics g)
    {
        super.paintComponent(g);

        scrollbar.setMaxValue(songs.size() * itemHeight + yOffset*2);
        //System.out.println(scrollbar.getMaxValue() + " - " + scrollbar.getValue());

        for(int i = 0; i < songs.size(); i++)
        {
            drawSongNum(g,i);
            if(songs.get(i) != null)
            {
                drawAlbumArt(g, i);
                //drawSongName(g, i);
            }
        }
    }
    public void drawSongNum(Graphics g, int i)
    {
        int itemYPos = yOffset + i*itemHeight - (scrollbar.getValue());

        g.setColor(Colors.c_Foreground2);
        Colors.setFont(g, 14);
        int textHeight = g.getFontMetrics().getHeight();
        int textWidth = g.getFontMetrics().stringWidth(""+(i+1));
        g.drawString(""+(i+1),
                     xOffset - textWidth+20,
                     itemYPos + itemHeight/2 + textHeight/2);
    }

    public void drawAlbumArt(Graphics g, int i)
    {
        int itemXPos = xOffset + 10;
        int itemYPos = yOffset + i*itemHeight - (scrollbar.getValue());

        int ins = 5; //album art inset
        g.drawRect(xOffset + ins + itemXPos,
                   itemYPos + ins,
                   itemHeight - ins*2,
                   itemHeight - ins*2); //frame

        //draw dark color behind art
        g.setColor(Colors.c_Background1);
        int imgsize = itemHeight - ins * 2 - 2;
        g.fillRect(xOffset + ins + itemXPos+1,
                   itemYPos + ins + 1,
                   imgsize,
                   imgsize);

        //draw art
        BufferedImage albumImg = songs.get(i).getScaledAlbumArt(imgsize+1, imgsize+1);
        if(albumImg != null)
            g.drawImage(albumImg,
                        xOffset + ins + itemXPos+1,
                        itemYPos + ins + 1, null);
    }

    public void drawSongName(Graphics g, int i)
    {
        int itemXPos = xOffset + itemHeight + 30;
        int itemYPos = yOffset + i*itemHeight - (scrollbar.getValue());

        g.setColor(Colors.c_Foreground1);
        Colors.setFont(g, 20);
        int textHeight = g.getFontMetrics().getHeight();
        g.drawString(songs.get(i).getSongName(),
                     itemXPos,
                     itemYPos + itemHeight / 2 + textHeight / 2);
    }
}
