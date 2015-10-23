package syncjam.net;

import java.io.IOException;
import java.nio.channels.SocketChannel;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;

/**
 * Send and receive client messages.
 * Created by Ithmeer on 3/22/2015.
 */
public class ClientSocket
{
    private final SocketChannel channel;

    public ClientSocket(Executor exec, SocketChannel sockChan, BlockingQueue<String> commandQueue) throws IOException
    {
        channel = sockChan;
        exec.execute(new SocketProducer(channel, commandQueue));
    }
}
