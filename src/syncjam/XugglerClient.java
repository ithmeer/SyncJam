package syncjam;

import com.xuggle.xuggler.IContainer;

import java.net.SocketAddress;
import java.nio.channels.ByteChannel;
import java.nio.channels.WritableByteChannel;

/**
 * Created by Ithmeer on 1/14/2017.
 */
public class XugglerClient
{
    public final IContainer container;
    public final WritableByteChannel channel;
    public final SocketAddress address;

    private volatile int _failures = 0;

    private final int MAX_RETRIES = 5;

    public XugglerClient(IContainer container, ByteChannel streamChannel, SocketAddress addr)
    {
        this.container = container;
        this.channel = streamChannel;
        this.address = addr;
    }

    public boolean isDead()
    {
        return _failures++ >= MAX_RETRIES;
    }
}
