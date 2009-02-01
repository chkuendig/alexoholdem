package ao.regret.holdem.node;

import ao.holdem.engine.state.StateTree;
import ao.holdem.model.act.AbstractAction;
import ao.regret.InfoNode;
import ao.regret.holdem.HoldemBucket;
import ao.util.text.Txt;
import org.apache.log4j.Logger;

import java.util.Map;

/**
 * Date: Jan 8, 2009
 * Time: 3:27:58 PM
 */
public class OpponentNode implements PlayerNode
{
    //--------------------------------------------------------------------
    private static final Logger LOG =
            Logger.getLogger(OpponentNode.class);


    //--------------------------------------------------------------------
    private PlayerKids kids;


    //--------------------------------------------------------------------
    public OpponentNode(StateTree.Node state,
                        HoldemBucket   bucket,
                        boolean        forFirstToAct)
    {
//        LOG.debug(state.round());

        kids = new PlayerKids(state, bucket, forFirstToAct, false);
    }


    //--------------------------------------------------------------------
    public InfoNode child(AbstractAction forAction)
    {
        return kids.child( forAction );
    }


    //--------------------------------------------------------------------
    public String toString()
    {
        return toString(0);
    }

    public String toString(int depth)
    {
        StringBuilder str = new StringBuilder();
        for (Map.Entry<AbstractAction, InfoNode>
                kid : kids.entrySet())
        {
            str.append( Txt.nTimes("\t", depth) )
               .append( kid.getKey() )
               .append( "\n" )
               .append( kid.getValue().toString(depth + 1) )
               .append( "\n" );
        }
        return str.substring(0, str.length()-1);
    }
}
