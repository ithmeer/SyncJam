package syncjam.net.client;

import syncjam.SongUtilities;
import syncjam.interfaces.CommandQueue;
import syncjam.net.SocketProducer;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Listen on a socket for commands.
 * Created by Ithmeer on 11/12/2015.
 */
public class ClientProducer extends SocketProducer
{
    public ClientProducer(OutputStream outStream, SongUtilities songUtils)
    {
        super(outStream, songUtils);
    }

    public void run()
    {
        CommandQueue cmdQueue = _songUtils.getCommandQueue();
        cmdQueue.toggleEnabled(true);

        while (!terminated)
        {
            try
            {
                String command = cmdQueue.take();
                System.out.println("produced command: " + command);
                _outputStream.write(command.getBytes());
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
