package syncjam.net.client;

import syncjam.SongUtilities;
import syncjam.net.NetworkSocket;

import java.io.IOException;
import java.net.Socket;
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

    private final DataSocketConsumer _dataConsumer;
    private final DataSocketProducer _dataProducer;

    public ClientSideSocket(Executor exec, SongUtilities songUtils, LinkedList<Socket> sockets)
            throws IOException
    {
        super(exec, sockets);

        _consumer = new ClientConsumer(getInputStream(0), songUtils);
        _producer = new ClientProducer(getOutputStream(0), songUtils);

        _dataConsumer = new DataSocketConsumer(getInputStream(1), songUtils);
        _dataProducer = new DataSocketProducer(getOutputStream(1), songUtils);
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
