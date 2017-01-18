package syncjam.net.client;

import syncjam.interfaces.CommandQueue;
import syncjam.interfaces.ServiceContainer;
import syncjam.net.SocketProducer;

import java.io.IOException;
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
        _cmdQueue.toggleEnabled(true);

        while (!terminated)
        {
            try
            {
                String command = _cmdQueue.take();
                System.out.println("produced command: " + command);
                _outputStream.write(command.getBytes());
            }
            catch (InterruptedException e)
            {
                // TODO: log error
                break;
            }
            catch (IOException e)
            {
                // TODO: log error
                break;
            }
        }

    }
}
