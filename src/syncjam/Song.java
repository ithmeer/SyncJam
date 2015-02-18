package syncjam;

import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.Format;
import java.text.SimpleDateFormat;

public class Song
{
    private BufferedImage albumArt = null;
    private String songName = "Untitled";
    private String artistName = "";
    private String albumName = "";
    private int songLength = 0; //In seconds, can change if necessary

    // !!!! This is for you, cat !!!!
    public Song(File f) {}

    public Song(String song, String artist, String album, int length)  //for testing
    {
        songName = song;
        artistName = artist;
        albumName = album;
        songLength = length;
    }

    public BufferedImage getAlbumArt() {return albumArt; }

    public String getSongName() {return songName; }

    public String getArtistName() {return artistName;}

    public String getAlbumName() {return albumName; }

    public int getSongLength() {return songLength;}

    public String getSongLengthString()
    {
        String length = "";

        if (songLength > 3600)
            length += (int) Math.floor(songLength / 3600) + ":"; //if longer or equal to an hour, include hour digit

        Format timeFormat = new SimpleDateFormat("m:ss");
        length += timeFormat.format(songLength * 1000); //format minutes:seconds

        return length;
    }

    //snipped this from the internet cause it was a lot better than the stuff i was doing hahahahahaha
    public BufferedImage getScaledAlbumArt(int width, int height) throws IOException
    {
        int imageWidth  = this.getAlbumArt().getWidth();
        int imageHeight = this.getAlbumArt().getHeight();

        double scaleX = (double)width/imageWidth;
        double scaleY = (double)height/imageHeight;
        AffineTransform scaleTransform = AffineTransform.getScaleInstance(scaleX, scaleY);
        AffineTransformOp bilinearScaleOp = new AffineTransformOp(scaleTransform, AffineTransformOp.TYPE_BILINEAR);

        return bilinearScaleOp.filter(
                this.getAlbumArt(),
                new BufferedImage(width, height, this.getAlbumArt().getType()));
    }
}
