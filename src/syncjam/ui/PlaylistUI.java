package syncjam.ui;

import syncjam.NowPlaying;
import syncjam.Playlist;
import syncjam.Song;
import syncjam.ui.buttons.base.ScrollbarUI;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Iterator;

public class PlaylistUI extends JPanel implements MouseListener, MouseMotionListener
{
    private int myW, myH;
    private final int xOffset = 8, yOffset = 8;
    private final int itemHeight = 50;

    private int curItemYPos = 0;

    private int mouseX = -1, mouseY = -1;

    private final ScrollbarUI scrollbar;
    private final Playlist playlist;
    private int hoverIndex = -1;

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
        this.addMouseListener(this);
        this.addMouseMotionListener(this);

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

                if(i == playlist.getCurrentSongIndex())
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
        drawAlbumArt(g, song, index);
        drawArtistName(g, song);
        drawSongName(g, song);
        drawSongLength(g, song);
    }

    private void drawSongNum(Graphics g, int i)
    {
        int textHeight = g.getFontMetrics().getHeight();
        int textWidth = g.getFontMetrics().stringWidth(""+(i+1));

        int thisItemXPos = xOffset - textWidth + 20;
        int thisItemYPos = curItemYPos + itemHeight/2 + textHeight/4;

        g.setColor(Colors.c_Foreground2);
        Colors.setFont(g, 14);
        g.drawString(""+(i+1),
                     thisItemXPos,
                     thisItemYPos);
    }

    private void drawAlbumArt(Graphics g, Song song, int i)
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
        g.fillRect(thisItemXPos + 1,
                   thisItemYPos + 1,
                   imgsize,
                   imgsize);

        //draw art
        BufferedImage albumImg = song.getScaledAlbumArt(imgsize+1, imgsize+1);
        if(albumImg != null)
            g.drawImage(albumImg,
                        thisItemXPos + 1,
                        thisItemYPos + 1, null);

        //Mouse Over Effect
        Rectangle itemRect = new Rectangle(
                0,
                curItemYPos,
                (getWidth() - scrollbar.getWidth()),
                itemHeight);

        if(itemRect.contains(mouseX, mouseY))
        {
                g.setColor(Colors.c_Background1);
                Graphics2D g2 = (Graphics2D)g;
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, .4f));
                g2.fillRect(thisItemXPos + 1, thisItemYPos + 1, itemHeight - ins*2 - 1, itemHeight - ins*2 - 1);
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
        }

        Rectangle artRect = new Rectangle(
                thisItemXPos + 1,
                thisItemYPos + 1,
                imgsize,
                imgsize);

        if(artRect.contains(mouseX, mouseY))
            hoverIndex = i;
        else if(hoverIndex == i)
            hoverIndex = -1;

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

    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void mousePressed(MouseEvent e){

    }

    @Override
    public void mouseReleased(MouseEvent e)
    {
        System.out.println(hoverIndex);
        if(hoverIndex >= 0)
            playlist.setCurrentSong(hoverIndex);
    }

    @Override
    public void mouseEntered(MouseEvent e)
    {
    }

    @Override
    public void mouseExited(MouseEvent e)
    {
        mouseX = -1;
        mouseY = -1;
        hoverIndex = -1;
    }

    @Override
    public void mouseDragged(MouseEvent e) {

    }

    @Override
    public void mouseMoved(MouseEvent e)
    {
        mouseX = e.getX();
        mouseY = e.getY();
    }
}
