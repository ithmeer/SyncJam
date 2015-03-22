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
    private final List<Song> songList = Collections.synchronizedList(new ArrayList<Song>());

    // track the index of the currently playing (or to be played) song
    private int currentSong = 0;

    /**
     * Add one song onto the queue.
     * @param s
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
        synchronized (songList)
        {
            for (Song song : songs)
            {
                songList.add(song);
            }
            songList.notify();
        }
    }

    public int getCurrentSongIndex()
    {
        return waitingForSong() ? currentSong : currentSong - 1;
    }

    /**
     * Get the next song to be played or block if at end.
     * Synchronized block is fine, wait will unblock.
     * @return the next song to play
     * @throws InterruptedException
     */
    public Song getNextSong() throws InterruptedException
    {
        synchronized (songList)
        {
            // if empty, pause
            if (NowPlaying.isPlaying())
                NowPlaying.playToggle();

            while (currentSong == songList.size())
            {
                // block until more songs are added or a different song is selected
                songList.wait();
            }

            if (!NowPlaying.isPlaying())
                NowPlaying.playToggle();

            Song next = songList.get(currentSong++);
            return next;
        }
    }

    /**
     * Return an unmodifiable iterator, do not try to call remove.
     * @return the iterator
     */
    public Iterator<Song> iterator()
    {
        synchronized (songList)
        {
            return Collections.unmodifiableList(songList).iterator();
        }
    }

    /**
     * Simply call updateSong, currentSong was already incremented.
     */
    public void nextSong()
    {
        synchronized (songList)
        {
            if (waitingForSong())
                return;
        }
        NowPlaying.updateSong();
    }

    /**
     * Go back two songs since currentSong was already stepped.
     */
    public void prevSong()
    {
        synchronized (songList)
        {
            if (currentSong == 0 || songList.isEmpty())
                return;
            else if (waitingForSong())
            {
                currentSong--;
                songList.notify();
            }
            else if (currentSong == 1)
            {
                // if playing first song, just restart
                currentSong--;
                NowPlaying.updateSong();
            }
            else
            {
                // playing new song, so currentSong was stepped twice
                currentSong -= 1;
                NowPlaying.updateSong();
            }
        }
    }

    /**
     * Remove the song at the given position from the playlist.
     * @param i
     */
    public void remove(int i)
    {
        synchronized (songList)
        {
            if (i < 0 || i >= songList.size())
                return;

            songList.remove(i);
            if (currentSong > i)
                currentSong -= 1;
        }
    }

    /**
     * Start playing the song at a given index.
     * @param which
     */
    public void setCurrentSong(int which)
    {
        synchronized (songList)
        {
            currentSong = which;
            songList.notify();
        }
        NowPlaying.updateSong();
    }

    /**
     * Get the size of the list.
     * @return
     */
    public int size()
    {
        synchronized (songList)
        {
            return songList.size();
        }
    }

    /**
     * Swap the songs at the given indices.
     * @param from
     * @param to
     */
    public void swapSongs(int from, int to)
    {
        synchronized (songList)
        {
            Song toSwap = songList.get(from);
            songList.set(from, songList.get(to));
            songList.set(to, toSwap);
        }
    }

    // no need to synchronize, called from synchronized blocks
    private boolean waitingForSong()
    {
        return currentSong == songList.size() && !NowPlaying.isPlaying();
    }
}
