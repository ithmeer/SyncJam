package syncjam.ui;

import syncjam.NowPlaying;
import syncjam.Song;
import syncjam.base.Updatable;
import syncjam.ui.buttons.PlayButton;
import syncjam.ui.buttons.UIButton;

import java.awt.*;

public class PlayerUI implements Updatable
{
    private int myX, myY, myW, myH;
    private int aaWidth = 100; //album art width
    private int aaHeight = 100; //album art height
    private UIButton playButton;

    public PlayerUI(int x, int y, int w, int h)
    {
        myX = x;
        myY = y;
        myW = w;
        myH = h;

        playButton = new PlayButton(w / 2, y + 150, 40, 40);
    }

    public int getX() { return myX; }

    public int getY() { return myY; }

    public int getW() { return myW; }

    public int getH() { return myH; }

    public void draw(Graphics g)
    {
        NowPlaying.setSong(new Song("Spectrum", "Shook", "Spectrum", 324));
        drawAlbumArt(g);
        drawSongInfo(g);
        drawControls(g);
    }

    private void drawAlbumArt(Graphics g)
    {
        g.setColor(Colors.c_Highlight);
        g.drawRect(getX(), getY(), aaWidth + 4, aaHeight + 4);
        g.drawRect(getX() + 1, getY() + 1, aaWidth + 2, aaHeight + 2);
        if (NowPlaying.getAlbumArt() != null)
        {
            g.drawImage(NowPlaying.getScaledAlbumArt(aaWidth, aaHeight), getX() + 2, getY() + 2, null);
        }
    }

    private void drawSongInfo(Graphics g)
    {
        Colors.setFont(g, 23);
        int spacing = 28;
        g.setColor(Colors.c_Foreground1);
        g.drawString(NowPlaying.getSongName(), getX() + aaWidth + 12, getY() + 18);
        g.setColor(Colors.c_Foreground2);
        g.drawString(NowPlaying.getArtistName(),       getX() + aaWidth + 12, getY() + 18 + spacing);
        g.drawString(NowPlaying.getAlbumName(),        getX() + aaWidth + 12, getY() + 18 + spacing * 2);
        g.drawString(NowPlaying.getSongLengthString(), getX() + aaWidth + 12, getY() + 18 + spacing * 3);
    }

    private void drawControls(Graphics g)
    {
        playButton.draw(g);
    }

    public void update()
    {
        playButton.update();
    }
}
