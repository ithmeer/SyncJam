package syncjam;

import syncjam.ui.UIServices;

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
public class SongMetadata implements Externalizable {
    private volatile BufferedImage _albumArt;
    private volatile BufferedImage[] _scaledAlbumArt = new BufferedImage[2];
    private volatile String _songTitle;
    private volatile String _artistName;
    private volatile String _albumName;

    // song length in seconds
    private final AtomicInteger _songLength = new AtomicInteger(0);

    public SongMetadata() {
        // empty constructor
    }

    public SongMetadata(String artist, String album, String title, BufferedImage art, int length) {
        _artistName = artist;
        _albumArt = art;
        _albumName = album;
        _songTitle = title;
        _songLength.set(length);

        _scaledAlbumArt[0] = getScaledAlbumArt(120, 120);
        _scaledAlbumArt[1] = getScaledAlbumArt(49, 49);
    }

    public BufferedImage getAlbumArt() {
        return _albumArt;
    }

    public String getSongTitle() {
        return _songTitle;
    }

    public String getArtistName() {
        return _artistName;
    }

    public String getAlbumName() {
        return _albumName;
    }

    public int getSongLength() {
        return _songLength.get();
    }

    public BufferedImage getPrescaledAlbumArt(int num) {
        return _scaledAlbumArt[num];
    }

    public BufferedImage getScaledAlbumArt(int width, int height) {
        if (_albumArt != null)
            return scaleImage3(_albumArt, width, height, RenderingHints.VALUE_INTERPOLATION_BILINEAR, true);
        return null;
    }

    public BufferedImage getScaledAlbumArtFast(int width, int height) {
        BufferedImage albumArt = getAlbumArt();
        if (albumArt == null)
            return null;

        int imageWidth = albumArt.getWidth();
        int imageHeight = albumArt.getHeight();

        double scaleX = (double) width / imageWidth;
        double scaleY = (double) height / imageHeight;
        AffineTransform scaleTransform = AffineTransform.getScaleInstance(scaleX, scaleY);
        AffineTransformOp bilinearScaleOp = new AffineTransformOp(scaleTransform,
                AffineTransformOp.TYPE_BICUBIC);

        return bilinearScaleOp.filter(albumArt,
                new BufferedImage(width, height, albumArt.getType()));
    }

    //http://scaleimagesjava.blogspot.com/2011/09/scale-images-in-java.html
    public BufferedImage scaleImage3(BufferedImage img,
                                            int targetWidth,
                                            int targetHeight,
                                            Object hint,
                                            boolean higherQuality)
    {
        int type = (img.getTransparency() == Transparency.OPAQUE) ?
                BufferedImage.TYPE_INT_RGB : BufferedImage.TYPE_INT_ARGB;
        BufferedImage ret = img;
        int w, h;
        if (higherQuality) {
            // Use multi-step technique: start with original size, then
            // scale down in multiple passes with drawImage()
            // until the target size is reached
            w = img.getWidth();
            if (w < targetWidth) {
                w = targetWidth;
            }
            h = img.getHeight();
            if (h < targetHeight) {
                h = targetHeight;
            }
        } else {
            // Use one-step technique: scale directly from original
            // size to target size with a single drawImage() call
            w = targetWidth;
            h = targetHeight;
        }
        do {
            if (higherQuality && w > targetWidth) {
                w >>= 1;
                if (w < targetWidth) {
                    w = targetWidth;
                }
            }
            if (higherQuality && h > targetHeight) {
                h >>= 1;
                if (h < targetHeight) {
                    h = targetHeight;
                }
            }
            BufferedImage tmp = new BufferedImage(w, h, type);
            Graphics2D g2 = tmp.createGraphics();
            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, hint);
            g2.drawImage(ret, 0, 0, w, h, null);

            ret = tmp;
            g2.dispose();
        } while (w != targetWidth || h != targetHeight);

        return ret;
    }

    public void setSongLength(int lengthInSecs) {
        _songLength.set(lengthInSecs);
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
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
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
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
