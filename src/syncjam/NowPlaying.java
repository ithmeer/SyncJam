package syncjam;

import java.awt.image.BufferedImage;

public class NowPlaying
{
    private static Song np_Song;
    public static boolean isPlaying = false;
    public static double songPosition;

    public static void setSong(Song song)       { np_Song = song; }
    public static Song getSong()                { return np_Song; }
    public static BufferedImage getAlbumArt()   { return np_Song.getAlbumArt(); }

    public static String getSongName()          { return np_Song.getSongName(); }
    public static String getArtistName()        { return np_Song.getArtistName(); }
    public static String getAlbumName()         { return np_Song.getAlbumName(); }

    public static int getSongLength()           { return np_Song.getSongLength(); }
    public static String getSongLengthString()  { return np_Song.getSongLengthString(); }

    public static void playToggle()
    {
        if(!isPlaying)
        {
            /*
            if(AudioController.song_to_play == null)
                AudioController.playSong(np_Song);
            else
                AudioController.play(true);
            */
            isPlaying = true;
        }
        else
        {
            //AudioController.play(false);
            isPlaying = false;
        }
    }


    public static BufferedImage getScaledAlbumArt(int w, int h)
    {
        return np_Song.getScaledAlbumArt(w, h);
    }
}
