package syncjam.net.client;

import syncjam.Song;
import syncjam.SongUtilities;
import syncjam.interfaces.CommandQueue;
import syncjam.net.InterruptableRunnable;
import syncjam.net.SocketConsumer;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.BlockingQueue;

/**
 * Listen on a socket for commands.
 * Created by Ithmeer on 7/6/2015.
 */
public class ClientConsumer extends SocketConsumer
{
    public ClientConsumer(InputStream inStream, SongUtilities songUtils)
    {
        super(inStream, songUtils);
    }

    public void run()
    {
        CommandQueue cmdQueue = _songUtils.getCommandQueue();
        byte[] commandBuffer = new byte[3];

        while (!terminated)
        {
            try
            {
                _inputStream.read(commandBuffer);
                String command = new String(commandBuffer);
                System.out.println("consumed command: " + command);
                cmdQueue.executeCommand(command);
            }
            catch (IOException e)
            {
                break;
            }
        }
    }
}
