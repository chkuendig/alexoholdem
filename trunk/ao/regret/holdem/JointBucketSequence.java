package ao.regret.holdem;

import ao.bucket.abstraction.access.BucketAgglom;
import ao.holdem.model.Avatar;
import ao.holdem.model.Round;
import ao.holdem.model.card.Community;
import ao.holdem.model.card.Hole;
import ao.holdem.model.card.chance.ChanceCards;
import ao.holdem.model.card.chance.DeckCards;

/**
 * User: iscott
 * Date: Jan 6, 2009
 * Time: 4:48:44 PM
 */
public class JointBucketSequence
{
    //--------------------------------------------------------------------
    private final HoldemBucket[] dealerBuckets;
    private final HoldemBucket[] dealeeBuckets;


    //--------------------------------------------------------------------
    public static JointBucketSequence randomInstance(BucketAgglom agglom)
    {
        ChanceCards cards = new DeckCards();
        return new JointBucketSequence(agglom,
                cards.hole(Avatar.local("dealer")),
                cards.hole(Avatar.local("dealee")),
                cards.community(Round.RIVER));
    }

    public JointBucketSequence(
            BucketAgglom agglom,
            Hole         dealerHole,
            Hole         dealeeHole,
            Community    community)
    {
        dealerBuckets = agglom.computeBuckets(dealerHole, community);
        dealeeBuckets = agglom.computeBuckets(dealeeHole, community);
    }


    //--------------------------------------------------------------------
    public byte bucket(
            boolean dealer,
            Round   round)
    {
        return (dealer ? dealerBuckets
                       : dealeeBuckets)[ round.ordinal() ].index();
    }
}
