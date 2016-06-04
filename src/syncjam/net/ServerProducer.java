package syncjam.net;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Created by Ithmeer on 1/5/2016.
 */
public class ServerProducer extends SocketProducer
{
    private final Iterable<ServerSideSocket> _clients;

    public ServerProducer(OutputStream outStream, CommandQueue queue,
                          Iterable<ServerSideSocket> clients)
    {
        super(outStream, queue);
        _clients = clients;
    }

    @Override
    public void run()
    {
        while (!terminated)
        {
            try
            {
                String command = _queue.take();
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
