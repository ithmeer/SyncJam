package syncjam.net;

import java.io.IOException;
import java.io.InputStream;

/**
 * Listen on a socket for commands.
 * Created by Ithmeer on 7/6/2015.
 */
public class ServerConsumer extends SocketConsumer
{
    private final Iterable<ServerSideSocket> _clients;

    public ServerConsumer(InputStream inStream, CommandQueue queue,
                          Iterable<ServerSideSocket> clients)
    {
        super(inStream, queue);
        _clients = clients;
    }

    public void run()
    {
        byte[] commandBuffer = new byte[3];
        while (!terminated)
        {
            try
            {
                _socketInputStream.read(commandBuffer);
                String command = new String(commandBuffer);
                System.out.println("consumed command: " + command);
                _queue.executeCommand(command);
                for (ServerSideSocket client : _clients)
                {
                    if (_socketInputStream != client.getInputStream())
                    {
                        client.sendCommand(commandBuffer);
                    }
                }
            }
            catch (IOException e)
            {
                break;
            }
        }
    }
}
