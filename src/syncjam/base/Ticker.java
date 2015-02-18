package syncjam.base;

public class Ticker implements Runnable
{
    private volatile boolean running = true;
    private int wait;
    private int ticks = 0;

    private Updatable window;

    public Ticker(Updatable u, int s)
    {
        window = u;
        wait = s;
    }

    @Override
    public void run()
    {
        while (running)
        {
            try
            {
                Thread.sleep(wait);
            } catch (Exception e) {}
            window.update();
        }
    }

    public void stop()
    {
        running = false;
    }

    public int getTicks()
    {
        return ticks;
    }
}
