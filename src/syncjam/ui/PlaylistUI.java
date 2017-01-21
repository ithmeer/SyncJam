package syncjam.ui;

import syncjam.BytesSong;
import syncjam.SyncJamException;
import syncjam.interfaces.Playlist;
import syncjam.interfaces.ServiceContainer;
import syncjam.interfaces.Song;
import syncjam.interfaces.SongQueue;
import syncjam.ui.base.ItemList;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Arrays;
import java.util.Iterator;

public class PlaylistUI extends ItemList
{
    private final Playlist _playlist;
    private final SongQueue _songQueue;
    private int artHoverIndex = -1;
    private int removeHoverIndex = -1;

    public PlaylistUI(final ServiceContainer services)
    {
        _playlist = services.getService(Playlist.class);
        _songQueue = services.getService(SongQueue.class);
        this.setEnableCustomDrawing(true);

        final Border fileDropBorder = BorderFactory.createMatteBorder(2, 2, 2, 2, Colors.get(Colors.Highlight));
        new FileDrop(this, fileDropBorder, files -> {
            BytesSong[] songs = new BytesSong[files.length];
            for(int i = 0; i < files.length; i++)
            {
                try
                {
                    songs[i] = new BytesSong(files[i]);
                }
                catch (SyncJamException e)
                {
                    e.printStackTrace();
                }
            }

            _songQueue.addAll(Arrays.asList(songs));
            _playlist.addAll(songs);

            scrollbar.setMaxValue(_playlist.size() * itemHeight + yOffset*2);
        });
    }
    @Override
    public void paintComponent(Graphics g)
    {
        super.paintComponent(g);

        int i = 0;
        Iterator<Song> songIter = _playlist.iterator();
        Song draggedSong = null;
        int draggedIndex = -1;
        while (songIter.hasNext())
        {
            Song curSong = songIter.next();

            int curItemYPos = getYPosInUI(i);
            updateSplit(i);

            if(itemDragIndex == i && draggedSong == null)
            {
                draggedSong = curSong;
                draggedIndex = i;
                i++;
                continue;
            }

            if(curItemYPos+itemHeight > 0 && curItemYPos < getHeight())
            {
                drawSong(g, xOffset, curItemYPos, i, curSong);
            }
            i++;
        }

        //Draw Dragged Song & Determine Drop Position
        if(draggedSong != null)
        {
            int dragY = mouseY-itemHeight/2;

            ((Graphics2D)g).setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, .5f));
            drawSong(g, xOffset, dragY, draggedIndex, draggedSong);
            ((Graphics2D)g).setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));

            if(itemHoverIndex != -1)
            {
                int hoverItemYPos = getYPosInUI(itemHoverIndex);
                if(mouseY < hoverItemYPos + itemHeight/2)
                    itemDropIndex = itemHoverIndex;
                else if(mouseY >= hoverItemYPos - itemHeight/2)
                    itemDropIndex = itemHoverIndex+1;
            }
            if(lastDropIndex != itemDropIndex)
            {
                lastDropIndex = itemDropIndex;
            }
        }
        else
            buildSplitArray();
    }

    //====    DRAW SONG     ====

    private void drawSong(Graphics g, int x, int y, int index, Song song)
    {
        checkHoverIndex(index);
        drawSongProgress(g,x,y, song, index);
        drawSongNum(   g, x, y, index);
        drawAlbumArt(  g, x, y, song, index);
        drawArtistName(g, x, y, song);
        drawSongName(  g, x, y, song);
        drawSongLength(g, x, y, song);

        if(index == _playlist.getCurrentSongIndex())
        {
            g.setColor(Colors.get(Colors.Highlight));
            g.drawRect(x,  y,   getWidth() - scrollbar.getWidth() - xOffset - 1, itemHeight);
            g.drawRect(x+1,y+1, getWidth() - scrollbar.getWidth() - xOffset - 3, itemHeight-2);
        }
    }

    //====  DRAW DOWNLOAD PROGRESS  ====

    private void drawSongProgress(Graphics g, int x, int y, Song s, int i)
    {

        int progress = 100;
        if(progress < 100)
        {
            g.setColor(Colors.get(Colors.Background1));
            g.fillRect(x, y, getWidth() - scrollbar.getWidth(), itemHeight);

            if(progress > 0)
            {
                g.setColor(Colors.get(Colors.Highlight));

                float progWidth = (float)progress/100 * (getWidth() - scrollbar.getWidth());

                Graphics2D g2 = (Graphics2D) g;
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.4f));
                g2.fillRect(x, y, (int)progWidth, itemHeight);
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
            }
        }
    }

    //====  DRAW SONG NUM   ====

    private void drawSongNum(Graphics g, int x, int y, int i)
    {
        Colors.setFont(g, 14);

        int textHeight = g.getFontMetrics().getHeight();
        int textWidth = g.getFontMetrics().stringWidth(""+(i+1));

        //Mouse Over Effect

        int ins = 7;
        Rectangle xRect = new Rectangle(
            x + ins - 2,
            y + ins,
            23 - ins,
            itemHeight + 1 - ins*2);

        Rectangle itemRect = new Rectangle(
                0,
                getYPosInUI(i),
                getWidth()-scrollbar.getWidth(),
                itemHeight);


        if(itemRect.contains(mouseX, mouseY) && itemHoverIndex == i && itemDragIndex == -1)
        {
            g.setColor(Colors.get(Colors.Highlight2));
            g.fillRect(xRect.x, xRect.y, xRect.width, xRect.height);
            g.setColor(Colors.get(Colors.Foreground1));

            int cX = x+13;   //center xPos
            int cY = y + itemHeight/2; //center yPos
            int xS = 3;            //size of 'X'
            g.drawLine(cX - xS, cY - xS, cX + xS, cY + xS);
            g.drawLine(cX - xS, cY + xS, cX + xS, cY - xS);
        }
        else
        {
            g.setColor(Colors.get(Colors.Foreground2));
            g.drawString("" + (i + 1),
                    x - textWidth + 20,
                    y + itemHeight/2 + textHeight/4);
        }
        if(xRect.contains(mouseX, mouseY))
            removeHoverIndex = i;
        else if(removeHoverIndex == i)
            removeHoverIndex = -1;
    }

    //====  DRAW ALBUM ART  ====

    private void drawAlbumArt(Graphics g, int x, int y, Song song, int i)
    {
        int ins = 5; //album art padding

        int thisItemXPos = x + ins + 18;
        int thisItemYPos = y + ins;

        g.setColor(Colors.get(Colors.Foreground2));
        g.drawRect(thisItemXPos,
                   thisItemYPos,
                   itemHeight - ins*2,
                   itemHeight - ins*2); //frame

        //draw dark color behind art
        g.setColor(Colors.get(Colors.Background1));
        int imgsize = itemHeight - ins * 2 - 2;
        g.fillRect(thisItemXPos + 1,
                   thisItemYPos + 1,
                   imgsize,
                   imgsize);

        //draw art
        BufferedImage albumImg = song.getScaledAlbumArtFast(imgsize+1, imgsize+1);
        if(albumImg != null)
            g.drawImage(albumImg,
                        thisItemXPos + 1,
                        thisItemYPos + 1, null);

        //Mouse Over Effect

        Rectangle artRect = new Rectangle(
                thisItemXPos + 1,
                thisItemYPos + 1,
                imgsize,
                imgsize);

        Rectangle itemRect = new Rectangle(
                0,
                getYPosInUI(i),
                thisItemXPos+4+imgsize,
                itemHeight);


        if(itemRect.contains(mouseX, mouseY) && itemHoverIndex == i && itemDragIndex == -1)
        {
            g.setColor(Colors.get(Colors.Background1));
            Graphics2D g2 = (Graphics2D) g;
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, .4f));
            g2.fillRect(artRect.x, artRect.y, artRect.width + 1, artRect.height + 1);  //shade over art
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));

            int centerX = artRect.x + artRect.width / 2;
            int centerY = artRect.y + artRect.height / 2;

            g.setColor(Colors.get(Colors.Highlight));
            g.fillOval(centerX - imgsize / 4, centerY - imgsize / 4, imgsize / 2, imgsize / 2); //circle

            g.setColor(Colors.get(Colors.Foreground1));
            Polygon playShape = new Polygon(  //play symbol
                    new int[]{centerX - imgsize / 9, centerX - imgsize / 9, centerX + imgsize / 6},
                    new int[]{centerY - imgsize / 7, centerY + imgsize / 7, centerY}, 3);
            g.fillPolygon(playShape);
        }

        if(artRect.contains(mouseX, mouseY))
            artHoverIndex = i;
        else if(artHoverIndex == i)
            artHoverIndex = -1;

    }

    //====  DRAW ARTIST NAME  ====

    private void drawArtistName(Graphics g, int x, int y, Song song)
    {
        Colors.setFont(g, 14);

        int textHeight = g.getFontMetrics().getHeight();

        int thisItemXPos = x + itemHeight + 20;
        int thisItemYPos = y + itemHeight / 4 + textHeight / 2;

        g.setColor(Colors.get(Colors.Foreground2));

        String artistName = cutStringToWidth(
                song.getArtistName(),
                g.getFontMetrics(),
                getWidth() - thisItemXPos - scrollbar.getWidth() - 48);

        g.drawString(artistName, thisItemXPos, thisItemYPos);
    }

    //====  DRAW SONG NAME  ====

    private void drawSongName(Graphics g, int x, int y, Song song)
    {
        Colors.setFont(g, 16);

        int textHeight = g.getFontMetrics().getHeight();

        int thisItemXPos = x + itemHeight + 20;
        int thisItemYPos = y + itemHeight / 2 + textHeight / 2 + 8;

        g.setColor(Colors.get(Colors.Foreground1));

        String songName = cutStringToWidth(
                song.getTitle(),
                g.getFontMetrics(),
                getWidth() - thisItemXPos - scrollbar.getWidth());

        g.drawString(songName, thisItemXPos, thisItemYPos);
    }

    //====  DRAW SONG LENGTH  ====

    private void drawSongLength(Graphics g, int x, int y, Song song)
    {
        Colors.setFont(g, 14);

        int textHeight = g.getFontMetrics().getHeight();
        int textWidth = g.getFontMetrics().stringWidth(song.getLengthString());

        int thisItemXPos = getWidth() - scrollbar.getWidth() - textWidth - 4;
        int thisItemYPos = y + itemHeight / 4 + textHeight / 2 - 3;

        g.setColor(Colors.get(Colors.Foreground2));
        g.drawString(song.getLengthString(), thisItemXPos, thisItemYPos);
    }

    //====  UTILITY METHODS  ====

    @Override
    protected void checkHoverIndex(int i)
    {
        Rectangle itemRect = new Rectangle(
                0,
                getYPosInUI(i),
                (getWidth() - scrollbar.getWidth()),
                itemHeight);

        if(itemHoverIndex == i && !itemRect.contains(mouseX,mouseY))
            itemHoverIndex = -1;
        else if(itemDragIndex != i && mouseY > itemRect.getY() && mouseY < itemRect.getY()+itemRect.getHeight())
            itemHoverIndex = i;

        if(itemDragIndex >= 0)
        {
            if(mouseY > getHeight() && i < _playlist.size())
                itemHoverIndex = _playlist.size()-1;
            else if(mouseY < 0 && i > 0)
                itemHoverIndex = 0;
        }
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

    @Override
    protected void updateScrollbar() { scrollbar.setMaxValue(_playlist.size() * itemHeight + yOffset*2); }

    private void buildSplitArray()
    {
        splits = new int[_playlist.size()+1];
    }

    @Override
    public void remove(int index)
    {
        _playlist.remove(index);
        updateScrollbar();
    }
    public void clear()
    {
        //_playlist.clear();
        //updateScrollbar();
    }

    //====  LISTENERS  ====

    @Override
    public void mouseReleased(MouseEvent e)
    {
        if(itemDragIndex >= 0)
        {
            if(itemDropIndex == -1)
                itemDropIndex = itemDragIndex;

            if(itemDragIndex != itemDropIndex-1)
                _playlist.moveSong(itemDragIndex, itemDropIndex);
            itemDragIndex  = -1;
            itemDropIndex  = -1;
            itemHoverIndex = -1;
            mouseX = -1;
            mouseY = -itemHeight;
        }
        else
        {
            if (artHoverIndex >= 0)
                _playlist.setCurrentSong(artHoverIndex);
            else if (removeHoverIndex >= 0)
                remove(removeHoverIndex);
        }
    }

    @Override
    public void mouseExited(MouseEvent e)
    {
        mouseX = -1;
        mouseY = -1;
        artHoverIndex = -1;
    }

    @Override
    public void mouseDragged(MouseEvent e)
    {
        mouseX = e.getX();
        mouseY = e.getY();
        double dist = Math.hypot(clickStartX-mouseX, clickStartY-mouseY);
        if(isDraggingEnabled() && dist > 8)
        {
            if (itemDragIndex == -1 &&
                    itemHoverIndex != -1 &&
                    itemHoverIndex != artHoverIndex &&
                    itemHoverIndex != removeHoverIndex) {
                itemDragIndex = itemHoverIndex;
                for (int i = itemDragIndex; i < splits.length - 1; i++) {
                    splits[i] = itemHeight + 6;
                }
            }
        }
    }
}
