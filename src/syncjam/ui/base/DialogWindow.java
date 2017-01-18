package syncjam.ui.base;

import syncjam.ui.Colors;
import syncjam.ui.buttons.base.ButtonUI;

import javax.swing.*;
import java.awt.*;

/**
 * Created by Marty on 1/17/2017.
 */
public class DialogWindow
{
    public static void showErrorMessage(String message)
    {
        final JFrame window = new JFrame("Error");
        window.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        window.setLocationRelativeTo(null);

        //
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 60, 20, 60));
        panel.setBackground(Colors.c_Background2);
        panel.setForeground(Colors.c_Foreground1);
        JTextArea text = new JTextArea(message);
        text.setBackground(Colors.c_Background2);
        text.setForeground(Colors.c_Foreground1);
        panel.add(text);
        ButtonUI okButton = new ButtonUI(70,30, null) {
            protected void clicked() { window.dispose(); }
        };
        okButton.setText("OK");
        panel.add(okButton);
        //

        window.add(panel);

        window.pack();
        window.setVisible(true);
    }
}
