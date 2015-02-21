package syncjam;

import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.text.Format;
import java.text.SimpleDateFormat;

import com.mpatric.mp3agic.ID3v1;
import com.mpatric.mp3agic.ID3v2;
import com.mpatric.mp3agic.Mp3File;

import javax.imageio.ImageIO;

public class Song
{
    private BufferedImage albumArt = null;
    private String songName = "Untitled";
    private String artistName = "";
    private String albumName = "";
    private int songLength = 0; //In seconds, can change if necessary

    // !!!! This is for you, cat !!!!

    /**
     * Read song from file and set song info.
     * @param fileName path to file
     */
    public Song(String fileName)
    {
        String[] parts = fileName.split("\\.");

        // not mp3, just set name
        if (!parts[1].equals("mp3"))
            songName = parts[0];
        else
        {
            Mp3File mp3;
            ID3v1 tags = null;

            // open file, scanning for errors and fetching length
            try
            {
                mp3 = new Mp3File(fileName);
                songLength = (int) mp3.getLengthInSeconds();
            }
            catch (Exception e)
            {
                throw new RuntimeException("can't open file: " + fileName);
            }

            if (mp3.hasId3v2Tag())
                tags = mp3.getId3v2Tag();
            else if (mp3.hasId3v1Tag())
                tags = mp3.getId3v1Tag();

            if (tags != null)
                readTags(tags);
        }
    }

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
    public BufferedImage getScaledAlbumArt(int width, int height)
    {
        if(albumArt == null)
            return null;

        int imageWidth  = this.getAlbumArt().getWidth();
        int imageHeight = this.getAlbumArt().getHeight();

        double scaleX = (double)width/imageWidth;
        double scaleY = (double)height/imageHeight;
        AffineTransform scaleTransform = AffineTransform.getScaleInstance(scaleX, scaleY);
        AffineTransformOp bilinearScaleOp = new AffineTransformOp(scaleTransform, AffineTransformOp.TYPE_BICUBIC);

        return bilinearScaleOp.filter(
                this.getAlbumArt(),
                new BufferedImage(width, height, this.getAlbumArt().getType()));
    }

    // Read tag info and initialize fields
    private void readTags(ID3v1 tags)
    {
        songName = tags.getTitle();
        albumName = tags.getAlbum();
        artistName = tags.getArtist();

        // read album art
        if (tags instanceof ID3v2)
        {
            ID3v2 tags2 = (ID3v2) tags;
            try
            {
                albumArt = ImageIO.read(new ByteArrayInputStream(tags2.getAlbumImage()));
            } catch (IOException e)
            {
                // if we can't read, oh well
            }
        }
    }
}
