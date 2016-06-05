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

    // synchronized on this
    private boolean _enabled;

    public CommandQueue(NowPlaying player, Playlist playlist)
    {
        _queue = new LinkedBlockingQueue<String>();
        _player = player;
        _playlist = playlist;

        synchronized (this)
        {
            _enabled = false;
        }
    }

    public synchronized void toggleEnabled(boolean state)
    {
        _enabled = state;
    }

    public synchronized void executeCommand(String cmdBuffer)
    {
        char second = cmdBuffer.charAt(1);
        _enabled = false;

        switch (cmdBuffer.charAt(0))
        {
            case 'G':
                _playlist.setCurrentSong(second);
                break;
            case 'M':
                _playlist.moveSong(second, cmdBuffer.charAt(2));
                break;
            case 'N':
                _playlist.nextSong();
                break;
            case 'P':
                _player.playToggle(true);
                if (second == 'L')
                    _player.playToggle(true);
                else if (second == 'S')
                    _playlist.prevSong();
                break;
            case 'R':
                _playlist.remove(second);
                break;
            case 'S':
                if (second == 'T')
                {
                    _player.playToggle(false);
                }
                else
                {
                    int pos = Math.round(
                            (cmdBuffer.charAt(1) / 100.0f) * (float) _player.getSongLength());
                    _player.setSongPosition(pos);
                }
                break;
        }

        _enabled = true;
    }

    public synchronized void gotoSong(int song)
    {
        if (_enabled)
        {
            _queue.add(String.format("G%c", song));
        }
    }

    public synchronized void nextSong()
    {
        if (_enabled)
        {
            _queue.add("NS");
        }
    }

    public synchronized void prevSong()
    {
        if (_enabled)
        {
            _queue.add("PS");
        }
    }

    public synchronized void moveSong(int from, int to)
    {
        if (_enabled)
        {
            _queue.add(String.format("M%c%c", from, to));
        }
    }

    public synchronized void removeSong(int song)
    {
        if (_enabled)
        {
            _queue.add(String.format("R%c", song));
        }
    }

    public synchronized void playToggle(boolean state)
    {
        if (_enabled)
        {
            if (state)
                _queue.add("PL");
            else
                _queue.add("ST");
        }
    }

    public synchronized void seek(int percent)
    {
        if (_enabled)
        {
            _queue.add(String.format("S%c", percent));
        }
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
