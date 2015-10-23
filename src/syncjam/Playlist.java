package syncjam;

import java.util.*;

/**
 * A list of songs to be played. Thread-safe.
 * Created by Marty on 2/26/2015.
 * Modified by Ithmeer.
 */
public class Playlist
{
    // a synchronized ArrayList to store the songs
    private final List<Song> _songList = Collections.synchronizedList(new ArrayList<Song>());
    private final NowPlaying _playController;

    // track the index of the currently playing (or to be played) song
    private int currentSong = 0;

    private boolean intermediate = false;
    
    public Playlist(NowPlaying playCon)
    {
        _playController = playCon;
    }

    /**
     * Add one song onto the queue.
     * @param s the song
     */
    public void add(Song s)
    {
        addAll(s);
    }

    /**
     * Add multiple songs to the end of the playlist.
     * @param songs one or more songs to add
     */
    public void addAll(Song... songs)
    {
        synchronized (_songList)
        {
            Collections.addAll(_songList, songs);
            _songList.notify();
        }
    }

    /**
     * Clear the playlist, keeping it blocked if it's blocked.
     */
    public void clear()
    {
        synchronized (_songList)
        {
            _songList.clear();
            _playController.updateSong();
        }

    }

    public int getCurrentSongIndex()
    {
        synchronized (_songList)
        {
            return intermediate || waitingForSong() ? currentSong : currentSong - 1;
        }
    }

    /**
     * Get the next song to be played or block if at end.
     * Synchronized block is fine, wait will unblock.
     * @return the next song to play
     * @throws InterruptedException
     */
    public Song getNextSong() throws InterruptedException
    {
        synchronized (_songList)
        {
            // if empty, pause
            if (_playController.isPlaying())
                _playController.playToggle();

            while (currentSong == _songList.size())
            {
                // block until more songs are added or a different song is selected
                _songList.wait();
            }

            // we have a new song, unpause
            if (!_playController.isPlaying())
                _playController.playToggle();

            intermediate = false;
            return _songList.get(currentSong++);
        }
    }

    /**
     * Return an unmodifiable iterator, do not try to call remove.
     * @return the iterator
     */
    public Iterator<Song> iterator()
    {
        synchronized (_songList)
        {
            return Collections.unmodifiableList(_songList).iterator();
        }
    }

    /**
     * Simply call updateSong, currentSong was already incremented.
     */
    public void nextSong()
    {
        synchronized (_songList)
        {
            if (waitingForSong())
                return;
        }
        _playController.updateSong();
    }

    /**
     * Go back two songs since currentSong was already stepped.
     */
    public void prevSong()
    {
        synchronized (_songList)
        {
            if (currentSong != 0 && !_songList.isEmpty())
            {
                if (waitingForSong())
                {
                    currentSong--;
                    _songList.notify();
                }
                else if (currentSong == 1)
                {
                    // if playing first song, just restart
                    currentSong--;
                    _playController.updateSong();
                }
                else
                {
                    // playing new song, so currentSong was stepped twice
                    intermediate = true;
                    currentSong -= 2;
                    _playController.updateSong();
                }
            }
        }
    }

    /**
     * Remove the song at the given position from the playlist.
     * @param i the index to remove
     */
    public void remove(int i)
    {
        synchronized (_songList)
        {
            if (i < 0 || i >= _songList.size())
                return;

            _songList.remove(i);
            if (currentSong > i)
                currentSong -= 1;
        }
    }

    /**
     * Start playing the song at a given index.
     * @param which the index to play
     */
    public void setCurrentSong(int which)
    {
        synchronized (_songList)
        {
            intermediate = true;
            currentSong = which;
            _songList.notify();
        }
        _playController.updateSong();
    }

    /**
     * Get the size of the list.
     * @return list size
     */
    public int size()
    {
        synchronized (_songList)
        {
            return _songList.size();
        }
    }

    /**
     * Swap the songs at the given indices.
     * @param from first index
     * @param to second index
     */
    public void swapSongs(int from, int to)
    {
        synchronized (_songList)
        {
            Song toSwap = _songList.get(from);
            _songList.set(from, _songList.get(to));
            _songList.set(to, toSwap);
        }
    }

    // no need to synchronize, called from synchronized blocks
    private boolean waitingForSong()
    {
        return currentSong == _songList.size() && !_playController.isPlaying();
    }
}
