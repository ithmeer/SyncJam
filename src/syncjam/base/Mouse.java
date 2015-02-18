package syncjam.base;

import java.awt.event.MouseListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;

public final class Mouse implements MouseListener, MouseMotionListener
{
    private static int[] pos = {0, 0};
    private static boolean[] active = new boolean[3];
    private static boolean[] rawInput = new boolean[3];
    private static double scale = 1;

    public static boolean pressed(int b)
    {
        return active[b];
    }

    public static void release(int b)
    {
        active[b] = false;
    }

    public static void releaseAll()
    {
        active = new boolean[3];
    }

    public static int getX()
    {
        return (int) (pos[0] / scale);
    }

    public static int getY()
    {
        return (int) (pos[1] / scale);
    }

    public static void setScale(double s)
    {
        scale = s;
    }

    private static void updateMouseState(MouseEvent e)
    {
        pos[0] = e.getX();
        pos[1] = e.getY();

        if (e.getButton() == MouseEvent.BUTTON1)
            setButton(0, true);
        else
            setButton(0, false);
        if (e.getButton() == MouseEvent.BUTTON2)
            setButton(2, true);
        else
            setButton(2, false);
        if (e.getButton() == MouseEvent.BUTTON3)
            setButton(1, true);
        else
            setButton(1, false);
    }

    private static void setButton(int i, boolean set)
    {
        if ((!active[i] && !rawInput[i]) || set == false)
        {
            active[i] = set;
            rawInput[i] = set;
        }
    }

    public static boolean liesWithin(int x, int y, int w, int h)
    {
        if (getX() > x && getX() < x + w)
            if (getY() > y && getY() < y + h)
                return true;
        return false;
    }

    public void mouseMoved(MouseEvent e)
    {
        updateMouseState(e);
    }

    public void mouseDragged(MouseEvent e)
    {
        updateMouseState(e);
    }

    public void mouseClicked(MouseEvent e)
    {
        updateMouseState(e);
    }

    public void mousePressed(MouseEvent e)
    {
        updateMouseState(e);
    }

    public void mouseReleased(MouseEvent e)
    {
        updateMouseState(e);
    }

    public void mouseEntered(MouseEvent e) {}

    public void mouseExited(MouseEvent e) {}
}
