package syncjam.utilities;

import syncjam.ui.Colors;

import java.awt.*;

/**
 * Created by Marty on 1/22/2017.
 */
public class UserInfo implements Comparable<UserInfo>
{
    //TODO: Should this implement an interface?
    public final String _userName;
    public final String _userAddress;
    //TODO: userLevel should be enum
    public final int _userLevel;

    public UserInfo(String name, String address, int l)
    {
        _userName = name;
        _userAddress = address;
        _userLevel = l;
    }
    public Color getUserLevelColor()
    {
        Color c;
        switch (_userLevel)
        {
            case 1:
                c = Colors.get(Colors.Highlight); break;
            case 2:
                c = Colors.get(Colors.Highlight2); break;
            default:
                c = Colors.get(Colors.Foreground1);
        }
        return c;
    }
    @Override
    public int compareTo(UserInfo o) {
        if(_userLevel > o._userLevel) //TODO: change when userLevel is enum
            return -1;
        else if(_userLevel == o._userLevel)
            return _userName.compareTo(o._userName);
        return 1;
    }
}