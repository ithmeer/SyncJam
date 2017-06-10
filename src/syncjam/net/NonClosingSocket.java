package syncjam.net;

import syncjam.utilities.NonClosingInputStream;
import syncjam.utilities.NonClosingOutputStream;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketException;
import java.nio.channels.SocketChannel;

/**
 * Created by Ithmeer on 5/7/2017.
 */
public class NonClosingSocket extends Socket
{
    private boolean _canClose = false;
    private Socket _innerSocket;

    public NonClosingSocket(Socket sock)
    {
        _innerSocket = sock;
    }

    @Override
    public void connect(SocketAddress endpoint) throws IOException
    {
        _innerSocket.connect(endpoint);
    }

    @Override
    public void connect(SocketAddress endpoint, int timeout) throws IOException
    {
        _innerSocket.connect(endpoint, timeout);
    }

    @Override
    public void bind(SocketAddress bindpoint) throws IOException
    {
        _innerSocket.bind(bindpoint);
    }

    @Override
    public InetAddress getInetAddress()
    {
        return _innerSocket.getInetAddress();
    }

    @Override
    public InetAddress getLocalAddress()
    {
        return _innerSocket.getLocalAddress();
    }

    @Override
    public int getPort()
    {
        return _innerSocket.getPort();
    }

    @Override
    public int getLocalPort()
    {
        return _innerSocket.getLocalPort();
    }

    @Override
    public SocketAddress getRemoteSocketAddress()
    {
        return _innerSocket.getRemoteSocketAddress();
    }

    @Override
    public SocketAddress getLocalSocketAddress()
    {
        return _innerSocket.getLocalSocketAddress();
    }

    @Override
    public SocketChannel getChannel()
    {
        return _innerSocket.getChannel();
    }

    @Override
    public InputStream getInputStream() throws IOException
    {
        return new NonClosingInputStream(_innerSocket.getInputStream());
    }

    @Override
    public OutputStream getOutputStream() throws IOException
    {
        return new NonClosingOutputStream(_innerSocket.getOutputStream());
    }

    @Override
    public void setTcpNoDelay(boolean on) throws SocketException
    {
        _innerSocket.setTcpNoDelay(on);
    }

    @Override
    public boolean getTcpNoDelay() throws SocketException
    {
        return _innerSocket.getTcpNoDelay();
    }

    @Override
    public void setSoLinger(boolean on, int linger) throws SocketException
    {
        _innerSocket.setSoLinger(on, linger);
    }

    @Override
    public int getSoLinger() throws SocketException
    {
        return _innerSocket.getSoLinger();
    }

    @Override
    public void sendUrgentData(int data) throws IOException
    {
        _innerSocket.sendUrgentData(data);
    }

    @Override
    public void setOOBInline(boolean on) throws SocketException
    {
        _innerSocket.setOOBInline(on);
    }

    @Override
    public boolean getOOBInline() throws SocketException
    {
        return _innerSocket.getOOBInline();
    }

    @Override
    public synchronized void setSoTimeout(int timeout) throws SocketException
    {
        _innerSocket.setSoTimeout(timeout);
    }

    @Override
    public synchronized int getSoTimeout() throws SocketException
    {
        return _innerSocket.getSoTimeout();
    }

    @Override
    public synchronized void setSendBufferSize(int size) throws SocketException
    {
        _innerSocket.setSendBufferSize(size);
    }

    @Override
    public synchronized int getSendBufferSize() throws SocketException
    {
        return _innerSocket.getSendBufferSize();
    }

    @Override
    public synchronized void setReceiveBufferSize(int size) throws SocketException
    {
        _innerSocket.setReceiveBufferSize(size);
    }

    @Override
    public synchronized int getReceiveBufferSize() throws SocketException
    {
        return _innerSocket.getReceiveBufferSize();
    }

    @Override
    public void setKeepAlive(boolean on) throws SocketException
    {
        _innerSocket.setKeepAlive(on);
    }

    @Override
    public boolean getKeepAlive() throws SocketException
    {
        return _innerSocket.getKeepAlive();
    }

    @Override
    public void setTrafficClass(int tc) throws SocketException
    {
        _innerSocket.setTrafficClass(tc);
    }

    @Override
    public int getTrafficClass() throws SocketException
    {
        return _innerSocket.getTrafficClass();
    }

    @Override
    public void setReuseAddress(boolean on) throws SocketException
    {
        _innerSocket.setReuseAddress(on);
    }

    @Override
    public boolean getReuseAddress() throws SocketException
    {
        return _innerSocket.getReuseAddress();
    }

    @Override
    public synchronized void close() throws IOException
    {
        if (_canClose)
        {
            _innerSocket.close();
        }
    }

    @Override
    public void shutdownInput() throws IOException
    {
        _innerSocket.shutdownInput();
    }

    @Override
    public void shutdownOutput() throws IOException
    {
        _innerSocket.shutdownOutput();
    }

    @Override
    public String toString()
    {
        return _innerSocket.toString();
    }

    @Override
    public boolean isConnected()
    {
        return _innerSocket.isConnected();
    }

    @Override
    public boolean isBound()
    {
        return _innerSocket.isBound();
    }

    @Override
    public boolean isClosed()
    {
        return _innerSocket.isClosed();
    }

    @Override
    public boolean isInputShutdown()
    {
        return _innerSocket.isInputShutdown();
    }

    @Override
    public boolean isOutputShutdown()
    {
        return _innerSocket.isOutputShutdown();
    }

    @Override
    public void setPerformancePreferences(int connectionTime, int latency, int bandwidth)
    {
        _innerSocket.setPerformancePreferences(connectionTime, latency, bandwidth);
    }

    public void setCanClose(boolean canClose)
    {
        _canClose = canClose;
    }
}
