package ao.regret.holdem.pair;

import ao.holdem.engine.state.StateTree;
import ao.regret.holdem.HoldemBucket;
import ao.regret.holdem.JointBucketSequence;
import ao.regret.holdem.node.BucketNode;
import ao.regret.holdem.node.PlayerNode;

/**
 * Date: Jan 18, 2009
 * Time: 7:07:31 PM
 */
public class BucketPair implements InfoPair
{
    //--------------------------------------------------------------------
    private final BucketNode   first;
    private final BucketNode   last;


    //--------------------------------------------------------------------
    public BucketPair(StateTree.Node state,
                      HoldemBucket   root)
    {
        this(state, root, root);
    }

    public BucketPair(StateTree.Node state,
                      HoldemBucket   dealerBucket,
                      HoldemBucket   dealeeBucket)
    {
        this(new BucketNode(dealerBucket, state, true),
             new BucketNode(dealeeBucket, state, false));
    }

    public BucketPair(BucketNode firstBucket,
                      BucketNode lastBucket)
    {
        first   = firstBucket;
        last    = lastBucket;
    }


    //--------------------------------------------------------------------
    public PlayerPair accordingTo(JointBucketSequence jbs)
    {
        PlayerNode pFirst = first.accordingTo( jbs );
        PlayerNode pLast  = last .accordingTo( jbs );

        return new PlayerPair(pFirst, pLast);
    }

    public BucketNode accordingTo(boolean forFirstToAct)
    {
        return (forFirstToAct ? first : last);
    }


    //--------------------------------------------------------------------
    public double approximate(JointBucketSequence b)
    {
        return approximate(b, 0.0);
    }
    public double approximate(JointBucketSequence b, double aggression)
    {
        return approximate(b, 1.0, 1.0, aggression);
    }

    public double approximate(
            JointBucketSequence b,
            double pA, double pB, double aggression)
    {
        return accordingTo( b ).approximate(b, pA, pB, aggression);
    }


    //--------------------------------------------------------------------
    public String toString()
    {
        return "FIRST:\n" + first.toString() + "\n\n" +
               "LAST:\n"  + last.toString();
    }
}
