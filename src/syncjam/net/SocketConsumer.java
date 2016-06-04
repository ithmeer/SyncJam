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
public class SocketConsumer extends InterruptableRunnable
{
    protected final InputStream _socketInputStream;
    protected final CommandQueue _queue;

    public SocketConsumer(InputStream inStream, CommandQueue queue)
    {
        _socketInputStream = inStream;
        _queue = queue;
    }

    public void run()
    {
        byte[] commandBuffer = new byte[3];
        while (!terminated)
        {
            try
            {
                _socketInputStream.read(commandBuffer);
                System.out.println("consumed command: " + commandBuffer.toString());
                _queue.executeCommand(commandBuffer);
            }
            catch (IOException e)
            {
                break;
            }
        }
    }
}
