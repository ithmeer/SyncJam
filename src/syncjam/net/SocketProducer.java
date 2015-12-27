package syncjam.net;

import syncjam.SongUtilities;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.WritableByteChannel;

/**
 * Listen on a socket for commands.
 * Created by Ithmeer on 11/12/2015.
 */
public class SocketProducer extends InterruptableRunnable implements Runnable
{
    private final CommandQueue commandQueue;
    private final OutputStream _socketOutputStream;

    public SocketProducer(OutputStream outStream, SongUtilities songUtils)
    {
        _socketOutputStream = outStream;
        commandQueue = songUtils.getCommandQueue();
    }

    public void run()
    {
        while (!terminated)
        {
            try
            {
                String command = commandQueue.take();
                _socketOutputStream.write(command.getBytes());
            }
            catch (InterruptedException e)
            {
                break;
            }
            catch (IOException e)
            {
                break;
            }
        }

    }
}
