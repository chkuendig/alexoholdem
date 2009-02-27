package ao.regret.holdem.node;

/**
 * Date: Jan 8, 2009
 * Time: 3:26:46 PM
 */
public class BucketNode //implements InfoNode
{
//    //--------------------------------------------------------------------
//    private static final Logger LOG =
//            Logger.getLogger(BucketNode.class);
//
//
//    //--------------------------------------------------------------------
//    private PlayerNode[] kids;
//    private boolean      dealer;
//    private Round        round;
//
//
//    //--------------------------------------------------------------------
//    public BucketNode(
//            HoldemBucket   parentBucket,
//            StateTree.Node state,
//            boolean        forDealer)
//    {
////        LOG.debug(parentBucket.subBucketCount() +
////                  " at " + state.state().round());
//
//        kids   = new PlayerNode[ parentBucket.subBucketCount() ];
//        round  = state.state().round();
//        dealer = forDealer;
//
//        for (byte b = 0; b < parentBucket.subBucketCount(); b++)
//        {
//            HoldemBucket bucket = parentBucket.nextBucket(b);
//            PlayerNode kid =
//                    (forDealer ==
//                        state.state().dealerIsNext())
//                     ? new ProponentNode(state, bucket, forDealer)
//                     : new  OpponentNode(state, bucket, forDealer);
//
//            kids[ bucket.index() ] = kid;
//        }
//    }
//
//
//    //--------------------------------------------------------------------
//    public boolean forFirstToAct()
//    {
//        return dealer;
//    }
//
//    public PlayerNode accordingTo(JointBucketSequence jbs)
//    {
//        return accordingTo(
//                jbs.bucket(dealer, round));
//    }
//    public PlayerNode accordingTo(byte bucket)
//    {
//        return kids[ bucket ];
//    }
//
//
//    //--------------------------------------------------------------------
//    public String toString()
//    {
//        return toString(0);
//    }
//
//    public String toString(int depth)
//    {
//        StringBuilder str = new StringBuilder();
//        for (int i = 0; i < kids.length; i++)
//        {
//            PlayerNode kid = kids[i];
//            str.append(Txt.nTimes("\t", depth))
//                    .append(i)
//                    .append("\n")
//                    .append(kid.toString(depth + 1))
//                    .append("\n");
//        }
//        return str.toString();
//    }
}
