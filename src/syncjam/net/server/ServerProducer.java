package syncjam.net.server;

import syncjam.SongUtilities;
import syncjam.interfaces.CommandQueue;
import syncjam.net.SocketProducer;

import java.io.OutputStream;

/**
 * Created by Ithmeer on 1/5/2016.
 */
public class ServerProducer extends SocketProducer
{
    private final Iterable<ServerSideSocket> _clients;

    public ServerProducer(OutputStream outStream, SongUtilities songUtils,
                          Iterable<ServerSideSocket> clients)
    {
        super(outStream, songUtils);
        _clients = clients;
    }

    @Override
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
                for (ServerSideSocket client : _clients)
                {
                    client.sendCommand(command);
                }
            }
            catch (InterruptedException e)
            {
                break;
            }
        }
    }
}
