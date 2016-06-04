package syncjam.net;

/**
 * Created by Ithmeer on 7/6/2015.
 */
public abstract class InterruptableRunnable implements Runnable
{
    protected volatile boolean terminated = false;

    public void terminate()
    {
        terminated = true;
    }
}
