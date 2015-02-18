package syncjam.ui;

import javax.swing.*;
import java.awt.*;

public class WindowObject extends JFrame
{
    public WindowObject(JPanel panel, int minW, int minH, boolean resizable)
    {
        this.setTitle("SyncJam");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setUndecorated(true);

        this.add(panel);
        this.setSize((int) panel.getPreferredSize().getWidth(),
                     (int) panel.getPreferredSize().getHeight());
        this.setMinimumSize(new Dimension(minW, minH));

        this.pack();
        this.setLocationRelativeTo(null);
        this.setResizable(resizable);
        this.setVisible(true);
    }

    public WindowObject(JPanel panel, int minW, int minH)
    {
        this(panel, minW, minH, false);
    }

    public WindowObject(JPanel panel)
    {
        this(panel, 500, 250);
    }
}
