package syncjam;

import syncjam.net.CommandQueue;

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

    private volatile CommandQueue _queue;

    // track the index of the currently playing (or to be played) song
    private int _currentSong = 0;

    private boolean _intermediate = false;
    
    public Playlist(NowPlaying playCon)
    {
        _playController = playCon;
    }

    public void setCommandQueue(CommandQueue cq)
    {
        _queue = cq;
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
            return _intermediate || waitingForSong() ? _currentSong : _currentSong - 1;
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
            _playController.playToggle(false);

            while (_currentSong == _songList.size())
            {
                // block until more songs are added or a different song is selected
                _songList.wait();
            }

            // we have a new song, unpause
            _playController.playToggle(true);

            _intermediate = false;
            return _songList.get(_currentSong++);
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
        _queue.nextSong();
        _playController.updateSong();
    }

    /**
     * Go back two songs since currentSong was already stepped.
     */
    public void prevSong()
    {
        synchronized (_songList)
        {
            if (_currentSong != 0 && !_songList.isEmpty())
            {
                if (waitingForSong())
                {
                    _currentSong--;
                    _songList.notify();
                }
                else if (_currentSong == 1)
                {
                    // if playing first song, just restart
                    _currentSong--;
                    _playController.updateSong();
                }
                else
                {
                    // playing new song, so currentSong was stepped twice
                    _intermediate = true;
                    _currentSong -= 2;
                    _playController.updateSong();
                }
                _queue.prevSong();
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
            if (_currentSong > i)
                _currentSong -= 1;
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
            _intermediate = true;
            _currentSong = which;
            _songList.notify();
        }
        _queue.gotoSong(which);
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
     * Move a song between the given indices.
     * @param from first index
     * @param to second index
     */
    public void moveSong(int from, int to)
    {
        synchronized (_songList)
        {
            Song toSwap = _songList.remove(from);

            if (to > from)
                to--;

            _songList.add(to, toSwap);
            _queue.moveSong(from, to);
        }
    }

    // no need to synchronize, called from synchronized blocks
    private boolean waitingForSong()
    {
        return _currentSong == _songList.size() && !_playController.isPlaying();
    }
}
