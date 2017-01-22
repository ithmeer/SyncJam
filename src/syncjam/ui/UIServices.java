package syncjam.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Created by Marty on 1/21/2017.
 */
public class UIServices
{
    private static JFrame mainWindow;
    private static SyncJamUI syncJamUI;

    public static JFrame getMainWindow() {
        return mainWindow;
    }
    public static void setMainWindow(JFrame mainWindow) {
        UIServices.mainWindow = mainWindow;
    }

    public static SyncJamUI getSyncJamUI() {
        return syncJamUI;
    }
    public static void setSyncJamUI(SyncJamUI syncJamUI) {
        UIServices.syncJamUI = syncJamUI;
    }
}
