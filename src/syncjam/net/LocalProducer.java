package syncjam.net;

import syncjam.interfaces.CommandQueue;
import syncjam.interfaces.ServiceContainer;

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
                String command = _cmdQueue.take();

                // TODO: change to method on cmdQueue
                if (command.equals("DI"))
                {
                    break;
                }

                System.out.println("produced command: " + command);
                _cmdQueue.executeCommand(command);
            }
            catch (InterruptedException e)
            {
                // TODO: log error
                break;
            }
        }

        int x = 5;
    }
}
