package syncjam.ui;

import syncjam.ui.base.CustomFrame;
import syncjam.ui.buttons.TextButton;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

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
        TextButton okButton = new TextButton("OK", 70, 30) {
            protected void clicked() { window.dispose(); }
        };
        okButton.setBorder( new EmptyBorder(16, 16, 16, 16) );
        okButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(okButton);
        //

        window.getContentPanel().add(panel);
        window.setFocusable(true);
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

    private static CustomFrame createWindow(String title) {
        CustomFrame w = new CustomFrame(350, 80, title);
        w.setLocationRelativeTo(UIServices.getMainWindow());
        w.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        w.setAlwaysOnTop(true);
        w.allowMinimizing(false);
        w.allowResizing(false);

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
