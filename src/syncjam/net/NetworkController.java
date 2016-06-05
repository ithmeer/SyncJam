package syncjam.net;

import syncjam.SyncJamException;

import java.io.IOException;
import java.net.*;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Handle server hosting or connection. Thread-safe.
 * Created by Ithmeer on 3/22/2015.
 */
public class NetworkController
{
    private final String ackMessage = "OK";
    private final String connectionErrorStr = "Could not connect to server {0}:{1}";
    private final String hostingErrorStr = "Could not start server on port {0}";
    private final AtomicBoolean _terminated = new AtomicBoolean(false);
    private final ExecutorService _exec = Executors.newCachedThreadPool();
    private final CommandQueue _queue;

    public NetworkController(CommandQueue queue)
    {
        _queue = queue;
    }

    public void connectToServer(String address, int port, String password) throws SyncJamException
    {
        final InetAddress host;
        try
        {
            host = InetAddress.getByName(address);
        }
        catch (UnknownHostException ex)
        {
            throw new SyncJamException(String.format(connectionErrorStr, address, port));
        }

        Socket serverSock;
        try
        {
            serverSock = new Socket(host, port);

            InetAddress info = serverSock.getInetAddress();
            System.out.printf("Connected to %s (%s)%n",
                              info.getHostName(), info.getHostAddress());

            ClientSideSocket cs = new ClientSideSocket(_exec, serverSock.getInputStream(),
                                                       serverSock.getOutputStream(), _queue);

            cs.sendCommand(password);

            String ack = cs.readNext();

            if (ack.equals(ackMessage))
            {
                System.out.println("password accepted");
                cs.start();
            }
            else
            {
                System.out.println("password rejected");
            }
        }
        catch (IOException e)
        {
            throw new SyncJamException(String.format(connectionErrorStr, address, port));
        }
    }

    /**
     * Start up the server and wait on new connections.
     */
    public void startServer(int port, String password) throws SyncJamException
    {
        _exec.execute(new ServerRunner(port, password));
    }

    private class ServerRunner implements Runnable
    {
        private final ServerSocket _serv;
        private final String _password;
        private final Queue<ServerSideSocket> _clients =
                new ConcurrentLinkedQueue<ServerSideSocket>();

        public ServerRunner(int port, String password) throws SyncJamException
        {
            _password = password;

            try
            {
                _serv = new ServerSocket(port);
                System.out.println("Server started\n");
            }
            catch (IOException e)
            {
                throw new SyncJamException(String.format(hostingErrorStr, port));
            }
        }

        @Override
        public void run()
        {
            while (!_terminated.get())
            {
                try
                {
                    final Socket clientSock = _serv.accept();
                    InetAddress info = clientSock.getInetAddress();
                    System.out.printf("Connection from %s (%s)%n",
                                      info.getHostName(), info.getHostAddress());

                    // start up the socket producer and consumer tasks
                    ServerSideSocket cs = new ServerSideSocket(_exec, clientSock.getInputStream(),
                                                               clientSock.getOutputStream(),
                                                               _queue, _clients);
                    String password = cs.readNext();
                    if (_password.isEmpty() || password.equals(_password))
                    {
                        System.out.println("password accepted");
                        cs.sendCommand(ackMessage);
                        cs.start();
                        _clients.add(cs);
                    }
                    else
                    {
                        System.out.println("password rejected");
                        cs.sendCommand("bad password");
                        clientSock.close();
                    }
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
    }
}
