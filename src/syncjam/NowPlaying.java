package syncjam;

import java.awt.image.BufferedImage;
import java.io.IOException;

public class NowPlaying
{
    private static Song np_Song = null;
    public static boolean isPlaying = false;

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


    public static BufferedImage getScaledAlbumArt(int w, int h)
    {
        try {
            return np_Song.getScaledAlbumArt(w, h);
        }
        catch (IOException e)
        {
            e.printStackTrace();
            return null;
        }
    }
}
