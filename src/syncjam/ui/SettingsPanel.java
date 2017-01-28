package syncjam.ui;

import syncjam.interfaces.ServiceContainer;
import syncjam.interfaces.Settings;
import syncjam.ui.base.ItemList;
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
    private final SettingsListUI settingsList;
    private final Settings _syncJamSettings;

    public SettingsPanel(ServiceContainer services) {
        _syncJamSettings = services.getService(Settings.class);

        this.setPreferredSize(new Dimension(250, 500));
        this.setMinimumSize(new Dimension(250, 500));
        this.setLayout(new BorderLayout());
        this.setOpaque(false);

        TextLabelUI title = new TextLabelUI("Settings", JLabel.CENTER);
        title.setBorder(new EmptyBorder(8, 8, 8, 8));
        this.add(title, BorderLayout.NORTH);
        title.validate();

        settingsList = new SettingsListUI();
        settingsList.setBorder(new EmptyBorder(30,8,8,8));
        this.add(settingsList, BorderLayout.CENTER);

        new SettingsItem("General:", settingsList);
        SettingsString usernameField = new SettingsString("Username", settingsList){  //TODO: _syncJamSettings.getUserName()
            @Override
            public void output() {
                //TODO: _syncJamSettings.setUserName(getValue());
            }
        };

        new SettingsItem("", settingsList);
        new SettingsItem("Playlist:", settingsList);
        SettingsCheckbox showMarker = new SettingsCheckbox("Show Marker", settingsList){ //TODO: _syncJamSettings.getShowMarker()
            @Override
            public void output() {
                //TODO: _syncJamSettings.setShowMarker(getCheckboxOn());
            }
        };
        SettingsCheckbox followMarker = new SettingsCheckbox("Follow Marker", settingsList){ //TODO: _syncJamSettings.getFollowMarker()
            @Override
            public void output() {
                //TODO: _syncJamSettings.setFollowMarker(getCheckboxOn());
            }
        };

        /*
        JPanel settings1 = new JPanel(new GridLayout(5,1,0,4));
        settings1.setOpaque(false);

        settings1.setBorder(new EmptyBorder(20,8,8,8));
        this.add(settings1, BorderLayout.PAGE_START);

        settings1.add(new TextLabelUI("Username"));
        TextFieldUI userName = new TextFieldUI(30, "default_user");
        userName.setBGColor(Colors.Background2);
        settings1.add(userName);
        */
    }
    public class SettingsItem
    {
        final String _label;
        final ItemList _list;
        protected boolean _highlightItem = false;
        private SettingsItem(ItemList l) {
            _label = "";
            _list = l;
        }
        private SettingsItem(String label, ItemList l) {
            _label = label;
            _list = l;
            _list.add(this);
        }
        
        public boolean highlightItem() {
            return _highlightItem;
        }
        
        public void draw(Graphics g, int x, int y)
        {
            int itemHeight = _list.getItemHeight();
            int fontSize = g.getFontMetrics().getHeight();
            
            g.setColor(Colors.get(Colors.Foreground1));
            g.drawString(_label, _list.getLeft()+4, y+itemHeight/2 + fontSize/3);
        }
        public void clicked() {
            output();
        }
        public void output() {};
    }
    private class SettingsString extends SettingsItem
    {
        private String _value;
        private SettingsString(String label, ItemList l) {
            this(label, l, "default");
        }
        private SettingsString(String label, ItemList l, String value) {
            super(label, l);
            _value = value;
            _highlightItem = true;
        }



    }
    private class SettingsCheckbox extends SettingsItem
    {
        private boolean _enabled = false;
        private int _boxSize = 18;

        private SettingsCheckbox(String label, ItemList l) {
            this(label, l, false);
        }
        private SettingsCheckbox(String label, ItemList l, boolean enabled) {
            super(label, l);
            _enabled = enabled;
            _highlightItem = true;
        }

        public boolean getCheckboxOn() {
            return _enabled;
        }

        @Override
        public void clicked() {
            _enabled = !_enabled;
            super.clicked();
        }

        @Override
        public void draw(Graphics g, int x, int y) {
            super.draw(g, x, y);

            int boxX = x + _list.getRight() - _boxSize - 6;
            int middle = y + _list.getItemHeight()/2;

            g.drawRect(boxX, middle - _boxSize /2, _boxSize, _boxSize);
            if(_enabled)
                g.fillRect(boxX + 3, middle - _boxSize /2 + 3, _boxSize - 5, _boxSize - 5);
        }
    }
}
