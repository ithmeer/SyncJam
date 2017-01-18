package syncjam;

import com.xuggle.xuggler.IContainer;

import java.net.SocketAddress;
import java.nio.channels.ByteChannel;

/**
 * Created by Ithmeer on 1/14/2017.
 */
public final class XugglerClient
{
    public final IContainer container;
    public final ByteChannel channel;
    public final SocketAddress address;

    public XugglerClient(IContainer container, ByteChannel streamChannel, SocketAddress addr)
    {
        this.container = container;
        this.channel = streamChannel;
        this.address = addr;
    }
}
