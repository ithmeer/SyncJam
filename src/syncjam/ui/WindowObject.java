package syncjam.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;


public class WindowObject extends JFrame
{
    public boolean Resizable = false;
    public WindowObject(int minW, int minH, boolean resizable)
    {
        this.setTitle("SyncJam");
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        this.setUndecorated(false);

        //this.add(panel);
        this.setMinimumSize(new Dimension(minW, minH));
        this.setPreferredSize(new Dimension(minW + 20, minH + 150));
        Resizable = resizable;

        this.addWindowListener(new WindowAdapter(){
            public void windowClosing(WindowEvent e){
                close();
                System.exit(0);//
            }
        });
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

    private void close()
    {
        // TODO: 6/9/2016 Disconnect from server, etc. 
    }
}
