package syncjam.net.client;

import syncjam.interfaces.ServiceContainer;
import syncjam.net.NetworkSocket;

import java.io.IOException;
import java.net.Socket;
import java.net.SocketAddress;
import java.nio.channels.ByteChannel;
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

    public ClientSideSocket(Executor exec, ServiceContainer services, LinkedList<Socket> sockets,
                            ByteChannel channel, SocketAddress ipAddress)
            throws IOException
    {
        super(exec, sockets, channel, ipAddress);

        _consumer = new ClientConsumer(getInputStream(SocketType.Command), services);
        _producer = new ClientProducer(getOutputStream(SocketType.Command), services);

        _dataConsumer = new ClientDataSocketConsumer(getInputStream(SocketType.Data), services,
                                                     this);
        _dataProducer = new ClientDataSocketProducer(getOutputStream(SocketType.Data), services);
    }

    @Override
    public void stop()
    {
        super.stop();

        _consumer.terminate();
        _producer.terminate();
        _dataConsumer.terminate();
        _dataProducer.terminate();

        try
        {
            _streamChannel.close();
        }
        catch (IOException e)
        {
            // TODO: log the error, it doesn't need to be reported
            e.printStackTrace();
        }
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
