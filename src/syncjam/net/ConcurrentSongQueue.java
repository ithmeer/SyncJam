package syncjam.net;

import syncjam.BytesSong;
import syncjam.interfaces.Song;
import syncjam.interfaces.SongQueue;

import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by Ithmeer on 9/11/2016.
 */
public class ConcurrentSongQueue extends LinkedBlockingQueue<Song> implements SongQueue
{
}
