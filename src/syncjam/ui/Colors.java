package syncjam.ui;

import java.awt.*;
import java.awt.font.TextAttribute;
import java.awt.font.TransformAttribute;
import java.awt.geom.AffineTransform;
import java.util.HashMap;
import java.util.Map;

public enum Colors
{
    Background1,
    Background2,
    Foreground1,
    Foreground2,
    Highlight,
    Highlight2;

    public static final Color[] defaultColors = {
            new Color(27, 27, 27),   //Background1
            new Color(49, 49, 49),   //Background2
            new Color(255, 255, 255),//Foreground1
            new Color(188, 188, 188),//Foreground2
            new Color(44, 130, 226), //Highlight
            new Color(209, 72, 58),  //Highlight2
    };
    public static final Color[] lightColors = {
            new Color(238, 238, 238),//Background1
            new Color(217, 217, 217),//Background2
            new Color(0, 0, 0),      //Foreground1
            new Color(67, 67, 67),   //Foreground2
            new Color(44, 130, 226), //Highlight
            new Color(226, 82, 61),  //Highlight2
    };
    public static final Color[] blueberry = {
            new Color(29, 43, 59),   //Background1
            new Color(24, 36, 51),   //Background2
            new Color(87, 225, 255), //Foreground1
            new Color(170, 211, 247),//Foreground2
            new Color(255, 255, 255),//Highlight
            new Color(226, 82, 61),  //Highlight2
    };
    public static final Color[] plum = {
            new Color(68, 26, 50),   //Background1
            new Color(53, 23, 39),   //Background2
            new Color(255, 255, 255),//Foreground1
            new Color(188, 188, 188),//Foreground2
            new Color(225, 190, 65), //Highlight
            new Color(226, 82, 61),  //Highlight2
    };
    public static final Color[] test2 = {
            new Color(17, 22, 27),   //Background1
            new Color(31, 39, 49),   //Background2
            new Color(87, 225, 255), //Foreground1
            new Color(52, 118, 172), //Foreground2
            new Color(255, 255, 255),//Highlight
            new Color(226, 82, 61),  //Highlight2
    };

    private static Color[] currentColors = defaultColors.clone();

    public static Color get(Colors c)
    {
        return currentColors[c.ordinal()];
    }

    public static void setColorScheme(Color[] colors)
    {
        currentColors = colors.clone();
        UIServices.updateLookAndFeel();
    }
    public static void setColor(Colors which, Color newColor)
    {
         currentColors[which.ordinal()] = newColor;
        UIServices.updateLookAndFeel();
    }

    public static void setFont(Graphics g, int size)
    {
        Font font = new Font(Font.SANS_SERIF, Font.PLAIN, size);

        Map<TextAttribute, Object> attributes = new HashMap<TextAttribute, Object>();
        attributes.put(TextAttribute.TRACKING, 0.075);
        attributes.put(TextAttribute.KERNING, TextAttribute.KERNING_ON);
        attributes.put(TextAttribute.TRANSFORM, new TransformAttribute(AffineTransform.getScaleInstance(1, 1.02)));
        Font font2 = font.deriveFont(attributes);

        g.setFont(font2);
    }
}
