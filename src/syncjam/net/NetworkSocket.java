package syncjam.net;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.Executor;

/**
 * Created by Ithmeer on 1/5/2016.
 */
public abstract class NetworkSocket
{
    protected final Executor _exec;
    protected final InputStream _inputStream;
    protected final OutputStream _outputStream;

    public NetworkSocket(Executor exec, InputStream inStream, OutputStream outStream)
    {
        _inputStream = inStream;
        _outputStream = outStream;
        _exec = exec;
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

        return new String(next).trim();
    }

    public void sendCommand(String cmd)
    {
        sendCommand(cmd.getBytes());
    }

    public void sendCommand(byte[] cmd)
    {
        try
        {
            _outputStream.write(cmd);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public abstract void start();
}
