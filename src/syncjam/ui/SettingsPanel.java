package syncjam.ui;

import syncjam.interfaces.ServiceContainer;
import syncjam.interfaces.Settings;
import syncjam.ui.base.ItemList;
import syncjam.ui.buttons.base.TextLabelUI;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.util.Arrays;

/**
 * Created by Marty on 1/24/2017.
 * Tray Panel for general SyncJam Settings
 */
public class SettingsPanel extends JPanel
{
    private final SettingsListUI settingsList;
    private final Settings _syncJamSettings;

    private boolean enterToggle = false;
    private KeyAdapter keys = new KeyAdapter() {
        @Override
        public void keyReleased(KeyEvent e) {
            super.keyReleased(e);
            if(isVisible()) {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_ENTER:
                        if(settingsList.getSelectedItem() != null && !enterToggle) settingsList.getSelectedItem().clicked();
                        if(settingsList.getSelectedItem() instanceof SettingsString) enterToggle = !enterToggle;
                        break;
                    case KeyEvent.VK_UP:
                        do settingsList.moveSelection("up");
                        while(settingsList.getSelectedItem() != null && !settingsList.getSelectedItem().highlightItem());
                        enterToggle = false;
                        break;
                    case KeyEvent.VK_DOWN:
                        do settingsList.moveSelection("down");
                        while(settingsList.getSelectedItem() != null && !settingsList.getSelectedItem().highlightItem());
                        enterToggle = false;
                        break;
                    case KeyEvent.VK_TAB:
                        UIServices.getSyncJamUI().togglePanel(SettingsPanel.this);
                        enterToggle = false;
                        break;
                }
            }
        }
    };
    @Override
    public void setVisible(boolean aFlag) {
        super.setVisible(aFlag);
        if(aFlag)
            UIServices.getMainWindow().addKeyListener(keys);
        else {
            UIServices.getMainWindow().removeKeyListener(keys);
        }
    }

    SettingsPanel(ServiceContainer services) {
        _syncJamSettings = services.getService(Settings.class);

        this.setPreferredSize(new Dimension(252, 500));
        this.setMinimumSize(new Dimension(252, 500));
        this.setBorder(new EmptyBorder(8, 8, 8, 8));
        this.setLayout(new BorderLayout());
        this.setOpaque(false);

        TextLabelUI title = new TextLabelUI("Settings", JLabel.CENTER);
        title.setBorder(new EmptyBorder(8, 8, 8, 8));
        this.add(title, BorderLayout.NORTH);
        title.validate();

        settingsList = new SettingsListUI();
        settingsList.setBorder(new EmptyBorder(30, 8, 8, 8));
        this.add(settingsList, BorderLayout.CENTER);

        new SettingsItem("General:", settingsList);
        SettingsString usernameField = new SettingsString("Username", settingsList, _syncJamSettings.getUserName()){
            @Override
            public void output() {
                super.output();
                _syncJamSettings.setUserName(getValue());
            }
        };
        SettingsString defaultPortField = new SettingsString("Default Port", settingsList, _syncJamSettings.getDefaultPort()){
            @Override
            public void output() {
                super.output();
                _syncJamSettings.setDefaultPort(getValue());
            }
        };
        new SettingsItem("", settingsList);

        SettingsCheckbox fastScaling = new SettingsCheckbox("Use Fast Image Scaling", settingsList){ //TODO: , _syncJamSettings.getUseFastScaling()){
            @Override
            public void output() {
                super.output();
                //TODO: _syncJamSettings.setUseFastScaling(getCheckboxOn());
            }
        };
        SettingsCheckbox minimizeToTray = new SettingsCheckbox("Minimize To Tray", settingsList, _syncJamSettings.getMinimizeToTray()){
            @Override
            public void output() {
                super.output();
                _syncJamSettings.setMinimizeToTray(getCheckboxOn());
            }
        };
        

        new SettingsItem("", settingsList);

        new SettingsItem("Playlist:", settingsList);
        SettingsCheckbox showMarker = new SettingsCheckbox("Show Marker", settingsList, _syncJamSettings.getShowMarker()){
            @Override
            public void output() {
                super.output();
                _syncJamSettings.setShowMarker(getCheckboxOn());
            }
        };
        SettingsCheckbox followMarker = new SettingsCheckbox("Follow Marker", settingsList, _syncJamSettings.getFollowMarker()){
            @Override
            public void output() {
                super.output();
                _syncJamSettings.setFollowMarker(getCheckboxOn());
            }
        };
    }

    public class SettingsItem {
        final String _label;
        final ItemList _list;
        boolean _highlightItem = false;
        final int rightOffset = 16;

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
        
        public void draw(Graphics g, int x, int y){
            int itemHeight = _list.getItemHeight();
            int fontHeight = g.getFontMetrics().getHeight();
            
            g.setColor(Colors.get(_highlightItem ? Colors.Foreground1 : Colors.Foreground2));
            g.drawString(_label, _list.getLeft()+4, y+itemHeight/2 + fontHeight/3);
        }
        public void clicked() {
            output();
        }
        public void output() {}
    }
    public class SettingsString extends SettingsItem {
        private String _value;
        private final int _maxLength = 20;
        private int _pointer = 0;
        private int _selectStart = -1, _selectEnd = -1;
        
        private SettingsString(String label, ItemList l) {
            this(label, l, "default");
        }
        private SettingsString(String label, ItemList l, String value) {
            super(label, l);
            _value = value;
            _highlightItem = true;
        }

        private KeyAdapter _input = new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                super.keyReleased(e);

                int keyCode = e.getKeyCode();

                if(e.isControlDown())
                {
                    if (keyCode == KeyEvent.VK_BACK_SPACE)
                    {
                        while(_pointer < _value.length())
                            backspace();
                    }
                    else if (keyCode == KeyEvent.VK_DELETE)
                    {
                        while (_pointer > 0) {
                            _pointer--;
                            backspace();
                        }
                    }
                    else if(keyCode == KeyEvent.VK_A)
                        selectAll();
                    else if(keyCode == KeyEvent.VK_C)
                        copySelection();
                    else if(keyCode == KeyEvent.VK_V)
                        pasteSelection();
                    else if(keyCode == KeyEvent.VK_X) {
                        copySelection();
                        backspace();
                    }
                }
                if(!e.isAltDown())
                {
                    if(keyCode == KeyEvent.VK_HOME)
                        left(-1, e.isShiftDown());
                    else if(keyCode == KeyEvent.VK_END)
                        right(-1, e.isShiftDown());

                    if (keyCode == KeyEvent.VK_LEFT) {
                        if (e.isControlDown())
                            do left(1, e.isShiftDown());
                            while(_pointer < _value.length() && !Character.isSpaceChar(_value.charAt(_value.length()-_pointer-1)));
                        else
                            left(1, e.isShiftDown());
                    }
                    else if (keyCode == KeyEvent.VK_RIGHT)
                        if (e.isControlDown())
                            do right(1, e.isShiftDown());
                            while(_pointer > 0 && !Character.isSpaceChar(_value.charAt(_value.length()-_pointer-1)));
                        else
                            right(1, e.isShiftDown());
                }
            }
            @Override
            public void keyTyped(KeyEvent e) {
                super.keyTyped(e);

                if(!e.isActionKey() &! e.isControlDown())
                {
                    char key = e.getKeyChar();
                    switch (key){
                        case KeyEvent.VK_BACK_SPACE:
                            backspace();
                            break;
                        case KeyEvent.VK_ESCAPE:
                        case KeyEvent.VK_ENTER:
                            output();
                            _list.removeKeyListener(_input);
                            _list.transferFocusBackward();
                            break;
                        case KeyEvent.VK_DELETE:
                            if(_pointer > 0) {
                                _pointer--;
                                backspace();
                            }
                            break;
                        default:
                            if(_selectStart > -1)
                                backspace();
                            if(_value.length() < _maxLength) {
                                int pointerPos = _value.length()-_pointer;
                                _value = _value.substring(0, pointerPos) + key + _value.substring(pointerPos);
                            }
                    }
                }
            }
        };

        public String getValue(){
            return _value;
        }

        private void left(int amt, boolean sel) {
            if(amt == -1) amt = _value.length() - _pointer; //move all the way to the left side
            if(sel && _selectStart == -1) _selectStart = _pointer;

            if(_pointer < _value.length()) _pointer += amt;

            if(sel) _selectEnd = _pointer;
            else clearSelection();
        }
        private void right(int amt, boolean sel) {
            if(amt == -1) amt = _pointer; //move all the way to the right side
            if(sel && _selectStart == -1) _selectStart = _pointer;

            if(_pointer > 0) _pointer -= amt;

            if(sel) _selectEnd = _pointer;
            else clearSelection();
        }
        private void backspace() {
            if(_value.length() > 0)
            {
                if(_selectStart > -1 && _selectEnd > -1)
                {
                    int p1 = _value.length() - Math.max(_selectStart, _selectEnd);
                    int p2 = _value.length() - Math.min(_selectStart, _selectEnd);
                    _value = _value.substring(0, p1) + _value.substring(p2);
                    _pointer = _value.length() - p1;
                    clearSelection();
                }
                else
                {
                    int pointerPos = _value.length()-_pointer;
                    _value = _value.substring(0, pointerPos-1) + _value.substring(pointerPos);
                }
            }
        }
        private void copySelection() {
            if(_selectStart > -1) {
                int p1 = _value.length() - Math.max(_selectStart, _selectEnd);
                int p2 = _value.length() - Math.min(_selectStart, _selectEnd);

                Clipboard systemClipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                StringSelection stringSelection = new StringSelection(_value.substring(p1, p2));
                systemClipboard.setContents(stringSelection, null);
            }
        }
        private void pasteSelection() {
            String paste = "";
            DataFlavor dataFlavor = DataFlavor.stringFlavor;
            Clipboard systemClipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            if(systemClipboard.isDataFlavorAvailable(dataFlavor)) {
                try {
                    paste = (String) systemClipboard.getData(dataFlavor);

                } catch (UnsupportedFlavorException | IOException e) {
                    e.printStackTrace();
                }
            }
            if(paste.length() > 0)
                _value = _value.substring(0, _value.length()-_pointer) + paste + _value.substring(_pointer);
        }
        private void selectAll() {
            _selectStart = _value.length();
            _selectEnd = 0;
        }
        private void clearSelection() {
            if(_selectStart > -1)
            {
                _selectStart = -1;
                _selectEnd = -1;
            }
        }

        @Override
        public void clicked() {
            _list.addKeyListener(_input);
            _pointer = 0;
            selectAll();
            _list.grabFocus();
        }
        @Override
        public void draw(Graphics g, int x, int y) {
            super.draw(g, x, y);
            if(!_list.hasFocus() && Arrays.asList(_list.getKeyListeners()).contains(_input))
            {
                _list.removeKeyListener(_input);
                output();
            }

            int middle = y + _list.getItemHeight()/2;
            int fontHeight = g.getFontMetrics().getHeight();
            int stringX = x + _list.getRight() - rightOffset - g.getFontMetrics().stringWidth(_value);

            //Selection Highlight
            if(_selectStart > -1 && _selectEnd > -1)
            {
                g.setColor(Colors.get(Colors.Highlight));
                int p1 = _value.length() - Math.max(_selectStart, _selectEnd);
                int p2 = _value.length() - Math.min(_selectStart, _selectEnd);
                int startOffset = _value.length() > 0 ? g.getFontMetrics().stringWidth(_value.substring(p1)) : 0;
                int highlighLength = _value.length() > 0 ? g.getFontMetrics().stringWidth(_value.substring(p1, p2)) : 0;
                g.fillRect(x + _list.getRight() - rightOffset - startOffset , middle + fontHeight / 2, highlighLength, -fontHeight);
            }

            //Draw String
            g.setColor(Colors.get(Colors.Foreground2));
            g.drawString(_value, stringX, middle + fontHeight/3);

            //Pointer
            if(Arrays.asList(_list.getKeyListeners()).contains(_input))
            {
                long time = System.currentTimeMillis() % 1000;

                g.setColor(Colors.get(Colors.Foreground1));
                if(time < 500) {
                    int pointerPos = _value.length()-_pointer;
                    int trailingSpace = _value.length() > 0 ? g.getFontMetrics().stringWidth(_value.substring(pointerPos)) : 0;
                    g.drawString("|", x + _list.getRight() - rightOffset - trailingSpace , middle + fontHeight / 3);
                }
            }
        }
        @Override
        public void output() {
            clearSelection();
            _value = _value.trim();
            if(_value.equals("")) _value = "default";
            super.output();
            _list.transferFocusBackward();
        }
    }
    public class SettingsCheckbox extends SettingsItem {
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

            int boxX = x + _list.getRight() - rightOffset - _boxSize;
            int middle = y + _list.getItemHeight()/2;

            g.setColor(Colors.get(Colors.Foreground2));
            g.drawRect(boxX, middle - _boxSize /2, _boxSize, _boxSize);
            g.setColor(Colors.get(Colors.Foreground1));
            if(_enabled)
                g.fillRect(boxX + 3, middle - _boxSize /2 + 3, _boxSize - 5, _boxSize - 5);
        }
    }
}
