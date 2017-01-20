package syncjam.ui;

import java.awt.*;
import java.awt.font.TextAttribute;
import java.awt.font.TransformAttribute;
import java.awt.geom.AffineTransform;
import java.util.HashMap;
import java.util.Map;

public class Colors
{
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
    private static Color[] currentColors = defaultColors;

    public static Color c_Background1 = currentColors[0];
    public static Color c_Background2 = currentColors[1];
    public static Color c_Foreground1 = currentColors[2];
    public static Color c_Foreground2 = currentColors[3];
    public static Color c_Highlight   = currentColors[4];
    public static Color c_Highlight2  = currentColors[5];

    public static void setColorScheme(Color[] colors)
    {
        if(colors.length == defaultColors.length)
        {
            for(int i = 0; i < colors.length; i++)
            {
                currentColors[i] = colors[i];
            }
        }
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
