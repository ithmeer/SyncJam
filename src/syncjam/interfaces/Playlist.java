package syncjam.interfaces;

import syncjam.BytesSong;

import java.util.Iterator;

/**
 * Created by Ithmeer on 9/11/2016.
 */
public interface Playlist
{
    void add(Song s);

    void addAll(Song... songs);

    void clear();

    int getCurrentSongIndex();

    Song getNextSong() throws InterruptedException;

    Iterator<Song> iterator();

    void moveSong(int from, int to);

    void nextSong();

    void prevSong();

    void remove(int i);

    void setCurrentSong(int which);

    int size();

    void wakeUp();
}
