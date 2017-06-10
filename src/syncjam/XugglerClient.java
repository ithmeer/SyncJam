package syncjam;

import com.xuggle.xuggler.IContainer;

import java.io.BufferedOutputStream;
import java.io.OutputStream;
import java.net.SocketAddress;
import java.nio.channels.ByteChannel;
import java.nio.channels.WritableByteChannel;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Created by Ithmeer on 1/14/2017.
 */
public class XugglerClient
{
    public final OutputStream channel;
    public final SocketAddress address;

    private final AtomicReference<IContainer> _container = new AtomicReference<>();
    private final AtomicInteger _failures = new AtomicInteger();

    private final int MAX_RETRIES = 5;

    public XugglerClient(IContainer container, OutputStream streamChannel, SocketAddress addr)
    {
        _container.set(container);
        this.channel = streamChannel;
        this.address = addr;
    }

    public IContainer getContainer()
    {
        return _container.get();
    }

    public void setContainer(IContainer container)
    {
        _container.set(container);
    }

    public boolean isDead()
    {
        return _failures.incrementAndGet() >= MAX_RETRIES;
    }
}
