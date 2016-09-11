package syncjam.net;

import syncjam.SongUtilities;
import syncjam.SyncJamException;
import syncjam.interfaces.NetworkController;
import syncjam.net.client.ClientSideSocket;
import syncjam.net.server.ServerSideSocket;

import java.io.IOException;
import java.net.*;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Handle server hosting or connection. Thread-safe.
 * Created by Ithmeer on 3/22/2015.
 */
public class SocketNetworkController implements NetworkController
{
    private final String ackMessage = "OK";
    private final String connectionErrorStr = "Could not connect to server {0}:{1}";
    private final String hostingErrorStr = "Could not start server on port {0}";
    private final ExecutorService _exec = Executors.newCachedThreadPool();
    private final SongUtilities _songUtils;

    public SocketNetworkController(SongUtilities songUtils)
    {
        _songUtils = songUtils;
    }

    /**
     * Client-to-server connection path. Connect to the given server.
     * @param address
     * @param port
     * @param password
     * @throws SyncJamException
     */
    @Override
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

        try
        {
            Socket commandSocket = new Socket(host, port);
            commandSocket.setKeepAlive(true);
            Socket dataSocket = new Socket(host, port);
            dataSocket.setKeepAlive(true);
            Socket streamSocket = new Socket(host, port);
            streamSocket.setKeepAlive(true);

            InetAddress info = commandSocket.getInetAddress();
            System.out.printf("Connected to %s (%s)%n",
                              info.getHostName(), info.getHostAddress());

            LinkedList<Socket> sockets = new LinkedList<Socket>(
                    Arrays.asList(commandSocket, dataSocket, streamSocket));
            ClientSideSocket cs = new ClientSideSocket(_exec, _songUtils, sockets);

            cs.sendCommand(password);

            String ack = cs.readNextCommand();

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
     * Server-to-clients connection path. Start the server socket and listen for connections.
     * @param port
     * @param password
     * @throws SyncJamException
     */
    @Override
    public void startServer(int port, final String password) throws SyncJamException
    {
        final ServerSocket serv;

        try
        {
            serv = new ServerSocket(port);
            System.out.println("Server started\n");
        }
        catch (IOException e)
        {
            throw new SyncJamException(String.format(hostingErrorStr, port));
        }

        final Map<String, List<Socket>> socketMap = new HashMap<String, List<Socket>>();

        _exec.execute(new InterruptableRunnable()
        {
            @Override
            public void run()
            {
                while (!terminated)
                {
                    try
                    {
                        Socket clientSock = serv.accept();
                        InetAddress info = clientSock.getInetAddress();
                        System.out.printf("Connection from %s (%s)%n",
                                          info.getHostName(), info.getHostAddress());

                        List<Socket> currentSockets = socketMap.get(info.getHostAddress());
                        if (currentSockets == null)
                        {
                            currentSockets = new LinkedList<Socket>();
                            socketMap.put(info.getHostAddress(), currentSockets);
                        }

                        if (currentSockets.size() < 2)
                        {
                            currentSockets.add(clientSock);
                        }
                        else
                        {
                            currentSockets.add(clientSock);
                            socketMap.remove(info.getHostAddress());
                            _exec.execute(new ServerRunner(password, currentSockets));
                        }
                    }
                    catch (IOException e)
                    {
                        e.printStackTrace();
                        break;
                    }
                }
            }
        });
    }

    private class ServerRunner extends InterruptableRunnable
    {
        private final String _password;
        private final List<Socket> _socketList;
        private final Queue<ServerSideSocket> _clients =
                new ConcurrentLinkedQueue<ServerSideSocket>();

        public ServerRunner(String password, List<Socket> socketList)
        {
            _password = password;
            _socketList = socketList;
        }

        @Override
        public void run()
        {
            while (!terminated)
            {
                try
                {
                    Socket commandSocket = _socketList.get(0);
                    Socket dataSocket = _socketList.get(1);

                    // start up the socket producer and consumer tasks
                    ServerSideSocket cs = new ServerSideSocket(_exec, _songUtils, _clients,
                                                               _socketList);

                    String password = cs.readNextCommand();
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
                        commandSocket.close();
                        dataSocket.close();
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
