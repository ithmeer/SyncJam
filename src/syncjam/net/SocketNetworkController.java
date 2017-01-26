package syncjam.net;

import syncjam.ConnectionStatus;
import syncjam.SyncJamException;
import syncjam.interfaces.AudioController;
import syncjam.interfaces.NetworkController;
import syncjam.utilities.ServerInfo;
import syncjam.interfaces.ServiceContainer;
import syncjam.net.client.ClientSideSocket;
import syncjam.net.server.ServerSideSocket;

import java.io.IOException;
import java.net.*;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Handle server hosting or connection. Thread-safe.
 * Created by Ithmeer on 3/22/2015.
 */
public class SocketNetworkController implements NetworkController
{
    // string constants
    private final String ackMessage = "OK";
    private final String connectionErrorStr = "Could not connect to server {0}:{1}";
    private final String hostingErrorStr = "Could not start server on port {0}";

    // the service container
    private final ServiceContainer _services;

    // the audio controller
    private final AudioController _audioCon;

    // the executor for all tasks
    private final ExecutorService _exec = Executors.newCachedThreadPool();

    // true if this is a client
    private AtomicBoolean _isClient = new AtomicBoolean(true);

    // thread-safe list of clients
    private final Queue<ServerSideSocket> _clients = new ConcurrentLinkedQueue<ServerSideSocket>();

    private volatile InterruptableRunnable _gateKeeper;

    // the client-side socket for this client
    private volatile NetworkSocket _socket;

    protected AtomicReference<ConnectionStatus> _status;

    public SocketNetworkController(ServiceContainer services)
    {
        _services = services;
        _audioCon = services.getService(AudioController.class);
        _status = new AtomicReference<ConnectionStatus>(ConnectionStatus.Unconnected);
    }

    public ConnectionStatus getStatus()
    {
        return _status.get();
    }

    public void setStatus(ConnectionStatus st)
    {
        _status.set(st);
    }

    public void disconnect()
    {
        if (isClient())
        {
            _socket.stop();
        }
        else
        {
            _gateKeeper.terminate();

            // TODO: inform clients of shutdown
            for (ServerSideSocket clientSocket: _clients)
            {
                clientSocket.stop();
            }

            _clients.clear();
        }

        setStatus(ConnectionStatus.Unconnected);
    }

    public Queue<ServerSideSocket> getClients()
    {
        return _clients;
    }

    public boolean isClient()
    {
        return _isClient.get();
    }

    /**
     * Client-to-server connection path. Connect to the given server.
     * @throws SyncJamException
     */
    @Override
    public void connectToServer(ServerInfo serverInfo) throws SyncJamException
    {
        setStatus(ConnectionStatus.Intermediate);

        final InetAddress host;
        try
        {
            host = InetAddress.getByName(serverInfo.ipAddress);
        }
        catch (UnknownHostException ex)
        {
            // TODO: log error
            setStatus(ConnectionStatus.Disconnected);
            throw new SyncJamException(String.format(connectionErrorStr, serverInfo.ipAddress,
                                                     serverInfo.port));
        }

        try
        {
            _isClient.set(true);
            Socket commandSocket = new Socket(host, serverInfo.port);
            commandSocket.setKeepAlive(true);
            Socket dataSocket = new Socket(host, serverInfo.port);
            dataSocket.setKeepAlive(true);
            Socket streamSocket = new Socket(host, serverInfo.port);
            streamSocket.setKeepAlive(true);

            InetAddress info = commandSocket.getInetAddress();
            System.out.printf("Connected to %s (%s)%n",
                              info.getHostName(), info.getHostAddress());

            LinkedList<Socket> sockets = new LinkedList<Socket>(
                    Arrays.asList(commandSocket, dataSocket));
            ClientSideSocket cs = new ClientSideSocket(_exec, _services, sockets, streamSocket,
                                                       commandSocket.getRemoteSocketAddress());

            cs.sendCommand(serverInfo.password);

            String ack = cs.readNextCommand();

            if (ack.equals(ackMessage))
            {
                // TODO: log message
                System.out.println("password accepted");
                _socket = cs;
                cs.start();
                setStatus(ConnectionStatus.Connected);
            }
            else
            {
                // TODO: log error
                System.out.println("password rejected");
                setStatus(ConnectionStatus.Disconnected);
            }
        }
        catch (IOException e)
        {
            // TODO: log error
            setStatus(ConnectionStatus.Disconnected);
            throw new SyncJamException(String.format(connectionErrorStr, serverInfo.ipAddress,
                                                     serverInfo.port));
        }
    }

    /**
     * Server-to-clients connection path. Start the server socket and listen for connections.
     * @param serverInfo
     * @throws SyncJamException
     */
    @Override
    public void startServer(ServerInfo serverInfo) throws SyncJamException
    {
        setStatus(ConnectionStatus.Intermediate);
        final ServerSocket serv;

        try
        {
            _isClient.set(false);
            serv = new ServerSocket(serverInfo.port);
            System.out.println("Server started\n");
        }
        catch (IOException e)
        {
            // TODO: log error
            throw new SyncJamException(String.format(hostingErrorStr, serverInfo));
        }

        final Map<String, List<Socket>> socketMap = new HashMap<String, List<Socket>>();

        _gateKeeper = new InterruptableRunnable()
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
                            _exec.execute(new ServerRunner(serverInfo.password, currentSockets));
                        }
                    }
                    catch (IOException e)
                    {
                        // TODO: log error
                        e.printStackTrace();
                        break;
                    }
                }
            }
        };

        _exec.execute(_gateKeeper);
        setStatus(ConnectionStatus.Hosted);
    }

    /**
     * Handle a connection to the server by a client.
     */
    private class ServerRunner extends InterruptableRunnable
    {
        private final String _password;
        private final List<Socket> _socketList;

        public ServerRunner(String password, List<Socket> socketList)
        {
            _password = password;
            _socketList = socketList;
        }

        @Override
        public void run()
        {
            try
            {
                Socket commandSocket = _socketList.get(0);
                Socket dataSocket = _socketList.get(1);
                Socket streamSocket = _socketList.get(2);

                ServerSideSocket ss = new ServerSideSocket(_exec, _services, _clients,
                                                           _socketList, streamSocket,
                                                           commandSocket.getRemoteSocketAddress());

                // TODO: investigate
                String password = ss.readNextCommand();
                if (_password.isEmpty() || password.equals(_password))
                {
                    System.out.println("password accepted");
                    _audioCon.addClient(ss);
                    ss.sendCommand(ackMessage);
                    ss.start();
                    _clients.add(ss);
                }
                else
                {
                    System.out.println("password rejected");
                    ss.sendCommand("bad password");
                    commandSocket.close();
                    dataSocket.close();
                }
            }
            catch (SocketTimeoutException ex)
            {
                // TODO: log error
                System.out.println("Socket timed out: " + ex.getMessage());
            }
            catch (IOException ex)
            {
                // TODO: log error
                System.out.println("Cannot create socket: " + ex.getMessage());
            }
        }
    }
}
