package syncjam.net.client;

import syncjam.SyncJamException;
import syncjam.interfaces.CommandQueue;
import syncjam.interfaces.ServiceContainer;
import syncjam.net.CommandPacket;
import syncjam.net.SocketConsumer;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;

/**
 * Listen on a socket for commands.
 * Created by Ithmeer on 7/6/2015.
 */
public class ClientConsumer extends SocketConsumer
{
    protected final CommandQueue _cmdQueue;

    public ClientConsumer(InputStream inStream, ServiceContainer services)
    {
        super(inStream);
        _cmdQueue = services.getService(CommandQueue.class);
    }

    public void run()
    {
        try
        {
            ObjectInputStream socketObjectReader = new ObjectInputStream(_inputStream);

            while (!_terminated.get())
            {
                CommandPacket packet = (CommandPacket) socketObjectReader.readObject();
                System.out.println("consumed command: " + packet.toString());
                _cmdQueue.executeCommand(packet);
            }
        }
        catch (ClassNotFoundException | IOException e)
        {
            // TODO: log error
            e.printStackTrace();
            throw new SyncJamException(e.getMessage());
        }
    }
}
