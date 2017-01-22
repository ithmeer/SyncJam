package syncjam.interfaces;

import syncjam.SongMetadata;

import java.awt.image.BufferedImage;

/**
 * Created by Ithmeer on 9/17/2016.
 */
public interface Song
{
    SongMetadata getMetadata();

    BufferedImage getAlbumArt();

    String getTitle();

    String getArtistName();

    String getAlbumName();

    int getLength();

    String getLengthString();

    BufferedImage getPrescaledAlbumArt(int size);

    BufferedImage getScaledAlbumArt(int width, int height);

    BufferedImage getScaledAlbumArtFast(int width, int height);

    void setLength(int lengthInSecs);
}