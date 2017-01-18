package syncjam.net.client;

import syncjam.SongUtilities;
import syncjam.net.NetworkSocket;

import java.io.IOException;
import java.net.Socket;
import java.net.SocketAddress;
import java.nio.channels.ByteChannel;
import java.nio.channels.Channel;
import java.util.LinkedList;
import java.util.concurrent.Executor;

/**
 * Send and receive messages on the client.
 * Created by Ithmeer on 3/22/2015.
 */
public class ClientSideSocket extends NetworkSocket
{
    private final ClientConsumer _consumer;
    private final ClientProducer _producer;

    private final ClientDataSocketConsumer _dataConsumer;
    private final ClientDataSocketProducer _dataProducer;

    public ClientSideSocket(Executor exec, SongUtilities songUtils, LinkedList<Socket> sockets,
                            ByteChannel channel, SocketAddress ipAddress)
            throws IOException
    {
        super(exec, sockets, channel, ipAddress);

        _consumer = new ClientConsumer(getInputStream(SocketType.Command), songUtils);
        _producer = new ClientProducer(getOutputStream(SocketType.Command), songUtils);

        _dataConsumer = new ClientDataSocketConsumer(getInputStream(SocketType.Data), songUtils, this);
        _dataProducer = new ClientDataSocketProducer(getOutputStream(SocketType.Data), songUtils);
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
