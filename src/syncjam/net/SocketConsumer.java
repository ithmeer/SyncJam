package syncjam.net;

import syncjam.NowPlaying;
import syncjam.SongUtilities;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedByInterruptException;
import java.nio.channels.ReadableByteChannel;
import java.util.concurrent.BlockingQueue;

/**
 * Listen on a socket for commands.
 * Created by Ithmeer on 7/6/2015.
 */
public class SocketConsumer extends InterruptableRunnable implements Runnable
{
    private final InputStream _socketInputStream;
    private final SongUtilities _utils;

    public SocketConsumer(InputStream inStream, SongUtilities utils)
    {
        _socketInputStream = inStream;
        _utils = utils;
    }

    public void run()
    {
        byte[] commandBuffer = new byte[3];
        while (!terminated)
        {
            try
            {
                _socketInputStream.read(commandBuffer);
                _utils.getCommandQueue().executeCommand(commandBuffer, _utils.getPlayer(), _utils.getPlaylist());
            }
            catch (IOException e)
            {
                break;
            }
        }

    }
}
