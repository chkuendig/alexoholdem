package ao.regret.holdem.node;

import ao.holdem.engine.state.HeadsUpStatus;
import ao.holdem.engine.state.State;
import ao.regret.InfoNode;
import ao.regret.holdem.HoldemBucket;
import ao.util.text.Txt;

/**
 * Date: Jan 8, 2009
 * Time: 5:56:11 PM
 *
 * todo: must this be from POV of dealder?
 */
public class TerminalNode implements InfoNode
{
    //--------------------------------------------------------------------
    private final HoldemBucket  BUCKET;
    private final State         STATE;
    private final HeadsUpStatus STATUS;


    //--------------------------------------------------------------------
    public TerminalNode(HoldemBucket bucket,
                        State        state)
    {
        BUCKET = bucket;
        STATE  = state;
        STATUS = STATE.headsUpStatus();

        assert STATUS != null &&
               STATUS != HeadsUpStatus.IN_PROGRESS;
    }


    //--------------------------------------------------------------------
    public double expectedValue(
            TerminalNode vsLastToAct)
    {
        int    smallBlinds = STATE.pot().smallBlinds();
        double toWin       =
                  (STATUS == HeadsUpStatus.SHOWDOWN)
                ? BUCKET.against( vsLastToAct.BUCKET )
                :   (STATUS == HeadsUpStatus.DEALER_WINS)
                  ?  1 : -1;

        return smallBlinds * toWin;
    }


    //--------------------------------------------------------------------
    public String toString()
    {
        return toString( 0 );
    }

    public String toString(int depth)
    {
        return Txt.nTimes("\t", depth) +
               STATE.headsUpStatus();
    }
}
