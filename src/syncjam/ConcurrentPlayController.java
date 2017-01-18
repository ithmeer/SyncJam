package syncjam;

import syncjam.interfaces.*;

import java.awt.image.BufferedImage;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Class to provide access to the currently playing song. Thread-safe.
 *  Expects that only one thread will ever set a new song (the thread that drives AudioController).
 */
public class ConcurrentPlayController implements PlayController
{
    private volatile AudioController _audioController;
    private volatile CommandQueue _cmdQueue;
    private volatile Playlist _playlist;

    private volatile Song currentSong;
    private boolean isPlaying = false; // synchronized on this
    private final AtomicInteger songPosition = new AtomicInteger(0);

    @Override
    public BufferedImage getAlbumArt()   { return currentSong.getAlbumArt(); }

    @Override
    public String getAlbumName()         { return currentSong.getAlbumName(); }

    @Override
    public String getArtistName()        { return currentSong.getArtistName(); }

    @Override
    public Song getSong()                { return currentSong; }

    @Override
    public int getSongLength()           { return currentSong.getLength(); }

    @Override
    public String getSongLengthString()  { return currentSong.getLengthString(); }

    @Override
    public String getSongName()          { return currentSong.getTitle(); }

    @Override
    public int getSongPosition()         { return songPosition.get(); }

    @Override
    public BufferedImage getScaledAlbumArt(int w, int h) { return currentSong.getScaledAlbumArt(w, h); }

    @Override
    public synchronized boolean isPlaying() { return isPlaying; }

    @Override
    public synchronized void playToggle(boolean state)
    {
        if (currentSong == null)
            return;

        if(state)
        {
            _audioController.play();
            _playlist.wakeUp();
        }
        else
        {
            _audioController.pause();
        }
        _cmdQueue.playToggle(state);

        setPlaying(state);
    }

    @Override
    public void playToggle()
    {
        playToggle(!isPlaying);
    }

    @Override
    public void setSong(Song song) { currentSong = song; }

    /**
     * Set the current position in the song and provide length if needed.
     * @param pos new song position
     */
    @Override
    public void setSongPosition(int pos)
    {
        songPosition.set(pos);
    }

    @Override
    public void setVolume(int value) { _audioController.setVolume(value); }

    @Override
    public synchronized void updateSong()
    {
        isPlaying = true;
        _audioController.updateSong();
    }

    public void setAudioController(AudioController audioCon)
    {
        _audioController = audioCon;
    }

    public void setCommandQueue(CommandQueue cmdQueue)
    {
        _cmdQueue = cmdQueue;
    }

    public void setPlaylist(Playlist pl)
    {
        _playlist = pl;
    }

    private void setPlaying(boolean playing)
    {
        isPlaying = playing;
    }
}
