package syncjam;

import java.util.*;

/**
 * A list of songs to be played. Thread-safe. Assumes only one thread will call
 * Created by Marty on 2/26/2015.
 */
public class Playlist
{
    private final List<Song> songList = Collections.synchronizedList(new ArrayList<Song>());

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
     * @param songs
     */
    public void addAll(Song... songs)
    {
        synchronized (songList)
        {
            boolean wasWaiting = waitingForSong();
            for (Song song : songs)
            {
                songList.add(song);
            }
            if (wasWaiting)
                songList.notify();
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
        synchronized (songList)
        {
            // if empty, pause
            if (waitingForSong())
                NowPlaying.playToggle();
            while (waitingForSong())
            {
                songList.wait();
            }
            Song next = songList.get(currentSong);
            currentSong++;
            return next;
        }
    }

    public Iterator<Song> iterator()
    {
        synchronized (songList)
        {
            return Collections.unmodifiableList(songList).iterator();
        }
    }

    public void nextSong()
    {
        synchronized (songList)
        {
            if (songList.isEmpty())
                return;
        }
        NowPlaying.updateSong();
    }

    public void prevSong()
    {
        synchronized (songList)
        {
            if (songList.isEmpty())
                return;
            currentSong -= 2;
        }
        NowPlaying.updateSong();
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

    public int size()
    {
        synchronized (songList)
        {
            return songList.size();
        }
    }

    public void swap(int from, int to)
    {
        synchronized (songList)
        {
            Song toSwap = songList.get(from);
            songList.set(from, songList.get(to));
            songList.set(to, toSwap);
        }
    }

    private boolean waitingForSong()
    {
        return currentSong == songList.size();
    }
}
