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
    public ServerSideSocket(Executor exec, InputStream inStream, OutputStream outStream,
                            CommandQueue queue, Iterable<ServerSideSocket> clients) throws IOException
    {
        super(inStream, outStream);
        exec.execute(new ServerConsumer(_inputStream, queue, clients));
        exec.execute(new ServerProducer(_outputStream, queue, clients));
    }
}
