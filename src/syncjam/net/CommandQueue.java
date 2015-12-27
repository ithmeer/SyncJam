package syncjam.net;

import syncjam.NowPlaying;
import syncjam.Playlist;

import java.nio.ByteBuffer;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Class to handle the commands to send to the socket.
 * Created by Ithmeer on 10/23/2015.
 */
public class CommandQueue
{
    private final LinkedBlockingQueue<String> _queue;

    public CommandQueue()
    {
        _queue = new LinkedBlockingQueue<String>();
    }

    public void executeCommand(byte[] cmdBuffer, NowPlaying player, Playlist playlist)
    {
        byte first = cmdBuffer[0];
        if (first == 'G')
        {
            playlist.setCurrentSong(cmdBuffer[1]);
        }
        else if (first == 'M')
        {
            playlist.moveSong(cmdBuffer[1], cmdBuffer[2]);
        }
        else if (first == 'P')
        {
            player.playToggle();
        }
        else if (first == 'S')
        {
            player.setSongPosition(cmdBuffer[1]);
        }
    }

    public void gotoSong(int song)
    {
        if (song >= 0)
            _queue.add("G" + (byte) song);
    }

    public void playToggle()
    {
        _queue.add("P");
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
