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

        for(int i = 0; i < 26; i++)
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

        scrollbar.setMaxValue(songs.size() * itemHeight + yOffset*2);
        //System.out.println(scrollbar.getMaxValue() + " - " + scrollbar.getValue());

        for(int i = 0; i < songs.size(); i++)
        {
            int itemYPos = yOffset + i*itemHeight - (scrollbar.getValue());

            g.setColor(Colors.c_Foreground2);

            Colors.setFont(g, 14);
            int textHeight = (int)g.getFontMetrics().getHeight();
            g.drawString(""+i, xOffset, itemYPos + itemHeight/2 + textHeight/2);

            int ins = 5; //album art inset
            g.drawRect(xOffset + ins + 20, itemYPos + ins, itemHeight - ins*2, itemHeight - ins*2);
            g.setColor(Colors.c_Background1);
            g.fillRect(xOffset + ins + 21, itemYPos + ins +1, itemHeight - ins*2 - 2, itemHeight - ins*2 - 2);
        }
    }
}
