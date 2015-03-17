package syncjam;

import java.util.ArrayList;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * Created by Marty on 2/26/2015.
 */
public class Playlist
{
    private ArrayList<Song> songList = new ArrayList<Song>();

    private int currentSong = -1;

    public void add(Song s)
    {
        songList.add(s);
    }

    public void remove(int i)
    {
        songList.remove(i);
        if(currentSong > i)
            currentSong -= 1;
    }

    public Song get(int i)
    {
        return songList.get(i);
    }

    public Song getSong()
    {
        if(songList.size() == 0) return null;

        return songList.get(currentSong);
    }

    public Song getNextSong()
    {
        if(songList.size() == 0) return null;

        if(currentSong < songList.size()-1)
        {
            currentSong++;
        }
        return songList.get(currentSong);
    }

    public Song getPrevSong()
    {
        if(songList.size() == 0) return null;

        if(currentSong > 0)
        {
            currentSong--;
        }
        return songList.get(currentSong);
    }

    public int size()
    {
        return songList.size();
    }
}
