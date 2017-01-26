package syncjam.net.server;

import syncjam.interfaces.ServiceContainer;
import syncjam.net.NetworkSocket;

import java.io.IOException;
import java.net.Socket;
import java.net.SocketAddress;
import java.nio.channels.ByteChannel;
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

    public ServerSideSocket(Executor exec, ServiceContainer services,
                            Iterable<ServerSideSocket> clients, List<Socket> sockets,
                            Socket channel, SocketAddress ipAddress)
            throws IOException
    {
        super(exec, sockets, channel, ipAddress);

        _consumer = new ServerConsumer(getInputStream(SocketType.Command), services, clients);

        if (_producer == null)
            _producer = new ServerProducer(getOutputStream(SocketType.Command), services, clients);

        _dataConsumer = new ServerDataSocketConsumer(getInputStream(SocketType.Data), services,
                                                     clients);
        _dataProducer = new ServerDataSocketProducer(getOutputStream(SocketType.Data), services,
                                                     clients);
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
            // TODO: report the error? it doesn't really matter
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
