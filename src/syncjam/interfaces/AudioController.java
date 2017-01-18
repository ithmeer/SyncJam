package syncjam.interfaces;

import syncjam.net.NetworkSocket;

/**
 * Handle playing audio.
 * Created by Ithmeer on 9/11/2016.
 */
public interface AudioController
{
    void addClient(NetworkSocket client);

    void play();

    void pause();

    void setVolume(int level);

    void updateSong();

    void start();
}
