package ao.holdem.bots.util;

import ao.holdem.def.model.card.Card;
import ao.holdem.def.model.card.eval7.Eval7Faster;
import ao.holdem.def.model.cards.Community;
import ao.holdem.def.model.cards.Hole;
import ao.util.rand.MersenneTwisterFast;
import ao.util.stats.Combiner;
import ao.util.stats.FastIntCombiner;

/**
 * Threadsafe!!
 */
public class ApproximateOddFinder implements OddFinder
{
    //--------------------------------------------------------------------
    private static final int  DEFAULT_FLOP_ITR = 500;
    private static final long DEFAULT_HOLE_ITR = 200;

    private final int  FLOP_ITR;
    private final long HOLE_ITR;

    private final double FLOP_FRACT;
    private final double HOLE_FRACT;

    private final boolean IS_FLOP_FRACT;
    private final boolean IS_HOLE_FRACT;


    //--------------------------------------------------------------------
    public ApproximateOddFinder()
    {
        this(DEFAULT_FLOP_ITR, DEFAULT_HOLE_ITR);
    }

    public ApproximateOddFinder(
            double flops,
            long   holes)
    {
        FLOP_FRACT = flops;
        HOLE_ITR   = holes;

        IS_FLOP_FRACT = true;
        IS_HOLE_FRACT = true;

        FLOP_ITR   = -1;
        HOLE_FRACT = Double.NaN;
    }

    public ApproximateOddFinder(
            int    flops,
            double holes)
    {
        FLOP_ITR   = flops;
        HOLE_FRACT = holes;

        IS_FLOP_FRACT = true;
        IS_HOLE_FRACT = true;

        FLOP_FRACT = Double.NaN;
        HOLE_ITR   = -1;
    }

    public ApproximateOddFinder(
            double flops,
            double holes)
    {
        FLOP_FRACT = flops;
        HOLE_FRACT = holes;

        IS_FLOP_FRACT = true;
        IS_HOLE_FRACT = true;

        FLOP_ITR = -1;
        HOLE_ITR = -1;
    }

    public ApproximateOddFinder(
            int  flops,
            long holes)
    {
        FLOP_ITR = flops;
        HOLE_ITR = holes;

        IS_FLOP_FRACT = false;
        IS_HOLE_FRACT = false;

        FLOP_FRACT = Double.NaN;
        HOLE_FRACT = Double.NaN;
    }


    //--------------------------------------------------------------------
    private int communityItr(Community community, double fraction)
    {
        // community
        long communities =
                Combiner.choose(52 - 2 - community.knownCount(),
                                community.unknownCount());
        return (int)(communities * fraction);
    }

    private long holeItr(int activeOpponents, double fraction)
    {
        // opponent cards
        long oppHoles =
                Combiner.choose(52 - 2 - 5,
                                activeOpponents * 2);

        // how many unique ways to group them by two
        long holeGroups =
                Combiner.choose(activeOpponents * 2, 2);
        // unique ways to assign two hole cards
        long holeDistributions =
                Combiner.choose((int)holeGroups, activeOpponents);

        long oppHoleChoices = oppHoles * holeDistributions;
        return (long)(oppHoleChoices * fraction);
    }


    //--------------------------------------------------------------------
    public Odds compute(
            Hole      hole,
            Community community,
            int       activeOpponents)
    {
        int  flops = (IS_FLOP_FRACT)
                        ? communityItr(community, FLOP_FRACT)
                        : FLOP_ITR;
        long holes = (IS_HOLE_FRACT)
                        ? holeItr(activeOpponents, HOLE_FRACT)
                        : HOLE_ITR;

        int  indexes[] = GeneralOddFinder.cardIndexes();
        Card cards[]   = Card.values();

        GeneralOddFinder.moveKnownCardsToEnd(
                indexes, hole, community);

        MersenneTwisterFast rand = new MersenneTwisterFast();

        switch (community.knownCount())
        {
            case 0:
                return computePreflop(
                        flops, holes, activeOpponents, indexes, cards, rand);
            case 3:
                return computeFlop(
                        flops, holes, activeOpponents, indexes, cards, rand);
            case 4:
                return computeTurn(
                        holes, activeOpponents, indexes, cards, rand);
            case 5:
                return computeRiver(
                        holes, activeOpponents, indexes, cards, rand);
        }
        return new Odds();
    }

    private Odds computePreflop(
            int flops, long holes, int activeOpponents,
            int indexes[], Card cards[], MersenneTwisterFast rand)
    {
//                int unknownCount = 52 - 2;
//                FastIntCombiner fc =
//                    new FastIntCombiner(indexes, unknownCount);
//
//                CommunityVisitor5 c5 =
//                        new CommunityVisitor5(
//                                activeOpponents, indexes, cards, rand);
//                fc.combine(c5);
//                return c5.odds();

        Odds odds = new Odds();
        for (int i = 0; i < flops; i++)
        {
            int xComA = rand.nextInt( GeneralOddFinder.COM_A + 1 );
            GeneralOddFinder.swap(indexes, xComA, GeneralOddFinder.COM_A);
            int xComB = rand.nextInt( GeneralOddFinder.COM_B + 1 );
            GeneralOddFinder.swap(indexes, xComB, GeneralOddFinder.COM_B);
            int xComC = rand.nextInt( GeneralOddFinder.COM_C + 1 );
            GeneralOddFinder.swap(indexes, xComC, GeneralOddFinder.COM_C);
            int xComD = rand.nextInt( GeneralOddFinder.COM_D + 1 );
            GeneralOddFinder.swap(indexes, xComD, GeneralOddFinder.COM_D);
            int xComE = rand.nextInt( GeneralOddFinder.COM_E + 1 );
            GeneralOddFinder.swap(indexes, xComE, GeneralOddFinder.COM_E);

            odds = odds.plus(computeOppOdds(
                        activeOpponents, indexes, cards, holes, rand));

            GeneralOddFinder.swap(indexes, xComE, GeneralOddFinder.COM_E);
            GeneralOddFinder.swap(indexes, xComD, GeneralOddFinder.COM_D);
            GeneralOddFinder.swap(indexes, xComC, GeneralOddFinder.COM_C);
            GeneralOddFinder.swap(indexes, xComB, GeneralOddFinder.COM_B);
            GeneralOddFinder.swap(indexes, xComA, GeneralOddFinder.COM_A);
        }
        return odds;
    }

    private Odds computeFlop(
            int flops, long holes, int activeOpponents,
            int indexes[], Card cards[], MersenneTwisterFast rand)
    {
        Odds odds = new Odds();
        if (flops >= 1081)
        {
            int unknownCount = 52 - 2 - 3;
            FastIntCombiner fc =
                new FastIntCombiner(indexes, unknownCount);

            TurnCommunityVisitor turn =
                    new TurnCommunityVisitor(
                            activeOpponents, indexes, cards, holes, rand);
            fc.combine(turn);
            odds = turn.odds();
        }
        else
        {
            for (int i = 0; i < flops; i++)
            {
                int xComD = rand.nextInt( GeneralOddFinder.COM_D + 1 );
                GeneralOddFinder.swap(indexes, xComD, GeneralOddFinder.COM_D);
                int xComE = rand.nextInt( GeneralOddFinder.COM_E + 1 );
                GeneralOddFinder.swap(indexes, xComE, GeneralOddFinder.COM_E);

                odds = odds.plus(computeOppOdds(
                            activeOpponents, indexes, cards, holes, rand));

                GeneralOddFinder.swap(indexes, xComE, GeneralOddFinder.COM_E);
                GeneralOddFinder.swap(indexes, xComD, GeneralOddFinder.COM_D);
            }
        }
        return odds;
    }

    private Odds computeTurn(
            long holes, int activeOpponents,
            int indexes[], Card cards[], MersenneTwisterFast rand)
    {
//        for (int i = 0; i < flops; i++)
//        {
//            int xComE = rand.nextInt( GeneralOddFinder.COM_E + 1 );
//            GeneralOddFinder.swap(indexes, xComE, GeneralOddFinder.COM_E);
//
//            odds = odds.plus(computeOppOdds(
//                        activeOpponents, indexes, cards, rand));
//
//            GeneralOddFinder.swap(indexes, xComE, GeneralOddFinder.COM_E);
//        }

        int unknownCount = 52 - 2 - 4;
        FastIntCombiner fc =
            new FastIntCombiner(indexes, unknownCount);

        RiverCommunityVisitor river =
                new RiverCommunityVisitor(
                        activeOpponents, indexes, cards, holes, rand);
        fc.combine(river);
        return river.odds();
    }

    private Odds computeRiver(
            long holes, int activeOpponents,
            int indexes[], Card cards[], MersenneTwisterFast rand)
    {
        return computeOppOdds(
                    activeOpponents, indexes, cards, holes, rand);
    }


    //--------------------------------------------------------------------
    private static class TurnCommunityVisitor
            implements FastIntCombiner.CombinationVisitor2
    {
        private Card cards[];
        private int  indexes[];
        private int  activeOpponents;
        private Odds odds = new Odds();
        private long holes;
        private MersenneTwisterFast rand;

        public TurnCommunityVisitor(
                int  activeOpps,
                int  indexes[],
                Card cards[],
                long holes,
                MersenneTwisterFast rand)
        {
            activeOpponents = activeOpps;
            this.indexes    = indexes;
            this.cards      = cards;
            this.rand       = rand;
            this.holes      = holes;
        }
        public void visit(int d, int e)
        {
            GeneralOddFinder.swap(indexes, d, GeneralOddFinder.COM_D);
            GeneralOddFinder.swap(indexes, e, GeneralOddFinder.COM_E);

            odds = odds.plus(
                    computeOppOdds(
                                activeOpponents, indexes, cards, holes, rand));

            GeneralOddFinder.swap(indexes, e, GeneralOddFinder.COM_E);
            GeneralOddFinder.swap(indexes, d, GeneralOddFinder.COM_D);
        }
        public Odds odds() {  return odds;  }
    }

    private static class RiverCommunityVisitor
            implements FastIntCombiner.CombinationVisitor1
    {
        private Card cards[];
        private int  indexes[];
        private int  activeOpponents;
        private Odds odds = new Odds();
        private long holes;
        private MersenneTwisterFast rand;

        public RiverCommunityVisitor(
                int  activeOpps,
                int  indexes[],
                Card cards[],
                long holes,
                MersenneTwisterFast rand)
        {
            activeOpponents = activeOpps;
            this.indexes    = indexes;
            this.cards      = cards;
            this.rand       = rand;
            this.holes      = holes;
        }
        public void visit(int e)
        {
            GeneralOddFinder.swap(indexes, e, GeneralOddFinder.COM_E);

            odds = odds.plus(
                    computeOppOdds(
                                activeOpponents, indexes, cards, holes, rand));

            GeneralOddFinder.swap(indexes, e, GeneralOddFinder.COM_E);
        }
        public Odds odds() {  return odds;  }
    }



    //--------------------------------------------------------------------
    private static Odds computeOppOdds(
            int  activeOpps,
            int  indexes[],
            Card cards[],
            long holes,
            MersenneTwisterFast rand)
    {
        Card comA = cards[ indexes[GeneralOddFinder.COM_A] ];
        Card comB = cards[ indexes[GeneralOddFinder.COM_B] ];
        Card comC = cards[ indexes[GeneralOddFinder.COM_C] ];
        Card comD = cards[ indexes[GeneralOddFinder.COM_D] ];
        Card comE = cards[ indexes[GeneralOddFinder.COM_E] ];

        int wins   = 0;
        int losses = 0;
        int splits = 0;

        int shortcut =
                Eval7Faster.shortcutFor(
                        comA, comB, comC, comD, comE);

        short myVal =
                Eval7Faster.valueOf(shortcut,
                        cards[ indexes[GeneralOddFinder.HOLE_A] ],
                        cards[ indexes[GeneralOddFinder.HOLE_B] ]);

        for (int i = 0; i < holes; i++)
        {
            short oppVal =
                    approxMaxOppVal(0, activeOpps, indexes, cards,
                                    shortcut, rand);

            if      (myVal > oppVal) {  wins++;    }
            else if (myVal < oppVal) {  losses++;  }
            else                     {  splits++;  }
        }

        return new Odds(wins, losses, splits);
    }

    private static short approxMaxOppVal(
            int  atOpp,
            int  activeOpps,
            int  indexes[],
            Card cards[],
            int communityShortcut,
            MersenneTwisterFast rand)
    {
        if (atOpp >= activeOpps)
        {
            return Short.MIN_VALUE;
        }

        int holeDestA = GeneralOddFinder.OPPS - atOpp*2;
        int holeDestB = holeDestA - 1;

        int xOppA = rand.nextInt( holeDestA + 1 );
        int xOppB = rand.nextInt( holeDestB + 1 );

        GeneralOddFinder.swap(indexes, xOppA, holeDestA);
        GeneralOddFinder.swap(indexes, xOppB, holeDestB);

        Card oppA = cards[ indexes[holeDestA] ];
        Card oppB = cards[ indexes[holeDestB] ];

        short oppVal = Eval7Faster.valueOf(
                communityShortcut, oppA, oppB);

        short maxOtherOppVal =
                approxMaxOppVal(atOpp + 1, activeOpps, indexes, cards,
                                communityShortcut, rand);

        GeneralOddFinder.swap(indexes, xOppB, holeDestB);
        GeneralOddFinder.swap(indexes, xOppA, holeDestA);

        return (short) Math.max(oppVal, maxOtherOppVal);
    }
}
