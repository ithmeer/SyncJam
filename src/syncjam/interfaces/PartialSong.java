package syncjam.interfaces;

/**
 * Created by Ithmeer on 9/25/2016.
 */
public interface PartialSong extends Song
{
    boolean getComplete();

    void setComplete();

    int getProgress();

    void setProgress(int value);
}
