package syncjam;

import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.AudioHeader;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.KeyNotFoundException;
import org.jaudiotagger.tag.Tag;
import org.jaudiotagger.tag.images.Artwork;
import syncjam.interfaces.Song;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.Format;
import java.text.SimpleDateFormat;

/**
 * A song to be played. The song data is stored as an array of bytes. Thread-safe.
 */
public class BytesSong extends SongBase
{
    private final byte[] _songData;

    /**
     * Read song from file and set song info.
     */
    public BytesSong(File file) throws SyncJamException
    {
        super();

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

    public byte[] getSongData() { return _songData; }

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
