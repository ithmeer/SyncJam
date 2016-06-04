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
    public ClientSideSocket(Executor exec, InputStream inStream, OutputStream outStream,
                            CommandQueue queue) throws IOException
    {
        super(inStream, outStream);
        exec.execute(new SocketConsumer(_inputStream, queue));
        exec.execute(new SocketProducer(_outputStream, queue));
    }
}
