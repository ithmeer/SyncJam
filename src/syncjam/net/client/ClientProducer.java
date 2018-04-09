package syncjam.net.client;

import syncjam.SyncJamException;
import syncjam.interfaces.CommandQueue;
import syncjam.interfaces.ServiceContainer;
import syncjam.net.CommandPacket;
import syncjam.net.SocketProducer;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;

/**
 * Listen on a socket for commands.
 * Created by Ithmeer on 11/12/2015.
 */
public class ClientProducer extends SocketProducer
{
    protected final CommandQueue _cmdQueue;

    public ClientProducer(OutputStream outStream, ServiceContainer services)
    {
        super(outStream);
        _cmdQueue = services.getService(CommandQueue.class);
    }

    public void run()
    {
        try
        {
            _cmdQueue.toggleEnabled(true);
            ObjectOutputStream socketObjectWriter = new ObjectOutputStream(_outputStream);

            while (!_terminated.get())
            {
                CommandPacket packet = _cmdQueue.take();
                System.out.println("produced command: " + packet.toString());
                _cmdQueue.executeCommand(packet);
                socketObjectWriter.writeObject(packet);
            }
        }
        catch (InterruptedException | IOException e)
        {
            // TODO: log error
            e.printStackTrace();
            throw new SyncJamException(e.getMessage());
        }
    }
}
