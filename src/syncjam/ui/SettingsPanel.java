package syncjam.ui;

import syncjam.interfaces.ServiceContainer;
import syncjam.ui.base.ItemList;
import syncjam.ui.buttons.base.TextFieldUI;
import syncjam.ui.buttons.base.TextLabelUI;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Created by Marty on 1/24/2017.
 * Tray Panel for general SyncJam Settings
 */
public class SettingsPanel extends JPanel
{
    public SettingsPanel(ServiceContainer services) {
        this.setPreferredSize(new Dimension(250, 500));
        this.setMinimumSize(new Dimension(250, 500));
        this.setLayout(new BorderLayout());
        this.setOpaque(false);

        TextLabelUI title = new TextLabelUI("Settings", JLabel.CENTER);
        title.setBorder(new EmptyBorder(8, 8, 8, 8));
        this.add(title, BorderLayout.NORTH);
        title.validate();

        /*
        ItemList<SettingsItem> settingsList = new ItemList<>();
        settingsList.setBorder(new EmptyBorder(30,8,8,8));
        this.add(settingsList, BorderLayout.CENTER);

        /*
        JPanel settings1 = new JPanel(new GridLayout(5,1,0,4));
        settings1.setOpaque(false);

        settings1.setBorder(new EmptyBorder(20,8,8,8));
        this.add(settings1, BorderLayout.PAGE_START);

        settings1.add(new TextLabelUI("Username"));
        TextFieldUI userName = new TextFieldUI(30, "default_user");
        userName.setBGColor(Colors.Background2);
        settings1.add(userName);

        settings1.add(new JLabel());
        settings1.add(new TextLabelUI("Playlist:"));

        JCheckBox showMarker = new JCheckBox("Show Marker");
        showMarker.setOpaque(false);
        showMarker.setFocusable(false);
        settings1.add(showMarker);
        */
    }
    private class SettingsItem
    {
        protected final String _label;
        protected final ItemList _list;
        private SettingsItem(ItemList l) {
            _label = "";
            _list = l;
        }
        private SettingsItem(String label, ItemList l) {
            _label = label;
            _list = l;
        }
        public void draw(Graphics g, int x, int y, int itemHeight){};
        public void clicked(){};
    }
    private class SettingsCheckbox extends SettingsItem
    {
        private boolean _enabled = false;

        private SettingsCheckbox(String label, ItemList l) {
            this(label, l, false);
        }
        private SettingsCheckbox(String label, ItemList l, boolean enabled) {
            super(label, l);
            _enabled = enabled;
        }

        @Override
        public void draw(Graphics g, int x, int y, int itemHeight) {
            super.draw(g, x, y, itemHeight);

            int fontSize = g.getFontMetrics().getHeight();
            int rightSide = _list.getRight();

            g.setColor(Colors.get(Colors.Foreground1));
            g.drawString(_label, x, y+itemHeight/2+fontSize/2);

            g.drawRect(rightSide - itemHeight, 2, itemHeight-4, itemHeight-4);
            g.setColor(Colors.get(Colors.Foreground2));
            g.fillRect(rightSide - itemHeight+2, 4, itemHeight-8, itemHeight-8);
        }
    }
}
