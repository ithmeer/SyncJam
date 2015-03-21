package syncjam;

import java.awt.image.BufferedImage;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class NowPlaying
{
    private static AudioController controller;
    private volatile static Song np_Song;
    private static final AtomicBoolean isPlaying = new AtomicBoolean(false);
    private static final AtomicInteger songPosition = new AtomicInteger(0);

    private volatile static int songLength = 0;

    public static BufferedImage getAlbumArt()   { return np_Song.getAlbumArt(); }

    public static String getAlbumName()         { return np_Song.getAlbumName(); }

    public static String getArtistName()        { return np_Song.getArtistName(); }

    public static Song getSong()                { return np_Song; }

    public static int getSongLength()
    {
        int length = np_Song.getSongLength();
        return (length == 0) ? songLength : length;
    }

    public static String getSongLengthString()  { return np_Song.getSongLengthString(); }

    public static String getSongName()          { return np_Song.getSongName(); }

    public static int getSongPosition()             { return songPosition.get(); }

    public static BufferedImage getScaledAlbumArt(int w, int h)
    {
        return np_Song.getScaledAlbumArt(w, h);
    }

    public static boolean isPlaying()
    {
        return isPlaying.get();
    }

    public static void playToggle()
    {
        if(!isPlaying.get())
        {
            controller.play();
            isPlaying.set(true);
        }
        else
        {
            controller.pause();
            isPlaying.set(false);
        }
    }

    public static void setController(AudioController au)
    {
        controller = au;
    }

    public static void setSong(Song song)
    {
        np_Song = song;
    }

    public static void setSongDuration(int dur)
    {
        songLength = dur;
    }

    /**
     * Set the current position in the song and provide length if needed.
     * @param pos
     */
    public static void setSongPosition(int pos)
    {
        songPosition.set(pos);
    }

    public static void setVolume(int value)
    {
        controller.setVolume(value);
    }

    public static void updateSong()
    {
        isPlaying.set(true);
        controller.updateSong();
    }
}
