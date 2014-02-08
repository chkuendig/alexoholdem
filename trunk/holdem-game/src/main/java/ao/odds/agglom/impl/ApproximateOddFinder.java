package ao.odds.agglom.impl;

import ao.holdem.model.card.Card;
import ao.holdem.model.card.Community;
import ao.holdem.model.card.Hole;
import ao.odds.agglom.OddFinder;
import ao.odds.agglom.Odds;
import ao.odds.eval.eval7.Eval7Faster;
import ao.util.math.rand.MersenneTwisterFast;
import ao.util.math.stats.FastIntCombiner;

import static ao.odds.agglom.impl.PreciseHeadsUpOdds.initKnownCardsToEnd;
import static ao.util.data.Arrs.swap;

/**
 * Threadsafe!!
 */
public class ApproximateOddFinder implements OddFinder
{
    //--------------------------------------------------------------------
    private static final int HOLE_A = 52 -      1,
                             HOLE_B = 52 - (1 + 1),
                             COM_A  = 52 - (2 + 1),
                             COM_B  = 52 - (2 + 2),
                             COM_C  = 52 - (2 + 3),
                             COM_D  = 52 - (2 + 4),
                             COM_E  = 52 - (2 + 5),
                             OPP_A  = 52 - (2 + 5 + 1);

    private static final int  DEFAULT_FLOP_ITR = 1000; // 300
    private static final long DEFAULT_HOLE_ITR =  600; // 200

    private final int  FLOP_ITR;
    private final long HOLE_ITR;


    //--------------------------------------------------------------------
    public ApproximateOddFinder()
    {
        this(DEFAULT_FLOP_ITR, DEFAULT_HOLE_ITR);
//        this(-1, -1);
    }

    public ApproximateOddFinder(
            int  flops,
            long holes)
    {
        FLOP_ITR = flops;
        HOLE_ITR = holes;
    }


    //--------------------------------------------------------------------
    public Odds compute(
            Hole      hole,
            Community community,
            int       activeOpponents)
    {
        int  flops = FLOP_ITR;
        long holes = HOLE_ITR;

        Card cards[] = initKnownCardsToEnd(hole, community);

        MersenneTwisterFast rand = new MersenneTwisterFast();
        switch (community.knownCount())
        {
            case 0:
                return computePreflop(
                        flops, holes, activeOpponents, cards, rand);
//                return computePreflop(
//                        1000, 1, activeOpponents, cards, rand);
            case 3:
                return computeFlop(
                        flops, holes, activeOpponents, cards, rand);
//                return computeFlop(
//                        100, 50, activeOpponents, cards, rand);
            case 4:
                return computeTurn(
                        holes, activeOpponents, cards, rand);
//                return computeTurn(
//                        100, activeOpponents, cards, rand);
            case 5:
//                return computeRiver(
//                        holes, activeOpponents, cards, rand);
                return computeRiver(
                        2000, activeOpponents, cards, rand);
        }
        return new Odds();
    }

    private Odds computePreflop(
            int flops, long holes, int activeOpponents,
            Card cards[], MersenneTwisterFast rand)
    {
//                int unknownCount = 52 - 2;
//                FastIntCombiner fc =
//                    new FastIntCombiner(INDEXES, unknownCount);
//
//                CommunityVisitor5 c5 =
//                        new CommunityVisitor5(
//                                activeOpponents, cards, rand);
//                fc.combine(c5);
//                return c5.odds();

        Odds odds = new Odds();
        for (int i = 0; i < flops; i++)
        {
            int xComA = rand.nextInt( COM_A + 1 );
            swap(cards, xComA, COM_A);
            int xComB = rand.nextInt( COM_B + 1 );
            swap(cards, xComB, COM_B);
            int xComC = rand.nextInt( COM_C + 1 );
            swap(cards, xComC, COM_C);
            int xComD = rand.nextInt( COM_D + 1 );
            swap(cards, xComD, COM_D);
            int xComE = rand.nextInt( COM_E + 1 );
            swap(cards, xComE, COM_E);

            odds = odds.plus(computeOppOdds(
                        activeOpponents, cards, holes, rand));

            swap(cards, xComE, COM_E);
            swap(cards, xComD, COM_D);
            swap(cards, xComC, COM_C);
            swap(cards, xComB, COM_B);
            swap(cards, xComA, COM_A);
        }
        return odds;
    }

    private Odds computeFlop(
            int flops, long holes, int activeOpponents,
            Card cards[], MersenneTwisterFast rand)
    {
        Odds odds = new Odds();
        if (flops >= 1081)
        {
            int unknownCount = 52 - 2 - 3;
            FastIntCombiner fc =
                new FastIntCombiner(Card.INDEXES, unknownCount);

            TurnCommunityVisitor turn =
                    new TurnCommunityVisitor(
                            activeOpponents, cards, holes, rand);
            fc.combine(turn);
            odds = turn.odds();
        }
        else
        {
            for (int i = 0; i < flops; i++)
            {
                int xComD = rand.nextInt( COM_D + 1 );
                swap(cards, xComD, COM_D);
                int xComE = rand.nextInt( COM_E + 1 );
                swap(cards, xComE, COM_E);

                odds = odds.plus(computeOppOdds(
                            activeOpponents, cards, holes, rand));

                swap(cards, xComE, COM_E);
                swap(cards, xComD, COM_D);
            }
        }
        return odds;
    }

    private Odds computeTurn(
            long holes, int activeOpponents,
            Card cards[], MersenneTwisterFast rand)
    {
//        for (int i = 0; i < flops; i++)
//        {
//            int xComE = rand.nextInt( PreciseHeadsUpOdds.COM_E + 1 );
//            swap(cards, xComE, PreciseHeadsUpOdds.COM_E);
//
//            odds = odds.plus(computeOppOdds(
//                        activeOpponents, cards, rand));
//
//            swap(cards, xComE, PreciseHeadsUpOdds.COM_E);
//        }

        int unknownCount = 52 - 2 - 4;
        FastIntCombiner fc =
            new FastIntCombiner(Card.INDEXES, unknownCount);

        RiverCommunityVisitor river =
                new RiverCommunityVisitor(
                        activeOpponents, cards, holes, rand);
        fc.combine(river);
        return river.odds();
    }

    private Odds computeRiver(
            long holes, int activeOpponents,
            Card cards[], MersenneTwisterFast rand)
    {
        return computeOppOdds(
                    activeOpponents, cards, holes, rand);
    }


    //--------------------------------------------------------------------
    private static class TurnCommunityVisitor
            implements FastIntCombiner.CombinationVisitor2
    {
        private Card cards[];
        private int  activeOpponents;
        private Odds odds = new Odds();
        private long holes;
        private MersenneTwisterFast rand;

        public TurnCommunityVisitor(
                int  activeOpps,
                Card cards[],
                long holes,
                MersenneTwisterFast rand)
        {
            activeOpponents = activeOpps;
            this.cards      = cards;
            this.rand       = rand;
            this.holes      = holes;
        }
        public void visit(int d, int e)
        {
            swap(cards, d, COM_D);
            swap(cards, e, COM_E);

            odds = odds.plus(
                    computeOppOdds(
                                activeOpponents, cards, holes, rand));

            swap(cards, e, COM_E);
            swap(cards, d, COM_D);
        }
        public Odds odds() {  return odds;  }
    }

    private static class RiverCommunityVisitor
            implements FastIntCombiner.CombinationVisitor1
    {
        private Card cards[];
        private int  activeOpponents;
        private Odds odds = new Odds();
        private long holes;
        private MersenneTwisterFast rand;

        public RiverCommunityVisitor(
                int  activeOpps,
                Card cards[],
                long holes,
                MersenneTwisterFast rand)
        {
            activeOpponents = activeOpps;
            this.cards      = cards;
            this.rand       = rand;
            this.holes      = holes;
        }
        public void visit(int e)
        {
            swap(cards, e, COM_E);

            odds = odds.plus(
                    computeOppOdds(
                                activeOpponents, cards, holes, rand));

            swap(cards, e, COM_E);
        }
        public Odds odds() {  return odds;  }
    }



    //--------------------------------------------------------------------
    private static Odds computeOppOdds(
            int  activeOpps,
            Card cards[],
            long holes,
            MersenneTwisterFast rand)
    {
        Card comA = cards[ COM_A ];
        Card comB = cards[ COM_B ];
        Card comC = cards[ COM_C ];
        Card comD = cards[ COM_D ];
        Card comE = cards[ COM_E ];

        int wins   = 0;
        int losses = 0;
        int splits = 0;

        int shortcut =
                Eval7Faster.shortcutFor(
                        comA, comB, comC, comD, comE);

        short myVal =
                Eval7Faster.fastValueOf(shortcut,
                        cards[ HOLE_A ],
                        cards[ HOLE_B ]);

        for (int i = 0; i < holes; i++)
        {
            short oppVal =
                    approxMaxOppVal(0, activeOpps, cards,
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
            Card cards[],
            int  communityShortcut,
            MersenneTwisterFast rand)
    {
        if (atOpp >= activeOpps)
        {
            return Short.MIN_VALUE;
        }

        int holeDestA = OPP_A - atOpp*2;
        int holeDestB = holeDestA - 1;

        int xOppA = rand.nextInt( holeDestA + 1 );
        int xOppB = rand.nextInt( holeDestB + 1 );

        swap(cards, xOppA, holeDestA);
        swap(cards, xOppB, holeDestB);

        Card oppA = cards[ holeDestA ];
        Card oppB = cards[ holeDestB ];

        short oppVal = Eval7Faster.fastValueOf(
                communityShortcut, oppA, oppB);

        short maxOtherOppVal =
                approxMaxOppVal(atOpp + 1, activeOpps, cards,
                                communityShortcut, rand);

        swap(cards, xOppB, holeDestB);
        swap(cards, xOppA, holeDestA);

        return (short) Math.max(oppVal, maxOtherOppVal);
    }
}
