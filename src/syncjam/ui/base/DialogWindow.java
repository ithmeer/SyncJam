package syncjam.ui.base;

import syncjam.ui.Colors;
import syncjam.ui.CustomFrame;
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
        showErrorMessage(message, null);
    }

    public static void showErrorMessage(String message, CustomFrame rel)
    {
        //final JFrame window = new JFrame("Error");
        final CustomFrame window = new CustomFrame(300, 60);
        window.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        window.setAlwaysOnTop(true);
        window.allowMinimizing(false);

        //
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 60, 20, 60));
        panel.setBackground(Colors.c_Background2.darker());
        panel.setForeground(Colors.c_Foreground1);
        JLabel text = new JLabel(message);
        text.setBackground(Colors.c_Background1);
        text.setForeground(Colors.c_Foreground1);
        panel.add(text, BorderLayout.CENTER);
        ButtonUI okButton = new ButtonUI(70,40, null) {
            protected void clicked() { window.dispose(); }
        };
        okButton.setText("OK");
        panel.add(okButton, BorderLayout.SOUTH);
        //

        window.getContentPanel().add(panel);

        window.open();
        window.setLocationRelativeTo(rel);
    }
}
