package syncjam.interfaces;

import java.awt.image.BufferedImage;

/**
 * Manage the currently playing song.
 * Created by Ithmeer on 9/11/2016.
 */
public interface PlayController
{
    BufferedImage getAlbumArt();

    String getAlbumName();

    String getArtistName();

    Song getSong();

    int getSongLength();

    String getSongLengthString();

    String getSongName();

    int getSongPosition();

    BufferedImage getScaledAlbumArt(int w, int h);

    boolean isPlaying();

    void playToggle(boolean state);

    void playToggle();

    void setSong(Song song);

    /**
     * Set the current position in the song and provide length if needed.
     * @param pos new song position
     */
    void setSongPosition(int pos);

    void setVolume(int value);

    void updateSong();
}
