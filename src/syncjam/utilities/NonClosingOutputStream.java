package syncjam.utilities;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Created by Ithmeer on 5/7/2017.
 */
public class NonClosingOutputStream extends OutputStream
{
    private final OutputStream _stream;

    public NonClosingOutputStream(OutputStream stream)
    {
        _stream = stream;
    }

    @Override
    public void write(int b) throws IOException
    {
        _stream.write(b);
    }

    @Override
    public void write(byte[] b) throws IOException
    {
        _stream.write(b);
    }

    @Override
    public void write(byte[] b, int off, int len) throws IOException
    {
        _stream.write(b, off, len);
    }

    @Override
    public void flush() throws IOException
    {
        _stream.flush();
    }

    @Override
    public void close() throws IOException
    {
    }
}
