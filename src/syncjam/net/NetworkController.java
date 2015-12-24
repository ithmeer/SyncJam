package syncjam.net;

import syncjam.SongUtilities;

import java.io.IOException;
import java.io.InputStream;
import java.net.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Handle server hosting or connection.
 * Created by Ithmeer on 3/22/2015.
 */
public class NetworkController
{
    private final AtomicBoolean _terminated = new AtomicBoolean(false);
    private final ExecutorService _exec = Executors.newCachedThreadPool();
    private final SongUtilities _songUtilities;
    private final int _port;

    public NetworkController(int port, SongUtilities songUtils)
    {
        _port = port;
        _songUtilities = songUtils;
    }

    public void startServer() throws IOException
    {
        _exec.execute(new Runnable()
        {
            final ServerSocket serv = new ServerSocket(_port);

            @Override
            public void run()
            {
                while (!_terminated.get())
                {
                    try
                    {
                        final Socket clientSock = serv.accept();
                        InetAddress info = clientSock.getInetAddress();
                        System.out.printf("Connection from %s (%s)%n",
                                          info.getHostName(), info.getHostAddress());

                        // start up the socket producer and consumer tasks
                        ClientSocket cs = new ClientSocket(_exec, clientSock.getInputStream(),
                                                           clientSock.getOutputStream(),
                                                           _songUtilities);
                    }
                    catch (SocketTimeoutException to)
                    {
                        break;
                    }
                    catch (IOException e)
                    {
                        System.out.println("Cannot create socket: " + e.getMessage());
                        break;
                    }
                }
            }
        });
    }
}
