package syncjam.utilities;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Ithmeer on 5/7/2017.
 */
public class NonClosingInputStream extends InputStream
{
    private final InputStream _stream;

    public NonClosingInputStream(InputStream stream)
    {
        _stream = stream;
    }

    @Override
    public int read() throws IOException
    {
        return _stream.read();
    }

    @Override
    public int read(byte[] b) throws IOException
    {
        return _stream.read(b);
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException
    {
        return _stream.read(b, off, len);
    }

    @Override
    public long skip(long n) throws IOException
    {
        return _stream.skip(n);
    }

    @Override
    public int available() throws IOException
    {
        return _stream.available();
    }

    @Override
    public void close() throws IOException
    {
    }

    @Override
    public synchronized void mark(int readlimit)
    {
        _stream.mark(readlimit);
    }

    @Override
    public synchronized void reset() throws IOException
    {
        _stream.reset();
    }

    @Override
    public boolean markSupported()
    {
        return _stream.markSupported();
    }
}
