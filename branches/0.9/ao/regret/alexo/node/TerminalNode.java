package ao.regret.alexo.node;

import ao.regret.InfoNode;
import ao.regret.alexo.AlexoBucket;
import ao.simple.alexo.state.AlexoState;
import ao.util.text.Txt;

/**
 *
 */
public class TerminalNode implements InfoNode
{
    //--------------------------------------------------------------------
    private final AlexoBucket BUCKET;
    private final AlexoState  STATE;


    //--------------------------------------------------------------------
    public TerminalNode(AlexoBucket bucket,
                        AlexoState  state)
    {
        BUCKET = bucket;
        STATE  = state;
    }


    //--------------------------------------------------------------------
    public double expectedValue(
            TerminalNode vsLastToAct)
    {
        return STATE.deltas(
                BUCKET.sequence( vsLastToAct.BUCKET ));
    }


    //--------------------------------------------------------------------
    public String toString()
    {
        return toString( 0 );
    }

    public String toString(int depth)
    {
        return Txt.nTimes("\t", depth) +
               STATE.outcome();
    }
}
