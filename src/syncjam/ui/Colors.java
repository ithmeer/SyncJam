package syncjam.ui;

import java.awt.*;
import java.awt.font.TextAttribute;
import java.awt.font.TransformAttribute;
import java.awt.geom.AffineTransform;
import java.util.HashMap;
import java.util.Map;

public class Colors
{
    public static Color[] defaultColors = {
            new Color(27, 27, 27),   //Background1
            new Color(49, 49, 49),   //Background2
            new Color(255, 255, 255),//Foreground1
            new Color(188, 188, 188),//Foreground2
            new Color(44, 130, 226), //Highlight
    };
    public static Color[] lightColors = {
            new Color(238, 238, 238),//Background1
            new Color(217, 217, 217),//Background2
            new Color(0, 0, 0),      //Foreground1
            new Color(67, 67, 67),   //Foreground2
            new Color(44, 130, 226), //Highlight
    };
    public static Color c_Background1 = defaultColors[0];
    public static Color c_Background2 = defaultColors[1];
    public static Color c_Foreground1 = defaultColors[2];
    public static Color c_Foreground2 = defaultColors[3];
    public static Color c_Highlight   = defaultColors[4];

    public static void setFont(Graphics g, int size)
    {
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(
                RenderingHints.KEY_TEXT_ANTIALIASING,
                RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);

        Font font = new Font("Calibri", Font.PLAIN, size);

        Map<TextAttribute, Object> attributes = new HashMap<TextAttribute, Object>();
        attributes.put(TextAttribute.TRACKING, 0.075);
        attributes.put(TextAttribute.KERNING, TextAttribute.KERNING_ON);
        attributes.put(TextAttribute.TRANSFORM, new TransformAttribute(AffineTransform.getScaleInstance(1, 1.04)));
        Font font2 = font.deriveFont(attributes);

        g2.setFont(font2);
    }
}
