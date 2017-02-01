package syncjam.ui;

import javax.swing.*;

/**
 * Created by Marty on 1/21/2017.
 */
public class UIServices
{
    private static JFrame _mainWindow;
    private static SyncJamUI _syncJamUI;

    public static JFrame getMainWindow() {
        return _mainWindow;
    }
    public static void setMainWindow(JFrame mainWindow) {
        UIServices._mainWindow = mainWindow;
    }

    public static SyncJamUI getSyncJamUI() {
        return _syncJamUI;
    }
    public static void setSyncJamUI(SyncJamUI syncJamUI) {
        UIServices._syncJamUI = syncJamUI;
    }

    public static void updateLookAndFeel() {
        UIManager.put("Button.disabledText", Colors.get(Colors.Background1).darker());
        UIManager.put("Label.foreground", Colors.get(Colors.Foreground1));
        UIManager.put("RadioButton.background", Colors.get(Colors.Background1));
    }
}
