package syncjam.net.server;

import syncjam.SyncJamException;
import syncjam.interfaces.CommandQueue;
import syncjam.interfaces.ServiceContainer;
import syncjam.net.CommandPacket;
import syncjam.net.SocketProducer;
import syncjam.utilities.CommandFlags;

import java.io.IOException;
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
                CommandPacket packet = _cmdQueue.take();
                System.out.println("produced command: " + packet.toString());
                if (!packet.getFlags().contains(CommandFlags.Suppressed))
                {
                    _cmdQueue.executeCommand(packet);
                }
                for (ServerSideSocket client : _clients)
                {
                    client.sendCommand(packet);
                }
            }
            catch (IOException | InterruptedException e)
            {
                // TODO: log error
                e.printStackTrace();
                throw new SyncJamException(e.getMessage());
            }
        }
    }
}
