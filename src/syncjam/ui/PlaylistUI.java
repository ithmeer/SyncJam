package syncjam.ui;

import syncjam.Song;
import syncjam.base.Updatable;
import syncjam.ui.buttons.base.ScrollbarUI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.ArrayList;

public class PlaylistUI extends JPanel
{
    private int myW, myH;
    private ScrollbarUI scrollbar;
    private ArrayList<Song> songs = new ArrayList<Song>();

    public PlaylistUI()
    {
        myW = 350;
        myH = 0;//440;

        setMinimumSize(new Dimension(myW, myH));
        setBackground(Colors.c_Background2);
        this.setLayout(new BorderLayout());

        scrollbar = new ScrollbarUI(Colors.c_Background2);
        this.add(scrollbar, BorderLayout.EAST);

        for(int i = 0; i < 42; i++)
            songs.add(new Song("Song " + i, "Artist", "Album", 60));

        this.addMouseWheelListener(new MouseWheelListener() {
            @Override
            public void mouseWheelMoved(MouseWheelEvent e)
            {
                scrollbar.scrollEvent(e);
            }
        });
    }
    public void paintComponent(Graphics g)
    {
        super.paintComponent(g);

        int xOffset = 8, yOffset = 8;
        int itemHeight = 50;

        scrollbar.setMaxValue(songs.size()*itemHeight);
        //System.out.println(scrollbar.getMaxValue() + " - " + scrollbar.getValue());

        for(int i = 0; i < songs.size(); i++)
        {
            g.setColor(Colors.c_Foreground2);
            Colors.setFont(g, 16);
            g.drawString(""+i, xOffset, (yOffset+itemHeight/2) + i*itemHeight - (scrollbar.getValue()));
        }
    }
}
