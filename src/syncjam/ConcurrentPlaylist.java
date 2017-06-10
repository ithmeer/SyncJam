package syncjam;

import syncjam.interfaces.CommandQueue;
import syncjam.interfaces.PlayController;
import syncjam.interfaces.Playlist;
import syncjam.interfaces.Song;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * A list of songs to be played. Thread-safe.
 * Created by Marty on 2/26/2015.
 * Modified by Ithmeer.
 */
public class ConcurrentPlaylist implements Playlist
{
    // a thread-safe ArrayList to store the songs (synchronized on itself)
    private final List<Song> _songList = new CopyOnWriteArrayList<>();
    private final PlayController _playController;

    private volatile CommandQueue _cmdQueue;

    // track the index of the currently playing (or to be played) song
    private int _currentSong = 0;

    private boolean _intermediate = false;
    
    public ConcurrentPlaylist(PlayController playCon)
    {
        _playController = playCon;
    }

    /**
     * Add one song onto the queue.
     * @param s the song
     */
    @Override
    public void add(Song s)
    {
        addAll(s);
    }

    /**
     * Add multiple songs to the end of the playlist.
     * @param songs one or more songs to add
     */
    @Override
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
    @Override
    public void clear()
    {
        synchronized (_songList)
        {
            _songList.clear();
            _playController.updateSong();
        }
    }

    @Override
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
    @Override
    public Song getNextSong() throws InterruptedException
    {
        synchronized (_songList)
        {
            // if empty, pause
            _playController.playToggle(false);

            while (waitingForSong())
            {
                // block until more songs are added or a different song is selected
                _songList.wait();
            }

            if (_currentSong == _songList.size())
                _currentSong = 0;

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
    @Override
    public Iterator<Song> iterator()
    {
        return _songList.iterator();
    }

    /**
     * Move a song between the given indices.
     * @param from first index
     * @param to second index
     */
    @Override
    public void moveSong(int from, int to)
    {
        _cmdQueue.moveSong(from, to);

        synchronized (_songList)
        {
            Song toSwap = _songList.remove(from);

            if(from + 1 == _currentSong)
            {
                if(to > from)
                    _currentSong = to;
                else
                    _currentSong = to + 1;
            }
            else if(to < _currentSong && from >= _currentSong)
                _currentSong += 1;
            else if(to >= _currentSong && from < _currentSong)
                _currentSong -= 1;

            if (to > from)
                to--;

            _songList.add(to, toSwap);
        }
    }

    /**
     * Simply call updateSong, currentSong was already incremented.
     */
    @Override
    public void nextSong()
    {
        synchronized (_songList)
        {
            if (waitingForSong())
                return;
        }
        _cmdQueue.nextSong();
        _playController.updateSong();
    }

    /**
     * Go back two songs since currentSong was already stepped.
     */
    @Override
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
                _cmdQueue.prevSong();
            }
        }
    }

    /**
     * Remove the song at the given position from the playlist.
     * @param i the index to remove
     */
    @Override
    public void remove(int i)
    {
        synchronized (_songList)
        {
            if (i < 0 || i >= _songList.size())
                return;

            _cmdQueue.removeSong(i);
            _songList.remove(i);
            if (_currentSong > i)
                _currentSong -= 1;
        }
    }

    /**
     * Start playing the song at a given index.
     * @param which the index to play
     */
    @Override
    public void setCurrentSong(int which)
    {
        synchronized (_songList)
        {
            _intermediate = true;
            _currentSong = which;
            _songList.notify();
        }
        _cmdQueue.gotoSong(which);
        _playController.updateSong();
    }

    /**
     * Get the size of the list.
     * @return list size
     */
    @Override
    public int size()
    {
        synchronized (_songList)
        {
            return _songList.size();
        }
    }

    @Override
    public void wakeUp()
    {
        synchronized (_songList)
        {
            _songList.notify();
        }
    }

    public void setCommandQueue(CommandQueue cq)
    {
        _cmdQueue = cq;
    }

    // no need to synchronize, called from synchronized blocks
    private boolean waitingForSong()
    {
        return _currentSong == _songList.size() && !_playController.isPlaying();
    }
}
