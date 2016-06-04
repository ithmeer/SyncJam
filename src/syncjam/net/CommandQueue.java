package syncjam.net;

import syncjam.NowPlaying;
import syncjam.Playlist;

import java.nio.ByteBuffer;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Class to handle the commands to send to the socket. Thread-safe.
 * Created by Ithmeer on 10/23/2015.
 */
public class CommandQueue
{
    private final LinkedBlockingQueue<String> _queue;
    private final NowPlaying _player;
    private final Playlist _playlist;

    public CommandQueue(NowPlaying player, Playlist playlist)
    {
        _queue = new LinkedBlockingQueue<String>();
        _player = player;
        _playlist = playlist;
    }

    public void executeCommand(byte[] cmdBuffer)
    {
        byte first = cmdBuffer[0];
        if (first == 'G')
        {
            _playlist.setCurrentSong(cmdBuffer[1]);
        }
        else if (first == 'M')
        {
            _playlist.moveSong(cmdBuffer[1], cmdBuffer[2]);
        }
        else if (first == 'P')
        {
            _player.playToggle();
        }
        else if (first == 'S')
        {
            _player.setSongPosition(cmdBuffer[1]);
        }
    }

    public void gotoSong(int song)
    {
        if (song >= 0)
            _queue.add("G" + (byte) song);
    }

    public void moveSong(int from, int to)
    {
        if (from > 0 && to > 0)
            _queue.add("M" + (byte) from + (byte) to);
    }

    public void playToggle()
    {
        _queue.add("PL");
    }

    public void seek(int percent)
    {
        if (percent >= 0 && percent <= 100)
            _queue.add("S" + (byte) percent);
    }

    /**
     * Blocking method to pop the front of the queue.
     * @return the String at the front of the queue
     * @throws InterruptedException
     */
    public String take() throws InterruptedException
    {
        return _queue.take();
    }
}
