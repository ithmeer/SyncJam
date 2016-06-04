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
    protected final CommandQueue _queue;
    protected final OutputStream _socketOutputStream;

    public SocketProducer(OutputStream outStream, CommandQueue queue)
    {
        _socketOutputStream = outStream;
        _queue = queue;
    }

    public void run()
    {
        while (!terminated)
        {
            try
            {
                String command = _queue.take();
                System.out.println("produced command: " + command);
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
