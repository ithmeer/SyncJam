package syncjam.ui.net;

import syncjam.interfaces.ServiceContainer;
import syncjam.ui.buttons.base.TextLabelUI;
import syncjam.utilities.UserInfo;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Created by Marty on 1/22/2017.
 */
public class UserListPanel extends JPanel
{
    public UserListPanel(ServiceContainer services)
    {
        this.setPreferredSize(new Dimension(252, 500));
        this.setMinimumSize(new Dimension(252, 500));
        this.setBorder(new EmptyBorder(8,8,8,8));
        this.setLayout(new BorderLayout());
        this.setOpaque(false);

        TextLabelUI title = new TextLabelUI("User List", JLabel.CENTER);
        title.setBorder(new EmptyBorder(8,8,8,8));
        this.add(title, BorderLayout.NORTH);
        title.validate();

        UserListUI userListUI = new UserListUI();
        this.add(userListUI, BorderLayout.CENTER);
        userListUI.validate();

        String[] name1 = {"Toast", "Eagle", "Reggae", "Anime", "Gross & Terrible", "Hot Pocket", "Nose", "Amazing", "Dog", "Slime", "Swamp", "Salmon", "Horse", "Jam", "Skeleton", "Furry", "Nerd", "Artist"};
        String[] name2 = {"Boy", "Lady", "Lover", "Taster", "Connoisseur", "Bones", "Man", "Mess", "Nose", "Pete", "Speed", "Swamp", "Berg", "Gravy", "Nerd", "Master", "Artist", "Loser", "Wizard"};

        java.util.Random r = new java.util.Random();
        for(int i = 0; i < 30; i++) {
            String name = name1[r.nextInt(name1.length)] + " " + name2[r.nextInt(name2.length)];
            userListUI.add(new UserInfo(name, "", 0));
        }
        for(int i = 0; i < 3; i++) {
            String name = name1[r.nextInt(name1.length)] + " " + name2[r.nextInt(name2.length)];
            userListUI.add(new UserInfo(name, "", 1));
        }
        userListUI.add(new UserInfo("Dog Wizard", "", 2));

        repaint();
    }
}
