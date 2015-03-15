package syncjam;

import java.util.ArrayList;

/**
 * Created by Marty on 2/26/2015.
 */
public class Playlist
{
    public static ArrayList<Song> songList = new ArrayList<Song>();
    public static int currentSong = -1;

    public static void add(Song s)
    {
        songList.add(s);
    }
    public static void remove(int i)
    {
        songList.remove(i);
        if(currentSong > i)
            currentSong -= 1;
    }
    public static Song get(int i)
    {
        return songList.get(i);
    }

    public static Song getSong()
    {
        if(songList.size() == 0) return null;

        return songList.get(currentSong);
    }

    public static Song getNextSong()
    {
        if(songList.size() == 0) return null;

        if(currentSong < songList.size()-1)
        {
            currentSong++;
        }
        return songList.get(currentSong);
    }

    public static Song getPrevSong()
    {
        if(songList.size() == 0) return null;

        if(currentSong > 0)
        {
            currentSong--;
        }
        return songList.get(currentSong);
    }

    public static int size()
    {
        return songList.size();
    }
}
