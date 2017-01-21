package syncjam.ui.base;

import syncjam.ui.Colors;
import syncjam.ui.CustomFrame;
import syncjam.ui.buttons.TextButton;
import syncjam.ui.buttons.base.ButtonUI;

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
    private static Component mainWindow;

    public static void setMainWindow(Component c)
    {
        mainWindow = c;
    }

    public static void showErrorMessage(String message)
    {
        //final JFrame window = new JFrame("Error");
        final CustomFrame window = new CustomFrame(350, 80);
        window.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        window.setAlwaysOnTop(true);
        window.allowMinimizing(false);
        window.allowResizing(false);

        //
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        panel.setBackground(Colors.get(Colors.Background1));
        panel.setForeground(Colors.get(Colors.Foreground1));
        panel.setBorder(BorderFactory.createEmptyBorder(0, 40, 10, 40));

        JTextPane text = new JTextPane();
        text.setAlignmentX(Component.CENTER_ALIGNMENT);
        text.setText(message);

        StyledDocument doc = text.getStyledDocument();
        SimpleAttributeSet center = new SimpleAttributeSet();
        StyleConstants.setAlignment(center, StyleConstants.ALIGN_CENTER);
        doc.setParagraphAttributes(0, doc.getLength(), center, false);


        text.setBorder( new EmptyBorder(16, 16, 16, 16) );
        text.setBackground(Colors.get(Colors.Background1));
        text.setForeground(Colors.get(Colors.Foreground1));
        panel.add(text);

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
        window.setLocationRelativeTo(mainWindow);
    }
}
