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
public class Song implements Externalizable
{
    private volatile byte[] _songData;
    private volatile SongMetadata _metadata;

    /**
     * Read song from file and set song info.
     */
    public Song(File file) throws SyncJamException
    {
        String[] parts = file.getName().split("\\.");
        AudioFile song = null;
        _songData = new byte[(int) file.length()];

        try
        {
            new FileInputStream(file).read(_songData);
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


        String songTitle = parts[0];
        BufferedImage albumArt = null;
        String artistName = "";
        String albumName = "";
        int songLength = 0;

        if (song != null)
        {
            Tag metadata = song.getTag();
            songLength = TryGetSongLength(song);
            songTitle = TryReadTag(metadata, FieldKey.TITLE);
            artistName = TryReadTag(metadata, FieldKey.ARTIST);
            albumName = TryReadTag(metadata, FieldKey.ALBUM);
            albumArt = TryReadCover(metadata);
        }

        _metadata = new SongMetadata(artistName, albumName, songTitle, albumArt, songLength);
    }

    public Song(byte[] songData, SongMetadata metadata)
    {
        _songData = songData;
        _metadata = metadata;
    }

    public BufferedImage getAlbumArt() {return _metadata.getAlbumArt(); }

    public String getSongTitle() {return _metadata.getSongTitle(); }

    public String getArtistName() {return _metadata.getArtistName();}

    public String getAlbumName() {return _metadata.getAlbumName(); }

    public byte[] getSongData() { return _songData; }

    public int getSongLength() { return _metadata.getSongLength(); }

    public String getSongLengthString()
    {
        String lengthStr = "";
        int length = getSongLength();

        if (length > 3600)
            lengthStr += (int) Math.floor(length / 3600) + ":"; //if longer or equal to an hour, include hour digit

        Format timeFormat = new SimpleDateFormat("m:ss");
        lengthStr += timeFormat.format(length * 1000); //format minutes:seconds

        return lengthStr;
    }

    public BufferedImage getScaledAlbumArt(int width, int height)
    {
        return _metadata.getScaledAlbumArt(width, height);
    }

    public BufferedImage getScaledAlbumArtFast(int width, int height)
    {
        return _metadata.getScaledAlbumArtFast(width, height);
    }

    public void setSongLength(int lengthInSecs)
    {
        _metadata.setSongLength(lengthInSecs);
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

    @Override
    public void writeExternal(ObjectOutput out) throws IOException
    {
        out.writeInt(_songData.length);
        out.write(_songData);

        out.writeObject(_metadata);
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException
    {
        int dataSize = in.readInt();
        _songData = new byte[dataSize];

        in.readFully(_songData);
        _metadata = (SongMetadata) in.readObject();
    }
}
