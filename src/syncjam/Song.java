package syncjam;

import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.*;
import java.text.Format;
import java.text.SimpleDateFormat;

import com.mpatric.mp3agic.ID3v1;
import com.mpatric.mp3agic.ID3v2;
import com.mpatric.mp3agic.Mp3File;

import javax.imageio.ImageIO;

public class Song
{
    // effectively final
    private volatile BufferedImage albumArt = null;
    private final String songName;
    private final String artistName;
    private final String albumName;
    private final byte[] songData;
    private final int songLength; //In seconds, can change if necessary

    // !!!! This is for you, cat !!!!

    public Song(String filePath)
    {
        this(new File(filePath));
    }

    /**
     * Read song from file and set song info.
     */
    public Song(File file)
    {
        String[] parts = file.getName().split("\\.");
        songData = new byte[(int) file.length()];
        try
        {
            new FileInputStream(file).read(songData);
        } catch (IOException e)
        {
            e.printStackTrace();
        }

        // not mp3, just set name
        if (!parts[parts.length - 1].equals("mp3"))
        {
            songName = parts[0];
            artistName = "";
            albumName = "";
            songLength = 0;
        } else
        {
            Mp3File mp3;
            ID3v1 tags = null;

            // open file, scanning for errors and fetching length
            try
            {
                mp3 = new Mp3File(file);
                songLength = (int) mp3.getLengthInSeconds();
            } catch (Exception e)
            {
                throw new RuntimeException("can't open file: " + file.getName());
            }

            if (mp3.hasId3v2Tag())
                tags = mp3.getId3v2Tag();
            else if (mp3.hasId3v1Tag())
                tags = mp3.getId3v1Tag();

            if (tags != null)
            {
                songName = tags.getTitle();
                albumName = tags.getAlbum();
                artistName = tags.getArtist();

                // read album art
                if (tags instanceof ID3v2)
                {
                    ID3v2 tags2 = (ID3v2) tags;
                    byte[] image = tags2.getAlbumImage();
                    if (image != null)
                    {
                        try
                        {
                            albumArt = ImageIO.read(new ByteArrayInputStream(image));
                        } catch (IOException e)
                        {
                            // if we can't read, oh well
                        }
                    }
                }
            } else
            {
                songName = parts[0];
                artistName = "";
                albumName = "";
            }
        }
    }

    public BufferedImage getAlbumArt() {return albumArt; }

    public String getSongName() {return songName; }

    public String getArtistName() {return artistName;}

    public String getAlbumName() {return albumName; }

    public byte[] getSongData()
    {
        return songData;
    }

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
}
