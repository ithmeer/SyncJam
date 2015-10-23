package syncjam.net;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.*;
import java.util.Scanner;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Handle server hosting or connection.
 * Created by Ithmeer on 3/22/2015.
 */
public class NetworkController
{
    private final AtomicBoolean terminated = new AtomicBoolean(false);
    private final ExecutorService exec = Executors.newCachedThreadPool();
    private final BlockingQueue<String> commandQueue;
    private final int port;

    public NetworkController(int port, BlockingQueue<String> queue)
    {
        this.port = port;
        this.commandQueue = queue;
    }

    public void startServer() throws IOException
    {
        exec.execute(new Runnable()
        {
            final ServerSocket serv = new ServerSocket(port);

            @Override
            public void run()
            {
                while (!terminated.get())
                {
                    try
                    {
                        final Socket clientSock = serv.accept();
                        InetAddress info = clientSock.getInetAddress();
                        System.out.printf("Connection from %s (%s)%n",
                                          info.getHostName(), info.getHostAddress());

                        // start up the socket producer task
                        exec.execute(new SocketProducer(clientSock.getChannel(), commandQueue));
                    }
                    catch (SocketTimeoutException to)
                    {
                        break;
                    }
                    catch (IOException e)
                    {
                        System.out.println("Cannot create socket: "
                                                   + e.getMessage());
                        break;
                    }
                }
            }
        });
    }
}
