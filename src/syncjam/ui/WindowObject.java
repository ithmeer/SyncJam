package syncjam.ui;

import javax.swing.*;
import java.awt.*;

public class WindowObject extends JFrame
{
    public boolean Resizable = false;
    public WindowObject(int minW, int minH, boolean resizable)
    {
        this.setTitle("SyncJam");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setUndecorated(false);

        //this.add(panel);
        this.setMinimumSize(new Dimension(minW, minH));
        this.setPreferredSize(new Dimension(minW + 20, minH + 150));
        Resizable = resizable;
    }
    public WindowObject(JPanel panel)
    {
        this(500, 250);
    }

    public WindowObject(int minW, int minH)
    {
        this(minW, minH, true);
    }

    public void open()
    {
        this.pack();
        this.setLocationRelativeTo(null);
        this.setResizable(Resizable);
        this.setVisible(true);
    }
}
