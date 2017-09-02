package syncjam.net;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by Ithmeer on 7/6/2015.
 */
public abstract class InterruptableRunnable implements Runnable
{
    protected AtomicBoolean _terminated = new AtomicBoolean(false);

    public boolean isTerminated()
    {
        return _terminated.get();
    }

    public void terminate()
    {
        _terminated.set(true);
    }
}
