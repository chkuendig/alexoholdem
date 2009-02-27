package ao.regret.holdem;

/**
 * User: iscott
 * Date: Jan 6, 2009
 * Time: 4:48:44 PM
 */
public class JointBucketSequence
{
//    //--------------------------------------------------------------------
//    private final byte[] dealerBuckets;
//    private final byte[] dealeeBuckets;
//
//
//    //--------------------------------------------------------------------
//    public static JointBucketSequence randomInstance(BucketAgglom agglom)
//    {
//        ChanceCards cards = new DeckCards();
//        return new JointBucketSequence(agglom,
//                cards.hole(Avatar.local("dealer")),
//                cards.hole(Avatar.local("dealee")),
//                cards.community(Round.RIVER));
//    }
//
//    public JointBucketSequence(
//            BucketAgglom agglom,
//            Hole         dealerHole,
//            Hole         dealeeHole,
//            Community    community)
//    {
//        dealerBuckets = agglom.computeBuckets(dealerHole, community);
//        dealeeBuckets = agglom.computeBuckets(dealeeHole, community);
//    }
//
//
//    //--------------------------------------------------------------------
//    public byte bucket(
//            boolean dealer,
//            Round   round)
//    {
//        return (dealer ? dealerBuckets
//                       : dealeeBuckets)[ round.ordinal() ];
//    }
}
