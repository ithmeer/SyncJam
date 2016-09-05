package syncjam.net;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.Executor;

/**
 * Send and receive messages on the server.
 * Created by Ithmeer on 1/5/2016.
 */
public class ServerSideSocket extends NetworkSocket
{
    private final ServerConsumer _consumer;
    private static volatile ServerProducer _producer;

    public ServerSideSocket(Executor exec, InputStream inStream, OutputStream outStream,
                            CommandQueue queue, Iterable<ServerSideSocket> clients) throws IOException
    {
        super(exec, inStream, outStream);
        _consumer = new ServerConsumer(_inputStream, queue, clients);

        if (_producer == null)
            _producer = new ServerProducer(_outputStream, queue, clients);
    }

    @Override
    public void start()
    {
        _exec.execute(_consumer);
        _exec.execute(_producer);
    }
}
