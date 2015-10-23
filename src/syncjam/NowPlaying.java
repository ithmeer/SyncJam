package syncjam;

import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Static class to provide access to the currently playing song. Thread-safe. Expects that only one thread will ever
 *     set a new song (the thread that drives AudioController).
 */
public class NowPlaying
{
    private volatile AudioController controller;
    private volatile Song currentSong;
    private boolean isPlaying = false; // synchronized on this
    private final AtomicInteger songPosition = new AtomicInteger(0);

    public void setAudioController(AudioController au)
    {
        controller = au;
    }

    public BufferedImage getAlbumArt()   { return currentSong.getAlbumArt(); }

    public String getAlbumName()         { return currentSong.getAlbumName(); }

    public String getArtistName()        { return currentSong.getArtistName(); }

    public Song getSong()                { return currentSong; }

    public int getSongLength()           { return currentSong.getSongLength(); }

    public String getSongLengthString()  { return currentSong.getSongLengthString(); }

    public String getSongName()          { return currentSong.getSongName(); }

    public int getSongPosition()         { return songPosition.get(); }

    public BufferedImage getScaledAlbumArt(int w, int h) { return currentSong.getScaledAlbumArt(w, h); }

    public synchronized boolean isPlaying() { return isPlaying; }

    public synchronized void playToggle()
    {
        if(!isPlaying)
            controller.play();
        else
            controller.pause();

        setPlaying(!isPlaying);
    }

    public void setSong(Song song) { currentSong = song; }

    /**
     * Set the current position in the song and provide length if needed.
     * @param pos new song position
     */
    public void setSongPosition(int pos) { songPosition.set(pos); }

    public void setVolume(int value) { controller.setVolume(value); }

    public synchronized void updateSong()
    {
        isPlaying = true;
        controller.updateSong();
    }

    private void setPlaying(boolean playing)
    {
        isPlaying = playing;
    }
}
