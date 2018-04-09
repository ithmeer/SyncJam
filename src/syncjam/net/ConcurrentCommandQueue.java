package syncjam.net;

import syncjam.interfaces.CommandQueue;
import syncjam.interfaces.PlayController;
import syncjam.interfaces.Playlist;
import syncjam.utilities.CommandFlags;
import syncjam.utilities.CommandType;

import java.util.EnumSet;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Class to handle the commands to send to the socket. Thread-safe.
 * Created by Ithmeer on 10/23/2015.
 */
public class ConcurrentCommandQueue implements CommandQueue
{
    private final LinkedBlockingQueue<CommandPacket> _queue;
    private final PlayController _player;
    private final Playlist _playlist;
    private static final EnumSet<CommandFlags> NoFlags = EnumSet.noneOf(CommandFlags.class);

    // synchronized on this
    private boolean _enabled;

    public ConcurrentCommandQueue(PlayController player, Playlist playlist)
    {
        _queue = new LinkedBlockingQueue<>();
        _player = player;
        _playlist = playlist;

        synchronized (this)
        {
            _enabled = false;
        }
    }

    @Override
    public synchronized void toggleEnabled(boolean state)
    {
        _enabled = state;
    }

    @Override
    public synchronized void executeCommand(CommandPacket packet)
    {
        CommandType type = packet.getType();
        EnumSet<CommandFlags> flags = packet.getFlags();
        String[] args = packet.getArgs();

        _enabled = false;

        if (!flags.contains(CommandFlags.Suppressed))
        {
            switch (type)
            {
                case Goto:
                    _playlist.setCurrentSong(Integer.parseInt(args[0]));
                    break;
                case Kill:
                    // todo implement
                    break;
                case Move:
                    int fromIndex = Integer.parseInt(args[0]);
                    int toIndex = Integer.parseInt(args[1]);
                    _playlist.moveSong(fromIndex, toIndex);
                    break;
                case Next:
                    _playlist.nextSong();
                    break;
                case Prev:
                    _playlist.prevSong();
                    break;
                case Play:
                    boolean doPlay = args[0].equals("T");
                    _player.playToggle(doPlay);
                    break;
                case Remove:
                    _playlist.remove(Integer.parseInt(args[0]));
                    break;
                case Seek:
                    int percentage = args[0].charAt(0);
                    int songPosition = Math.round(
                            (percentage / 100.0f) * (float) _player.getSongLength());
                    _player.setNextSeekPosition(songPosition);
                    break;
            }
        }

        _enabled = true;
    }

    @Override
    public synchronized void gotoSong(int song)
    {
        gotoSong(song, NoFlags);
    }

    @Override
    public synchronized void gotoSong(int song, EnumSet<CommandFlags> flags)
    {
        addCommand(CommandType.Goto, flags, String.format("%d", song));
    }

    public synchronized void kill()
    {
        kill(NoFlags);
    }

    public synchronized void kill(EnumSet<CommandFlags> flags)
    {
        addCommand(CommandType.Kill, flags);
    }

    @Override
    public synchronized void nextSong()
    {
        nextSong(NoFlags);
    }

    @Override
    public synchronized void nextSong(EnumSet<CommandFlags> flags)
    {
        addCommand(CommandType.Next, flags);
    }

    @Override
    public synchronized void prevSong()
    {
        prevSong(NoFlags);
    }

    @Override
    public synchronized void prevSong(EnumSet<CommandFlags> flags)
    {
        addCommand(CommandType.Prev, flags);
    }

    @Override
    public synchronized void moveSong(int from, int to)
    {
        moveSong(from, to, NoFlags);
    }

    @Override
    public synchronized void moveSong(int from, int to, EnumSet<CommandFlags> flags)
    {
        addCommand(CommandType.Move, flags, String.format("%d", from),
                   String.format("%d", to));
    }

    @Override
    public synchronized void removeSong(int song)
    {
        removeSong(song, NoFlags);
    }

    @Override
    public synchronized void removeSong(int song, EnumSet<CommandFlags> flags)
    {
        addCommand(CommandType.Remove, flags, String.format("%d", song));
    }

    @Override
    public synchronized void playToggle(boolean state)
    {
        playToggle(state, NoFlags);
    }

    @Override
    public synchronized void playToggle(boolean state, EnumSet<CommandFlags> flags)
    {
        addCommand(CommandType.Play, flags, state ? "T" : "F");
    }

    @Override
    public synchronized void seek(int percent)
    {
        seek(percent, NoFlags);
    }

    @Override
    public synchronized void seek(int percent, EnumSet<CommandFlags> flags)
    {
        addCommand(CommandType.Seek, flags, String.format("%c", percent));
    }

    /**
     * Blocking method to pop the front of the queue.
     * @return the String at the front of the queue
     * @throws InterruptedException
     */
    @Override
    public CommandPacket take() throws InterruptedException
    {
        return _queue.take();
    }

    /**
     * Add a command to the queue if enabled.
     * Must be called from synchronized methods.
     * @param cmd the command
     * @param flags the optional flags
     */
    private void addCommand(CommandType cmd, EnumSet<CommandFlags> flags)
    {
        addCommand(cmd, flags, new String[] {});
    }

    /**
     * Add a command to the queue if  enabled.
     * Must be called from synchronized methods.
     * @param cmd the command
     * @param args the arguments as a string
     * @param flags the optional flags
     */
    private void addCommand(CommandType cmd, EnumSet<CommandFlags> flags, String... args)
    {
        if (_enabled)
        {
            _queue.add(new CommandPacket(cmd, flags, args));
        }
    }
}
