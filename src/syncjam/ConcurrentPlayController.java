package syncjam;

import syncjam.interfaces.*;

import java.awt.image.BufferedImage;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Class to provide access to the currently playing song. Thread-safe.
 *  Expects that only one thread will ever set a new song (the thread that drives AudioController).
 */
public class ConcurrentPlayController implements PlayController
{
    private volatile AudioController _audioController;
    private volatile CommandQueue _cmdQueue;
    private volatile Playlist _playlist;

    private AtomicReference<Song> currentSong = new AtomicReference<>();
    private boolean isPlaying; // synchronized on this
    private final AtomicInteger songPosition = new AtomicInteger(0);
    private final AtomicInteger _nextSeekPosition = new AtomicInteger(-1);

    public ConcurrentPlayController()
    {
        synchronized (this)
        {
            isPlaying = false;
        }
    }

    @Override
    public BufferedImage getAlbumArt()   { return currentSong.get().getAlbumArt(); }

    @Override
    public String getAlbumName()         { return currentSong.get().getAlbumName(); }

    @Override
    public String getArtistName()        { return currentSong.get().getArtistName(); }

    @Override
    public Song getSong()                { return currentSong.get(); }

    @Override
    public int getSongLength()           { return currentSong.get().getLength(); }

    @Override
    public String getSongLengthString()  { return currentSong.get().getLengthString(); }

    @Override
    public String getSongName()          { return currentSong.get().getTitle(); }

    @Override
    public int getSongPosition()         { return songPosition.get(); }

    @Override
    public BufferedImage getScaledAlbumArt(int w, int h) { return currentSong.get().getScaledAlbumArt(w, h); }

    @Override
    public synchronized boolean isPlaying() { return isPlaying; }

    @Override
    public synchronized void playToggle(boolean state)
    {
        boolean oldState = isPlaying;

        if (currentSong == null || state == oldState)
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
    public synchronized void playToggle()
    {
        playToggle(!isPlaying);
    }

    @Override
    public void setSong(Song song)
    {
        currentSong.set(song);
    }

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
    public int getNextSeekPosition()
    {
        return _nextSeekPosition.getAndSet(-1);
    }

    @Override
    public void setNextSeekPosition(int pos)
    {
        _nextSeekPosition.set(pos);
    }

    @Override
    public void setVolume(int value) { _audioController.setVolume(value); }

    @Override
    public synchronized void updateSong()
    {
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
