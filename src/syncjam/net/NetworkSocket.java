package syncjam.net;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by Ithmeer on 1/5/2016.
 */
public class NetworkSocket
{
    protected final InputStream _inputStream;
    protected final OutputStream _outputStream;

    public NetworkSocket(InputStream inStream, OutputStream outStream)
    {
        _inputStream = inStream;
        _outputStream = outStream;
    }

    public InputStream getInputStream()
    {
        return _inputStream;
    }

    public String readNext()
    {
        byte[] next = new byte[20];

        try
        {
            _inputStream.read(next);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        return new String(next);
    }

    public void sendCommand(String cmd)
    {
        try
        {
            _outputStream.write(cmd.getBytes());
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}
