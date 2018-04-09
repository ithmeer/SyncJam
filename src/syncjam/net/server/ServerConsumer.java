package syncjam.net.server;

import syncjam.SyncJamException;
import syncjam.interfaces.ServiceContainer;
import syncjam.net.CommandPacket;
import syncjam.net.NetworkSocket;
import syncjam.net.client.ClientConsumer;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;

/**
 * Listen on a socket for commands.
 * Created by Ithmeer on 7/6/2015.
 */
public class ServerConsumer extends ClientConsumer
{
    private final Iterable<ServerSideSocket> _clients;

    public ServerConsumer(InputStream inStream, ServiceContainer services,
                          Iterable<ServerSideSocket> clients)
    {
        super(inStream, services);
        _clients = clients;
    }

    @Override
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
                for (ServerSideSocket client : _clients)
                {
                    if (_inputStream != client.getInputStream(NetworkSocket.SocketType.Command))
                    {
                        client.sendCommand(packet);
                    }
                }
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
