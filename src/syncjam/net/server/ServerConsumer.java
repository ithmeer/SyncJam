package syncjam.net.server;

import syncjam.interfaces.ServiceContainer;
import syncjam.net.NetworkSocket;
import syncjam.net.client.ClientConsumer;

import java.io.IOException;
import java.io.InputStream;

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
        byte[] commandBuffer = new byte[3];

        while (!terminated)
        {
            try
            {
                _inputStream.read(commandBuffer);
                String command = new String(commandBuffer);
                System.out.println("consumed command: " + command);
                _cmdQueue.executeCommand(command);

                for (ServerSideSocket client : _clients)
                {
                    if (_inputStream != client.getInputStream(NetworkSocket.SocketType.Command))
                    {
                        client.sendCommand(commandBuffer);
                    }
                }
            }
            catch (IOException e)
            {
                // TODO: log error
                break;
            }
        }
    }
}
