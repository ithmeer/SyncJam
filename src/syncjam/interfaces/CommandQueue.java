package syncjam.interfaces;

import syncjam.net.CommandPacket;
import syncjam.utilities.CommandFlags;

import java.util.EnumSet;

/**
 * Created by Ithmeer on 9/11/2016.
 */
public interface CommandQueue
{
    void toggleEnabled(boolean state);

    void executeCommand(CommandPacket command);

    void gotoSong(int song);

    void gotoSong(int song, EnumSet<CommandFlags> flags);

    void kill();

    void kill(EnumSet<CommandFlags> flags);

    void nextSong();

    void nextSong(EnumSet<CommandFlags> flags);

    void prevSong();

    void prevSong(EnumSet<CommandFlags> flags);

    void moveSong(int from, int to);

    void moveSong(int from, int to, EnumSet<CommandFlags> flags);

    void removeSong(int song);

    void removeSong(int song, EnumSet<CommandFlags> flags);

    void playToggle(boolean state);

    void playToggle(boolean state, EnumSet<CommandFlags> flags);

    void seek(int percent);

    void seek(int percent, EnumSet<CommandFlags> flags);

    CommandPacket take() throws InterruptedException;
}
