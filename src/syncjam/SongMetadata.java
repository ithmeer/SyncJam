package syncjam;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Thread-safe metadata container class. All fields but _songLength are effectively final.
 * Created by Ithmeer on 6/12/2016.
 */
public class SongMetadata implements Externalizable
{
    private volatile BufferedImage _albumArt;
    private volatile String _songTitle;
    private volatile String _artistName;
    private volatile String _albumName;

    // song length in seconds
    private final AtomicInteger _songLength = new AtomicInteger(0);

    public SongMetadata()
    {
        // empty constructor
    }

    public SongMetadata(String artist, String album, String title, BufferedImage art, int length)
    {
        _artistName = artist;
        _albumArt = art;
        _albumName = album;
        _songTitle = title;
        _songLength.set(length);
    }

    public BufferedImage getAlbumArt() { return _albumArt; }

    public String getSongTitle() { return _songTitle; }

    public String getArtistName() { return _artistName;}

    public String getAlbumName() { return _albumName; }

    public int getSongLength() { return _songLength.get();}

    public BufferedImage getScaledAlbumArt(int width, int height)
    {
        BufferedImage albumArt = getAlbumArt();

        if(albumArt == null)
            return null;

        Image tempImg = albumArt.getScaledInstance(width, height, Image.SCALE_SMOOTH);
        BufferedImage resizedImg = new BufferedImage(width, height, albumArt.getType());
        Graphics2D g = resizedImg.createGraphics();
        g.drawImage(tempImg, 0, 0, null);
        g.dispose();

        return resizedImg;
    }

    public BufferedImage getScaledAlbumArtFast(int width, int height)
    {
        BufferedImage albumArt = getAlbumArt();
        if(albumArt == null)
            return null;

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
        _songLength.set(lengthInSecs);
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException
    {
        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        ImageIO.write(_albumArt, "bmp", byteStream);
        byte[] outBytes = byteStream.toByteArray();

        out.writeInt(outBytes.length);
        out.write(outBytes);

        out.writeObject(_artistName);
        out.writeObject(_albumName);
        out.writeObject(_songTitle);
        out.writeInt(getSongLength());
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException
    {
        int bytesSize = in.readInt();
        byte[] inBytes = new byte[bytesSize];
        in.readFully(inBytes);

        _albumArt = ImageIO.read(new ByteArrayInputStream(inBytes));
        _artistName = (String) in.readObject();
        _albumName = (String) in.readObject();
        _songTitle = (String) in.readObject();
        _songLength.set(in.readInt());
    }
}
