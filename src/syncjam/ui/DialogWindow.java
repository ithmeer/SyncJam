package syncjam.ui;

import syncjam.ui.base.ColorPickerUI;
import syncjam.ui.base.CustomFrame;
import syncjam.ui.buttons.TextButton;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeListener;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeListener;

/**
 * Created by Marty on 1/17/2017.
 * Various Dialog Window Calls
 */
public class DialogWindow
{
    public static CustomFrame showErrorMessage(String message) {
        return showErrorMessage(message, "Error");
    }
    public static CustomFrame showErrorMessage(String message, String title) {

        CustomFrame window = createWindow(title);

        // Panel
        JPanel panel = createPanel();
        window.cm.registerComponent(panel);
        //
        //TextPane
        JTextPane text = createText(message);
        text.setAlignmentX(Component.CENTER_ALIGNMENT);
        window.cm.registerComponent(text);
        panel.add(text);
        //
        //OK Button
        TextButton okButton = new TextButton(70, 30, "OK") {
            protected void clicked() { window.dispose(); }
        };
        okButton.setBorder( new EmptyBorder(16, 16, 16, 16) );
        okButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(okButton);
        //

        window.getContentPanel().add(panel);
        window.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                super.keyReleased(e);
                if(e.getKeyChar() == KeyEvent.VK_ENTER || e.getKeyChar() == KeyEvent.VK_ESCAPE )
                    okButton.doClick();
            }
        });

        window.open();
        return window;
    }
/*
    //https://javagraphics.java.net
    public static void openColorPicker(Colors originalColor)
    {
        CustomFrame colorWindow = createWindow("Pick Color");
        colorWindow.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                super.keyReleased(e);
                if(e.getKeyCode() == KeyEvent.VK_ESCAPE || e.getKeyCode() == KeyEvent.VK_ENTER)
                    colorWindow.dispose();
            }
        });
        colorWindow.setAlwaysOnTop(false);

        ColorPicker colorPicker = new ColorPicker(true, false);
        colorPicker.setMode(ColorPicker.HUE);
        colorPicker.setFocusable(false);
        //colorPicker.setOpaque(true);

        colorPicker.setColor(Colors.get(originalColor));
        PropertyChangeListener c = e -> {
            Color newColor = colorPicker.getColor();
            Colors.setColor(originalColor, newColor);
            colorWindow.repaint();
        };
        colorPicker.addPropertyChangeListener(ColorPicker.SELECTED_COLOR_PROPERTY, c);

        colorWindow.getContentPanel().add(colorPicker);
        colorWindow.open();
    }
*/
    public static CustomFrame openColorPicker(Colors color)
    {
        CustomFrame colorWindow = new CustomFrame(500, 300, "Select Color");
        colorWindow.setLocationRelativeTo(UIServices.getMainWindow());
        colorWindow.allowResizing(false);

        ColorPickerUI colorChooser = new ColorPickerUI(Colors.get(color));

        PropertyChangeListener c = e -> {
            Colors.setColor(color, (Color)e.getNewValue());
            colorWindow.repaint();
        };
        colorChooser.addChangeListener(c);

        colorWindow.getContentPanel().add(colorChooser);
        colorWindow.open();
        return colorWindow;
    }


    public static void openColorPickerOld(Colors color)
    {
        CustomFrame colorWindow = new CustomFrame(500,300, "Pick Color");
        colorWindow.setLocationRelativeTo(UIServices.getMainWindow());
        JColorChooser colorChooser = new JColorChooser(Colors.get(color));
        colorChooser.setOpaque(false);

        ChangeListener c = e -> {
            Color t = colorChooser.getColor();
            Color newColor = new Color(t.getRed(), t.getGreen(), t.getBlue()); //choose allows transparency, NO
            Colors.setColor(color, newColor);
            colorWindow.repaint();
        };
        colorChooser.getSelectionModel().addChangeListener(c);

        colorWindow.getContentPanel().add(colorChooser);
        colorWindow.open();
    }
    private static CustomFrame createWindow(String title) {
        CustomFrame w = new CustomFrame(350, 80, title);
        w.setLocationRelativeTo(UIServices.getMainWindow());
        w.setAlwaysOnTop(true);
        w.allowMinimizing(false);
        w.allowResizing(false);
        w.setFocusable(true);

        return w;
    }
    private static JPanel createPanel() {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBackground(Colors.get(Colors.Background1));
        p.setForeground(Colors.get(Colors.Foreground1));
        p.setBorder(BorderFactory.createEmptyBorder(0, 40, 10, 40));

        return p;
    }

    private static JTextPane createText(String m) {
        JTextPane t = new JTextPane();
        t.setText(m);
        t.setEditable(false);
        t.getCaret().deinstall(t);

        // Center Text
        StyledDocument doc = t.getStyledDocument();
        SimpleAttributeSet center = new SimpleAttributeSet();
        StyleConstants.setAlignment(center, StyleConstants.ALIGN_CENTER);
        doc.setParagraphAttributes(0, doc.getLength(), center, false);
        //
        t.setBorder( new EmptyBorder(16, 16, 16, 16) );
        t.setBackground(Colors.get(Colors.Background1));
        t.setForeground(Colors.get(Colors.Foreground1));

        return t;
    }
}
