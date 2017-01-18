package syncjam.net.client;

import syncjam.interfaces.CommandQueue;
import syncjam.interfaces.ServiceContainer;
import syncjam.net.SocketConsumer;

import java.io.IOException;
import java.io.InputStream;

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
        byte[] commandBuffer = new byte[3];

        while (!terminated)
        {
            try
            {
                _inputStream.read(commandBuffer);
                String command = new String(commandBuffer);
                System.out.println("consumed command: " + command);
                _cmdQueue.executeCommand(command);
            }
            catch (IOException e)
            {
                // TODO: log error
                break;
            }
        }
    }
}
