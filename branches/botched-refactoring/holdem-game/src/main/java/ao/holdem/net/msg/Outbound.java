package ao.holdem.net.msg;

import ao.holdem.model.act.AbstractAction;

/**
 * User: alex
 * Date: 6-May-2009
 * Time: 3:10:16 PM
 */
public class Outbound
{
    //--------------------------------------------------------------------
    public static Outbound parse(String fromMessageString)
    {
        return null;
    }


    //--------------------------------------------------------------------
    public Outbound(int instance, AbstractAction act)
    {

    }


    //--------------------------------------------------------------------
    public String toMessageString()
    {
        return "";
    }

    @Override public String toString() {
        return toMessageString();
    }
}
