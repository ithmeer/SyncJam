package syncjam.utilities;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

/**
 * Additional data associated with a command.
 */
public enum CommandFlags
{
    /**
     * No additional flags
     */
    None(0),

    /**
     * The command action should be suppressed on our end
     */
    Suppressed(1);

    private final int _value;
    private static final Map<Integer, CommandFlags> _valueMap = new HashMap<>();

    static
    {
        for (CommandFlags flag : CommandFlags.values())
        {
            _valueMap.put(flag.getValue(), flag);
        }
    }

    CommandFlags(int value)
    {
        _value = value;
    }

    public int getValue()
    {
        return _value;
    }

    public static EnumSet<CommandFlags> getFlagSet(int bitSet)
    {
        EnumSet<CommandFlags> set = EnumSet.noneOf(CommandFlags.class);
        int mask = 0x1;

        while (mask != 0)
        {
            if ((mask & bitSet) == 0x1)
            {
                set.add(CommandFlags.valueOf(mask));
            }
            mask <<= 1;
        }

        return set;
    }

    public static CommandFlags valueOf(int intVal)
    {
        return _valueMap.get(intVal);
    }
}
