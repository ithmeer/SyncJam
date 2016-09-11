package syncjam.net.server;

import syncjam.SongUtilities;
import syncjam.interfaces.CommandQueue;
import syncjam.net.ConcurrentCommandQueue;
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

    public ServerConsumer(InputStream inStream, SongUtilities songUtils,
                          Iterable<ServerSideSocket> clients)
    {
        super(inStream, songUtils);
        _clients = clients;
    }

    @Override
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

                for (ServerSideSocket client : _clients)
                {
                    if (_inputStream != client.getInputStream(0))
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
