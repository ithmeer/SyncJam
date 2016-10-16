package syncjam;

import syncjam.interfaces.Song;

import java.awt.image.BufferedImage;
import java.text.Format;
import java.text.SimpleDateFormat;

/**
 * Created by Ithmeer on 9/17/2016.
 */
public abstract class SongBase implements Song
{
    // effectively final
    protected volatile SongMetadata _metadata;

    /**
     * Protected empty constructor. Base classes that use this MUST set _metadata in their
     * constructor.
     */
    protected SongBase() { }

    protected SongBase(SongMetadata metadata)
    {
        _metadata = metadata;
    }

    @Override
    public SongMetadata getMetadata() { return _metadata; }

    @Override
    public BufferedImage getAlbumArt() {return _metadata.getAlbumArt(); }

    @Override
    public String getTitle() {return _metadata.getSongTitle(); }

    @Override
    public String getArtistName() {return _metadata.getArtistName();}

    @Override
    public String getAlbumName() {return _metadata.getAlbumName(); }

    @Override
    public int getLength() { return _metadata.getSongLength(); }

    @Override
    public String getLengthString()
    {
        String lengthStr = "";
        int length = getLength();

        // if longer or equal to an hour, include hour digit
        if (length > 3600)
            lengthStr += (int) Math.floor(length / 3600) + ":";

        // format [hours]:minutes:seconds
        Format timeFormat = new SimpleDateFormat("m:ss");
        lengthStr += timeFormat.format(length * 1000);

        return lengthStr;
    }

    @Override
    public BufferedImage getScaledAlbumArt(int width, int height)
    {
        return _metadata.getScaledAlbumArt(width, height);
    }

    @Override
    public BufferedImage getScaledAlbumArtFast(int width, int height)
    {
        return _metadata.getScaledAlbumArtFast(width, height);
    }

    @Override
    public void setLength(int lengthInSecs)
    {
        _metadata.setSongLength(lengthInSecs);
    }
}
