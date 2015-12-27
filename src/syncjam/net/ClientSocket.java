package syncjam.net;

import syncjam.SongUtilities;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.SocketChannel;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;

/**
 * Send and receive client messages.
 * Created by Ithmeer on 3/22/2015.
 */
public class ClientSocket
{
    private final InputStream _inputStream;
    private final OutputStream _outputStream;

    public ClientSocket(Executor exec, InputStream inStream, OutputStream outStream,
                        SongUtilities songUtils) throws IOException
    {
        _inputStream = inStream;
        _outputStream = outStream;
        exec.execute(new SocketConsumer(_inputStream, songUtils));
        exec.execute(new SocketProducer(_outputStream, songUtils));
    }
}
