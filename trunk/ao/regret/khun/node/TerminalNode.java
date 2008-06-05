package ao.regret.khun.node;

import ao.regret.InfoNode;
import ao.simple.kuhn.rules.KuhnBucket;
import ao.simple.kuhn.state.KuhnOutcome;
import ao.util.text.Txt;

/**
 *
 */
public class TerminalNode implements InfoNode
{
    //--------------------------------------------------------------------
    private final KuhnBucket  BUCKET;
    private final KuhnOutcome OUTCOME;


    //--------------------------------------------------------------------
    public TerminalNode(KuhnBucket bucket, KuhnOutcome outcome)
    {
        BUCKET  = bucket;
        OUTCOME = outcome;
    }


    //--------------------------------------------------------------------
    public double expectedValue(TerminalNode vsLastToAct)
    {
        if (OUTCOME.isShowdown())
        {
            double toWin =
                    BUCKET.against( vsLastToAct.BUCKET )
                          .winPercent();

            return OUTCOME == KuhnOutcome.SHOWDOWN
                    ?       toWin
                    : 2.0 * toWin;
        }
        else
        {
            return OUTCOME == KuhnOutcome.FIRST_TO_ACT_WINS
                    ? 1 : -1;
        }
    }


    //--------------------------------------------------------------------
    public String toString()
    {
        return toString( 0 );
    }

    public String toString(int depth)
    {
        return Txt.nTimes("\t", depth) +
               OUTCOME;
//        return Txt.nTimes("\t", depth) +
//                BUCKET + ", " + OUTCOME;
    }
}
