package ao.regret.holdem.node;

import ao.holdem.engine.state.State;
import ao.regret.InfoNode;
import ao.regret.holdem.HoldemBucket;
import ao.regret.holdem.JointBucketSequence;
import ao.util.text.Txt;

/**
 * Date: Jan 8, 2009
 * Time: 3:26:46 PM
 */
public class BucketNode implements InfoNode
{
    //--------------------------------------------------------------------
    private PlayerNode[] kids;
    private boolean      dealer;
    private State        state;


    //--------------------------------------------------------------------
    public BucketNode(
            HoldemBucket[] buckets,
            State          atState,
            boolean        forDealer)
    {
        kids   = new PlayerNode[ buckets.length ];
        state  = atState;
        dealer = forDealer;

        for (HoldemBucket bucket : buckets)
        {
            PlayerNode kid =
                    (forDealer ==
                        state.dealerIsNext())
                     ? new ProponentNode(state, bucket, forDealer)
                     : new  OpponentNode(state, bucket, forDealer);

            kids[ bucket.index() ] = kid;
        }
    }


    //--------------------------------------------------------------------
    public boolean forFirstToAct()
    {
        return dealer;
    }

    public PlayerNode accordingTo(JointBucketSequence jbs)
    {
        return accordingTo(
                jbs.bucket(dealer, state.round()));
    }
    public PlayerNode accordingTo(byte bucket)
    {
        return kids[ bucket ];
    }


    //--------------------------------------------------------------------
    public String toString()
    {
        return toString(0);
    }

    public String toString(int depth)
    {
        StringBuilder str = new StringBuilder();
        for (int i = 0; i < kids.length; i++)
        {
            PlayerNode kid = kids[i];
            str.append(Txt.nTimes("\t", depth))
                    .append(i)
                    .append("\n")
                    .append(kid.toString(depth + 1))
                    .append("\n");
        }
        return str.toString();
    }
}
