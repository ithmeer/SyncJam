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
}
