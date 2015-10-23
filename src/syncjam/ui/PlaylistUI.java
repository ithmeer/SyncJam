package syncjam.ui;

import syncjam.Playlist;
import syncjam.Song;
import syncjam.SongUtilities;
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
    private final int xOffset = 6, yOffset = 6;
    private int itemHeight = 60;

    private int curItemYPos = 0;

    private int mouseX = -1, mouseY = -1;

    private final ScrollbarUI scrollbar;
    private final Playlist playlist;
    private int artHoverIndex = -1;
    private int removeHoverIndex = -1;

    public PlaylistUI(SongUtilities songUtils)
    {
        myW = 350;
        myH = 0;//440;

        setMinimumSize(new Dimension(myW, myH));
        setBackground(Colors.c_Background2);
        this.setLayout(new BorderLayout());

        playlist = songUtils.getPlaylist();
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

        //Mouse Over Effect
        Rectangle itemRect = new Rectangle(
                0,
                curItemYPos,
                (getWidth() - scrollbar.getWidth()),
                itemHeight);

        int ins = 7;
        Rectangle xRect = new Rectangle(
                xOffset + ins - 2,
                curItemYPos + ins,
                23 - ins,
                itemHeight + 1 - ins*2);

        if(itemRect.contains(mouseX,mouseY))
        {
            g.setColor(Colors.c_Highlight2);
            g.fillRect(xRect.x, xRect.y, xRect.width, xRect.height);
            g.setColor(Colors.c_Foreground1);

            int cX = xOffset+13;   //center xPos
            int cY = curItemYPos + itemHeight/2; //center yPos
            int xS = 3;            //size of 'X'
            g.drawLine(cX - xS, cY - xS, cX + xS, cY + xS);
            g.drawLine(cX - xS, cY + xS, cX + xS, cY - xS);
        }
        else
        {
            g.setColor(Colors.c_Foreground2);
            Colors.setFont(g, 14);
            g.drawString("" + (i + 1),
                    thisItemXPos,
                    thisItemYPos);
        }
        if(xRect.contains(mouseX, mouseY))
            removeHoverIndex = i;
        else if(removeHoverIndex == i)
            removeHoverIndex = -1;
    }

    private void drawAlbumArt(Graphics g, Song song, int i)
    {
        int ins = 5; //album art inset

        int thisItemXPos = xOffset + ins + 18;
        int thisItemYPos = curItemYPos + ins;

        g.setColor(Colors.c_Foreground2);
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

        Rectangle artRect = new Rectangle(
                thisItemXPos + 1,
                thisItemYPos + 1,
                imgsize,
                imgsize);

        if(itemRect.contains(mouseX, mouseY))
        {
                g.setColor(Colors.c_Background1);
                Graphics2D g2 = (Graphics2D)g;
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, .4f));
                g2.fillRect(artRect.x, artRect.y, artRect.width + 1, artRect.height + 1);  //shade over art
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));

                int centerX = artRect.x + artRect.width/2;
                int centerY = artRect.y + artRect.height/2;

                g.setColor(Colors.c_Highlight);
                g.fillOval(centerX - imgsize/4, centerY - imgsize/4, imgsize/2,imgsize/2); //circle

                g.setColor(Colors.c_Foreground1);
                Polygon playShape = new Polygon(  //play symbol
                    new int[]{centerX - imgsize/9, centerX - imgsize/9, centerX + imgsize/6},
                    new int[]{centerY - imgsize/7, centerY + imgsize/7, centerY}, 3);
                g.fillPolygon(playShape);
        }

        if(artRect.contains(mouseX, mouseY))
            artHoverIndex = i;
        else if(artHoverIndex == i)
            artHoverIndex = -1;

    }

    private void drawArtistName(Graphics g, Song song)
    {
        int textHeight = g.getFontMetrics().getHeight();

        int thisItemXPos = xOffset + itemHeight + 20;
        int thisItemYPos = curItemYPos + itemHeight / 4 + textHeight / 2 - 2;

        g.setColor(Colors.c_Foreground2);
        Colors.setFont(g, 14);

        String artistName = cutStringToWidth(
                song.getArtistName(),
                g.getFontMetrics(),
                getWidth() - thisItemXPos - scrollbar.getWidth() - 48);

        g.drawString(artistName, thisItemXPos, thisItemYPos);
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

        int thisItemXPos = getWidth() - scrollbar.getWidth() - textWidth;
        int thisItemYPos = curItemYPos + itemHeight / 4 + textHeight / 2 - 6;

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
            while(f.stringWidth(str) + f.stringWidth("...") > width || str.charAt(str.length()-1) == ' ')
            {
                str = str.substring(0,str.length()-1);
            }
            return str + "...";
        }
    }

    public void clear()
    {
        //playlist.clear();
    }

    @Override
    public void mouseClicked(MouseEvent e) {}

    @Override
    public void mousePressed(MouseEvent e){}

    @Override
    public void mouseReleased(MouseEvent e)
    {
        if(artHoverIndex >= 0)
            playlist.setCurrentSong(artHoverIndex);
        else if(removeHoverIndex >= 0)
            playlist.remove(removeHoverIndex);
    }

    @Override
    public void mouseEntered(MouseEvent e){}

    @Override
    public void mouseExited(MouseEvent e)
    {
        mouseX = -1;
        mouseY = -1;
        artHoverIndex = -1;
    }

    @Override
    public void mouseDragged(MouseEvent e) {}

    @Override
    public void mouseMoved(MouseEvent e)
    {
        mouseX = e.getX();
        mouseY = e.getY();
    }
}
