package syncjam.net;

import syncjam.utilities.CommandFlags;
import syncjam.utilities.CommandType;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.stream.Collectors;

/**
 * A SyncJam command (skip, seek, etc).
 */
public final class CommandPacket implements Externalizable
{
    private CommandType _type;
    private EnumSet<CommandFlags> _flags;
    private String[] _args;

    private final String _delimiter = "\0";

    public CommandPacket(CommandType type, EnumSet<CommandFlags> flags, String... args)
    {
        _type = type;
        _flags = flags.clone();
        _args = args;
    }

    public CommandType getType()
    {
        return _type;
    }

    public EnumSet<CommandFlags> getFlags()
    {
        return _flags;
    }

    public String[] getArgs()
    {
        return _args;
    }

    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder();
        builder.append(_type.name()).append("(");
        builder.append(Arrays.stream(_args).collect(Collectors.joining(", ")));
        builder.append(") [");
        builder.append(_flags.stream().map(Enum::name).collect(Collectors.joining(", ")));
        builder.append("]");
        return builder.toString();
    }

    /**
     * Given an enum set of flags, return the character representation of the flags
     * @param flags the enum set
     * @return the char representation
     */
    private int getEnumSetValue(EnumSet<CommandFlags> flags)
    {
        int value = 0x0;
        for (CommandFlags flag : flags)
        {
            value |= flag.getValue();
        }
        return value;
    }

    /**
     * The object implements the writeExternal method to save its contents
     * by calling the methods of DataOutput for its primitive values or
     * calling the writeObject method of ObjectOutput for objects, strings,
     * and arrays.
     *
     * @param out the stream to write the object to
     * @throws IOException Includes any I/O exceptions that may occur
     * @serialData Overriding methods should use this tag to describe
     * the data layout of this Externalizable object.
     * List the sequence of element types and, if possible,
     * relate the element to a public/protected field and/or
     * method of this Externalizable class.
     */
    @Override
    public void writeExternal(ObjectOutput out) throws IOException
    {
        out.writeInt(_type.getValue());
        out.writeInt(getEnumSetValue(_flags));
        out.writeObject(Arrays.stream(_args).collect(Collectors.joining(_delimiter)));
    }

    /**
     * The object implements the readExternal method to restore its
     * contents by calling the methods of DataInput for primitive
     * types and readObject for objects, strings and arrays.  The
     * readExternal method must read the values in the same sequence
     * and with the same types as were written by writeExternal.
     *
     * @param in the stream to read data from in order to restore the object
     * @throws IOException            if I/O errors occur
     * @throws ClassNotFoundException If the class for an object being
     *                                restored cannot be found.
     */
    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException
    {
        int type = in.readInt();
        int flagVal = in.readInt();
        String args = (String) in.readObject();

        _type = CommandType.valueOf(type);
        _flags = CommandFlags.getFlagSet(flagVal).clone();
        _args = args.split("\0");
    }
}
