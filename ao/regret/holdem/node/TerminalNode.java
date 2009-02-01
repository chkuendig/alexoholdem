package ao.regret.holdem.node;

import ao.holdem.engine.state.HeadsUpStatus;
import ao.holdem.engine.state.StateTree;
import ao.regret.InfoNode;
import ao.regret.holdem.HoldemBucket;
import ao.util.text.Txt;
import org.apache.log4j.Logger;

/**
 * Date: Jan 8, 2009
 * Time: 5:56:11 PM
 */
public class TerminalNode implements InfoNode
{
    //--------------------------------------------------------------------
    private static final Logger LOG =
            Logger.getLogger(TerminalNode.class);

    private static int count = 0;


    //--------------------------------------------------------------------
    private final HoldemBucket  BUCKET;
    private final int           POT;
    private final HeadsUpStatus STATUS;
    private final boolean       DEALER_ACTED_LAST;


    //--------------------------------------------------------------------
    public TerminalNode(HoldemBucket   bucket,
                        boolean        dealerActedLast,
                        StateTree.Node state)
    {
//        LOG.debug(state.round());
        if ( count      %     1000  == 0) System.out.print(".");
        if ((count + 1) % (50*1000) == 0) System.out.println();

        BUCKET            = bucket;
        POT               = state.state().pot().smallBlinds();
        STATUS            = state.state().headsUpStatus();
        DEALER_ACTED_LAST = dealerActedLast;

        assert STATUS != null &&
               STATUS != HeadsUpStatus.IN_PROGRESS;
    }


    //--------------------------------------------------------------------
    public double expectedValue(TerminalNode vs)
    {
        double toWin =
                  (STATUS == HeadsUpStatus.SHOWDOWN)
                ?  BUCKET.against( vs.BUCKET )
                :   (STATUS == HeadsUpStatus.DEALER_WINS &&
                       !DEALER_ACTED_LAST)
                  ?  1 : -1;
        return POT * toWin;
    }


    //--------------------------------------------------------------------
    public String toString()
    {
        return toString( 0 );
    }

    public String toString(int depth)
    {
        return Txt.nTimes("\t", depth) +
               STATUS;
    }
}
