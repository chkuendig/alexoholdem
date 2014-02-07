package ao.holdem.net.msg;

import ao.holdem.model.act.AbstractAction;
import ao.holdem.model.card.Community;
import ao.holdem.model.card.Hole;

/**
 * User: alex
 * Date: 6-May-2009
 * Time: 3:09:43 PM
 */
public class Inbound
{
    //--------------------------------------------------------------------
    public static Inbound parse(String fromMessageString)
    {
        return null;
    }


    //--------------------------------------------------------------------
    public Inbound(int            instance,
                   String         fromAddress,
                   boolean        isStartOfHand,
                   AbstractAction opponentAction,
                   boolean        proponentDealer,
                   Hole           hole,
                   Community      community)
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
