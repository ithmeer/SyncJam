package syncjam.net.server;

import syncjam.SongUtilities;
import syncjam.net.ConcurrentCommandQueue;
import syncjam.net.NetworkSocket;

import java.io.IOException;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.Executor;

/**
 * Send and receive messages on the server.
 * Created by Ithmeer on 1/5/2016.
 */
public class ServerSideSocket extends NetworkSocket
{
    private final ServerConsumer _consumer;
    private static volatile ServerProducer _producer;

    private final ServerDataSocketConsumer _dataConsumer;
    private final ServerDataSocketProducer _dataProducer;

    public ServerSideSocket(Executor exec, SongUtilities songUtils,
                            Iterable<ServerSideSocket> clients, List<Socket> sockets)
            throws IOException
    {
        super(exec, sockets);

        _consumer = new ServerConsumer(getInputStream(SocketType.Command), songUtils, clients);

        if (_producer == null)
            _producer = new ServerProducer(getOutputStream(SocketType.Command), songUtils, clients);

        _dataConsumer = new ServerDataSocketConsumer(getInputStream(SocketType.Data), songUtils, clients);
        _dataProducer = new ServerDataSocketProducer(getOutputStream(SocketType.Data), songUtils);
    }

    @Override
    public void start()
    {
        _exec.execute(_consumer);
        _exec.execute(_producer);
        _exec.execute(_dataConsumer);
        _exec.execute(_dataProducer);
    }
}
