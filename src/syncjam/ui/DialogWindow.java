package syncjam.ui;

import syncjam.ui.base.ColorPickerUI;
import syncjam.ui.base.CustomFrame;
import syncjam.ui.buttons.TextButton;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import java.awt.*;
import java.awt.event.ActionEvent;
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

    static JFileChooser openFileChooser()
    {
        CustomFrame chooserWindow = createWindow("Select Files");
        JFileChooser fileChooser = new JFileChooser(){
            @Override
            public void cancelSelection() {
                super.cancelSelection();
                chooserWindow.dispose();
            }
        };
        fileChooser.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                chooserWindow.dispose();
            }
        });
        fileChooser.setMultiSelectionEnabled(true);
        FileNameExtensionFilter filter = new FileNameExtensionFilter("Audio Files", "mp3", "m4a", "wav", "flac", "wma");
        fileChooser.setFileFilter(filter);

        JPanel whitePanel = new JPanel();
        whitePanel.add(fileChooser);
        chooserWindow.getContentPanel().add(whitePanel);
        chooserWindow.open();
        return fileChooser;
    }


    static CustomFrame openColorPicker(Colors color)
    {
        CustomFrame colorWindow = createWindow("Select Color");

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

    /*public static void openColorPickerOld(Colors color)
    {
        CustomFrame colorWindow = createWindow("Pick Color");
        JColorChooser colorChooser = new JColorChooser(Colors.get(color));
        colorChooser.setOpaque(false);

        ChangeListener c = e -> {
            Color t = colorChooser.getColor();
            Color newColor = new Color(t.getRed(), t.getGreen(), t.getBlue()); //remove transparency, NO THANKS DUDER
            Colors.setColor(color, newColor);
            colorWindow.repaint();
        };
        colorChooser.getSelectionModel().addChangeListener(c);

        colorWindow.getContentPanel().add(colorChooser);
        colorWindow.open();
    }*/

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
