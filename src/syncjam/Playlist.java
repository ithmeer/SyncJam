package syncjam;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * Created by Marty on 2/26/2015.
 */
public class Playlist
{
    private final LinkedBlockingDeque<Song> songList = new LinkedBlockingDeque<Song>();
    private final LinkedList<Song> cache = new LinkedList<Song>();

    private int currentSong = -1;

    public void offer(Song s)
    {
        songList.offer(s);
    }

    public void remove(int i)
    {
        songList.remove(i);
        if(currentSong > i)
            currentSong -= 1;
    }

    public Song take() throws InterruptedException
    {
        Song next = songList.take();
        cache.add(next);
        return next;
    }

    public Iterator<Song> iterator()
    {
        return songList.iterator();
    }

    public int size()
    {
        return songList.size();
    }
}
