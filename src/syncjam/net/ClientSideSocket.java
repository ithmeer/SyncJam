package syncjam.net;

import syncjam.SongUtilities;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.SocketChannel;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;

/**
 * Send and receive messages on the client.
 * Created by Ithmeer on 3/22/2015.
 */
public class ClientSideSocket extends NetworkSocket
{
    private final SocketConsumer _consumer;
    private final SocketProducer _producer;

    public ClientSideSocket(Executor exec, InputStream inStream, OutputStream outStream,
                            CommandQueue queue) throws IOException
    {
        super(exec, inStream, outStream);

        _consumer = new SocketConsumer(_inputStream, queue);
        _producer = new SocketProducer(_outputStream, queue);
    }

    @Override
    public void start()
    {
        _exec.execute(_consumer);
        _exec.execute(_producer);
    }
}
