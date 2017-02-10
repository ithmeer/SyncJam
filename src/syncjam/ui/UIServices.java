package syncjam.ui;

import syncjam.SyncJam;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * Created by Marty on 1/21/2017.
 */
public class UIServices
{
    private static JFrame _mainWindow;
    private static SyncJamUI _syncJamUI;

    public static JFrame getMainWindow() {
        return _mainWindow;
    }
    public static void setMainWindow(JFrame mainWindow) {
        UIServices._mainWindow = mainWindow;
    }

    public static SyncJamUI getSyncJamUI() {
        return _syncJamUI;
    }
    public static void setSyncJamUI(SyncJamUI syncJamUI) {
        UIServices._syncJamUI = syncJamUI;
    }

    public static Image loadImage(String res) {
        Image out;
        try {
            out = ImageIO.read(SyncJam.class.getResource("/syncjam/resources/"+res));
        } catch (IOException e) {
            System.out.println("File " + res + " not found.");
            out = null;
        }
        return out;
    }
    public static BufferedImage loadBufferedImage(String res) {
        Image i = loadImage(res);
        BufferedImage out;
        if(i != null) {
            out = new BufferedImage(i.getWidth(null), i.getHeight(null), BufferedImage.TYPE_INT_ARGB);
            Graphics2D temp = out.createGraphics();
            temp.drawImage(i, 0, 0, null);
            temp.dispose();
        }
        else out = null;

        return out;
    }
    public static Icon loadIcon(String res, Colors color) {
        BufferedImage i = loadBufferedImage(res);
        return new Icon() {
            @Override
            public void paintIcon(Component c, Graphics g, int x, int y) {
                g.drawImage(colorImage(i, color), 0, 0, null);
            }
            @Override public int getIconWidth() {
                return i.getWidth(null);
            }
            @Override public int getIconHeight() {
                return i.getHeight(null);
            }
        };
    }
    public static BufferedImage colorImage(BufferedImage img, Colors color) {
        return colorImage(img, Colors.get(color));
    }
    public static BufferedImage colorImage(BufferedImage img, Color t) {
        int w = img.getWidth();
        int h = img.getHeight();
        for(int x = 0; x < w; x++)
        {
            for(int y = 0; y < h; y++)
            {
                Color orig = new Color(img.getRGB(x, y), true);
                Color c = new Color(t.getRed(), t.getGreen(), t.getBlue(), orig.getAlpha());
                if(orig.getAlpha() != 0)
                    img.setRGB(x, y, c.getRGB());
            }
        }
        return img;
    }

    public static void updateLookAndFeel() {
        UIManager.put("Button.disabledText", Colors.get(Colors.Background1).darker());
        UIManager.put("Label.foreground", Colors.get(Colors.Foreground1));
        UIManager.put("RadioButton._background", Colors.get(Colors.Background1));
    }
}
