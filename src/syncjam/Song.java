package syncjam;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.*;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.concurrent.atomic.AtomicInteger;

import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.AudioHeader;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.KeyNotFoundException;
import org.jaudiotagger.tag.Tag;
import org.jaudiotagger.tag.images.Artwork;

/**
 * A song to be played. The song data is stored as an array of bytes. Thread-safe.
 */
public class Song
{
    private final BufferedImage albumArt;
    private final String songName;
    private final String artistName;
    private final String albumName;
    private final byte[] songData;
    private final AtomicInteger songLength = new AtomicInteger(0); //In seconds, can change if necessary

    /**
     * Read song from file and set song info.
     */
    public Song(File file) throws SyncJamException
    {
        String[] parts = file.getName().split("\\.");
        AudioFile song = null;
        songData = new byte[(int) file.length()];

        try
        {
            new FileInputStream(file).read(songData);
        }
        catch (IOException e)
        {
            throw new SyncJamException("Cannot read file: " + file.getName());
        }

        try
        {
            song = AudioFileIO.read(file);
        }
        catch (Exception e)
        {
            // can't read metadata, not fatal
        }

        if (song == null)
        {
            songName = parts[0];
            artistName = "";
            albumName = "";
            albumArt = null;
        }
        else
        {
            Tag metadata = song.getTag();
            songLength.set(TryGetSongLength(song));
            songName = TryReadTag(metadata, FieldKey.TITLE);
            artistName = TryReadTag(metadata, FieldKey.ARTIST);
            albumName = TryReadTag(metadata, FieldKey.ALBUM);
            albumArt = TryReadCover(metadata);
        }
    }

    public BufferedImage getAlbumArt() {return albumArt; }

    public String getSongName() {return songName; }

    public String getArtistName() {return artistName;}

    public String getAlbumName() {return albumName; }

    public byte[] getSongData() { return songData; }

    public int getSongLength() {return songLength.get();}

    public String getSongLengthString()
    {
        String lengthStr = "";
        int length = songLength.get();

        if (length > 3600)
            lengthStr += (int) Math.floor(length / 3600) + ":"; //if longer or equal to an hour, include hour digit

        Format timeFormat = new SimpleDateFormat("m:ss");
        lengthStr += timeFormat.format(length * 1000); //format minutes:seconds

        return lengthStr;
    }

    //snipped this from the internet cause it was a lot better than the stuff i was doing hahahahahaha
    public BufferedImage getScaledAlbumArt(int width, int height)
    {
        if(albumArt == null)
            return null;

        BufferedImage albumArt = getAlbumArt();
        Image tempImg = albumArt.getScaledInstance(width, height, Image.SCALE_SMOOTH);
        BufferedImage resizedImg = new BufferedImage(width, height, albumArt.getType());
        Graphics2D g = resizedImg.createGraphics();
        g.drawImage(tempImg, 0, 0, null);
        g.dispose();

        return resizedImg;
    }

    public BufferedImage getScaledAlbumArtFast(int width, int height)
    {
        if(albumArt == null)
            return null;

        BufferedImage albumArt = getAlbumArt();
        int imageWidth  = albumArt.getWidth();
        int imageHeight = albumArt.getHeight();

        double scaleX = (double)width/imageWidth;
        double scaleY = (double)height/imageHeight;
        AffineTransform scaleTransform = AffineTransform.getScaleInstance(scaleX, scaleY);
        AffineTransformOp bilinearScaleOp = new AffineTransformOp(scaleTransform,
                                                                  AffineTransformOp.TYPE_BICUBIC);

        return bilinearScaleOp.filter(albumArt,
                                      new BufferedImage(width, height, albumArt.getType()));
    }

    public void setSongLength(int lengthInSecs)
    {
        songLength.set(lengthInSecs);
    }

    private BufferedImage TryReadCover(Tag metadata)
    {
        BufferedImage cover = null;

        if (metadata != null)
        {
            Artwork coverArt = metadata.getFirstArtwork();
            if (coverArt != null)
            {
                try
                {
                    cover = (BufferedImage) coverArt.getImage();
                }
                catch (IOException e)
                {
                    // if we can't read, oh well :(
                }
            }
        }

        return cover;
    }

    private String TryReadTag(Tag metadata, FieldKey tag)
    {
        String value = "";

        if (metadata != null)
        {
            try
            {
                value = metadata.getFirst(tag);
            }
            catch (KeyNotFoundException e)
            {
                // use default
            }
        }

        return value;
    }

    private int TryGetSongLength(AudioFile song)
    {
        int length = 0;

        AudioHeader hdr = song.getAudioHeader();
        if (hdr != null)
        {
            length = hdr.getTrackLength();
        }

        return length;
    }
}
