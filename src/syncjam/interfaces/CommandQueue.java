package syncjam.interfaces;

/**
 * Created by Ithmeer on 9/11/2016.
 */
public interface CommandQueue
{
    void toggleEnabled(boolean state);

    void executeCommand(String cmdBuffer);

    void gotoSong(int song);

    void nextSong();

    void prevSong();

    void moveSong(int from, int to);

    void removeSong(int song);

    void playToggle(boolean state);

    void seek(int percent);

    String take() throws InterruptedException;
}
