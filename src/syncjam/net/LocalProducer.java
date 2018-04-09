package syncjam.net;

import syncjam.interfaces.CommandQueue;
import syncjam.interfaces.ServiceContainer;
import syncjam.utilities.CommandType;

import java.util.Objects;

/**
 * A producer for local commands.
 */
public class LocalProducer extends InterruptableRunnable
{
    private final CommandQueue _cmdQueue;

    public LocalProducer(ServiceContainer services)
    {
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

                // TODO: change to method on cmdQueue
                if (packet.getType() == CommandType.Kill)
                {
                    break;
                }

                System.out.println("produced command: " + packet.toString());
                _cmdQueue.executeCommand(packet);
            }
            catch (InterruptedException e)
            {
                // TODO: log error
                break;
            }
        }
    }
}
