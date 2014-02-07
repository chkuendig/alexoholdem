package ao.holdem.ai.ai.regret.alexo.pair;

import ao.holdem.ai.ai.regret.alexo.AlexoBucket;
import ao.holdem.ai.ai.regret.alexo.JointBucketSequence;
import ao.holdem.ai.ai.regret.alexo.node.BucketNode;
import ao.holdem.ai.ai.regret.alexo.node.PlayerNode;
import ao.holdem.ai.ai.simple.alexo.state.AlexoState;

/**
 *
 */
public class BucketPair implements InfoPair
{
    //--------------------------------------------------------------------
    private final BucketNode first;
    private final BucketNode last;


    //--------------------------------------------------------------------
    public BucketPair(AlexoState  state,
                      AlexoBucket root)
    {
        this(state, root, root);
    }

    public BucketPair(AlexoState  state,
                      AlexoBucket firstBucket,
                      AlexoBucket lastBucket)
    {
        first = new BucketNode(firstBucket.nextBuckets(), state, true);
        last  = new BucketNode(lastBucket .nextBuckets(), state, false);
    }

    public BucketPair(BucketNode firstBucket,
                      BucketNode lastBucket)
    {
        first = firstBucket;
        last  = lastBucket;
//
//        assert first.forFirstToAct();
//        assert !last.forFirstToAct();
    }


    //--------------------------------------------------------------------
    public PlayerPair accordingTo(JointBucketSequence jbs)
    {
        PlayerNode pFirst = first.accordingTo( jbs );
        PlayerNode pLast  = last .accordingTo( jbs );
//        if (pLast == null)
//        {
//            last .accordingTo( jbs );
//        }

        return new PlayerPair(pFirst, pLast);
    }
//    public PlayerNode accordingTo(AlexoBucket bucket,
//                                  boolean     forFirstToAct)
//    {
//        return (forFirstToAct ? first : last)
//                    .accordingTo(bucket);
//    }

    public BucketNode accordingTo(boolean forFirstToAct)
    {
        return (forFirstToAct ? first : last);
    }


    //--------------------------------------------------------------------
//    public double approximate()
//    {
//        return approximate(new JointBucketSequence(), 1.0, 1.0);
//    }
    public double approximate(double aggression)
    {
        return approximate(new JointBucketSequence(),
                           1.0, 1.0, aggression);
    }

//    public double approximate(
//            JointBucketSequence b, double pA, double pB)
//    {
//        return accordingTo( b ).approximate(b, pA, pB);
//    }
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
