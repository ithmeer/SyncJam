package syncjam.net;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedByInterruptException;
import java.nio.channels.ReadableByteChannel;
import java.util.concurrent.BlockingQueue;

/**
 * Listen on a socket for commands.
 * Created by Ithmeer on 7/6/2015.
 */
public class SocketProducer extends InterruptableRunnable implements Runnable
{
    private final ReadableByteChannel socketInputChannel;
    private final BlockingQueue<String> commandQueue;

    public SocketProducer(ReadableByteChannel sockChan, BlockingQueue<String> queue)
    {
        socketInputChannel = sockChan;
        commandQueue = queue;
    }

    public void run()
    {
        ByteBuffer commandBuffer = ByteBuffer.allocate(2);
        while (!terminated)
        {
            try
            {
                socketInputChannel.read(commandBuffer);
                commandQueue.add(commandBuffer.toString());
            }
            catch (IOException e)
            {
                break;
            }
        }

    }
}
