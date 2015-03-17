package syncjam;

import java.awt.image.BufferedImage;
import java.util.concurrent.atomic.AtomicBoolean;

public class NowPlaying
{
    private static AudioController controller;
    private volatile static Song np_Song;
    private static AtomicBoolean isPlaying = new AtomicBoolean(false);
    public static double songPosition;

    public static void setSong(Song song)
    {
        np_Song = song;
    }
    public static Song getSong()                { return np_Song; }
    public static BufferedImage getAlbumArt()   { return np_Song.getAlbumArt(); }

    public static String getSongName()          { return np_Song.getSongName(); }
    public static String getArtistName()        { return np_Song.getArtistName(); }
    public static String getAlbumName()         { return np_Song.getAlbumName(); }

    public static int getSongLength()           { return np_Song.getSongLength(); }
    public static String getSongLengthString()  { return np_Song.getSongLengthString(); }

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

    public static void nextSong()
    {
        controller.next();
    }

    public static void prevSong()
    {
    }

    public static BufferedImage getScaledAlbumArt(int w, int h)
    {
        return np_Song.getScaledAlbumArt(w, h);
    }

    public static void setController(AudioController au)
    {
        controller = au;
    }

    public static void setVolume(int value)
    {
        controller.setVolume(value);
    }
}
