package syncjam.net.server;

import syncjam.interfaces.CommandQueue;
import syncjam.interfaces.ServiceContainer;
import syncjam.net.SocketProducer;

import java.io.OutputStream;

/**
 * Created by Ithmeer on 1/5/2016.
 */
public class ServerProducer extends SocketProducer
{
    private final Iterable<ServerSideSocket> _clients;
    private final CommandQueue _cmdQueue;

    public ServerProducer(OutputStream outStream, ServiceContainer services,
                          Iterable<ServerSideSocket> clients)
    {
        super(outStream);
        _clients = clients;
        _cmdQueue = services.getService(CommandQueue.class);
    }

    @Override
    public void run()
    {
        _cmdQueue.toggleEnabled(true);

        while (!_terminated.get())
        {
            try
            {
                String command = _cmdQueue.take();
                System.out.println("produced command: " + command);
                _cmdQueue.executeCommand(command);
                for (ServerSideSocket client : _clients)
                {
                    client.sendCommand(command);
                }
            }
            catch (InterruptedException e)
            {
                // TODO: log error
                break;
            }
        }
    }
}
