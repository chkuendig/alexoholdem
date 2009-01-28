package ao.bucket.index.detail.preflop;

import ao.bucket.index.enumeration.CardEnum;
import ao.bucket.index.hole.CanonHole;
import ao.bucket.index.hole.HoleLookup;
import ao.holdem.model.card.Community;
import ao.odds.agglom.Odds;
import ao.odds.agglom.impl.PreciseHeadsUpOdds;
import ao.util.misc.Traverser;
import org.apache.log4j.Logger;

/**
 * Date: Jan 27, 2009
 * Time: 11:02:10 PM
 */
public class HoleOdds
{
    //--------------------------------------------------------------------
    public static void main(String[] args)
    {
        long minWins = Integer.MAX_VALUE;
        long maxWins = Integer.MIN_VALUE;

        long minLose = Integer.MAX_VALUE;
        long maxLose = Integer.MIN_VALUE;

        long minTies = Integer.MAX_VALUE;
        long maxTies = Integer.MIN_VALUE;

        for (int i = 0; i < HoleLookup.CANONS; i++)
        {
            Odds odds = lookup(i);

            minWins = Math.min(minWins, odds.winOdds());
            maxWins = Math.max(maxWins, odds.winOdds());

            minLose = Math.min(minLose, odds.loseOdds());
            maxLose = Math.max(maxLose, odds.loseOdds());

            minTies = Math.min(minTies, odds.splitOdds());
            maxTies = Math.max(maxTies, odds.splitOdds());
        }

        System.out.println("minWins = " + minWins);
        System.out.println("maxWins = " + maxWins);

        System.out.println("minLose = " + minLose);
        System.out.println("maxLose = " + maxLose);

        System.out.println("minTies = " + minTies);
        System.out.println("maxTies = " + maxTies);
    }


    //--------------------------------------------------------------------
    private static final Logger LOG =
            Logger.getLogger(HoleOdds.class);


    //--------------------------------------------------------------------
    private HoleOdds() {}


    //--------------------------------------------------------------------
    static
    {
        LOG.debug("initializing");

        if (! HoleOddsDao.retrieve())
        {
            LOG.debug("starting from scratch");
            HoleOddsDao.init();
        }

        if (! HoleOddsDao.isPreComputed())
        {
            LOG.debug("resuming computation");

            computeOdds();
            HoleOddsDao.persist();
            HoleOddsDao.setPreComputed();
        }
    }


    //--------------------------------------------------------------------
    private static void computeOdds()
    {
        LOG.debug("computing odds");

        final int[]  skinCount      = {0};
        final int[]  calcCount      = {0};
        final long[] milestoneStart = {0};

        CardEnum.traverseUniqueHoles(
                new Traverser<CanonHole>() {
            public void traverse(CanonHole hole) {
                if (HoleOddsDao.isSet( hole.canonIndex() )) {
                    skinCount[0]++;
                    return;
                } else if (skinCount[0] != 0) {
                    System.out.println("skipped " + skinCount[0]);
                    skinCount[0] = 0;
                }

                Odds odds = PreciseHeadsUpOdds.INSTANCE.compute(
                                hole.reify(), Community.PREFLOP);

                HoleOddsDao.set(hole.canonIndex(), odds);

                checkpoint( calcCount[0]++, milestoneStart );
            }});
    }

    private static void checkpoint(
            int count, long[] milestoneStart)
    {
        if (milestoneStart[0] == 0)
            milestoneStart[0] = System.currentTimeMillis();
        System.out.print(".");

        boolean milesoneReached = ((count + 1) % 13 == 0);
        if (milesoneReached) {
            System.out.println(
                    " " + (count + 1) + ", completed 13 in " +
                    (System.currentTimeMillis() - milestoneStart[0]));
            HoleOddsDao.persist();
        }

        if (milesoneReached)
            milestoneStart[0] = System.currentTimeMillis();
    }


    //--------------------------------------------------------------------
    public static Odds lookup(int canonHoleIndex)
    {
        return HoleOddsDao.get(canonHoleIndex);
    }
}
