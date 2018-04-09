package syncjam.utilities;

import java.util.HashMap;
import java.util.Map;

/**
 * The command type.
 */
public enum CommandType
{
    Goto(0),
    Kill(1),
    Move(2),
    Play(3),
    Next(4),
    Prev(5),
    Remove(6),
    Seek(7),
    Password(8),
    Welcome(9),
    Error(99999);

    private final int _value;
    private static final Map<Integer, CommandType> _valueMap = new HashMap<>();

    static
    {
        for (CommandType type : CommandType.values())
        {
            _valueMap.put(type.getValue(), type);
        }
    }

    CommandType(int value)
    {
        _value = value;
    }

    public int getValue()
    {
        return _value;
    }

    public static CommandType valueOf(int intVal)
    {
        return _valueMap.get(intVal);
    }
}
