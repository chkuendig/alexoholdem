package ao.holdem.bots.util;

import ao.holdem.def.model.card.Card;
import ao.holdem.def.model.cards.Hole;
import ao.holdem.def.model.cards.community.Community;
import ao.util.rand.MersenneTwisterFast;
import ao.util.stats.FastIntCombiner;

/**
 * Threadsafe!!
 */
public class AproximatingOddFinder implements OddFinder
{
    //--------------------------------------------------------------------
    private static final int FLOPS = 2000;
    private static final int HOLES = 100;


    //--------------------------------------------------------------------
    public Odds compute(
            Hole      hole,
            Community community,
            int       activeOpponents)
    {
        int  indexes[] = GeneralOddFinder.cardIndexes();
        Card cards[]   = Card.values();

        GeneralOddFinder.moveKnownCardsToEnd(
                indexes, hole, community);

        MersenneTwisterFast rand = new MersenneTwisterFast();

        Odds odds = new Odds();
        switch (community.knownCount())
        {
            case 0:
//                int unknownCount = 52 - 2 - community.knownCount();
//                FastIntCombiner fc =
//                    new FastIntCombiner(indexes, unknownCount);
//
//                CommunityVisitor5 c5 =
//                        new CommunityVisitor5(
//                                activeOpponents, indexes, cards, rand);
//                fc.combine(c5);
//                return c5.odds();

                for (int i = 0; i < FLOPS; i++)
                {
                    int xComA = rand.nextInt( 50 );
                    GeneralOddFinder.swap(indexes, xComA, 49);
                    int xComB = rand.nextInt( 49 );
                    GeneralOddFinder.swap(indexes, xComB, 48);
                    int xComC = rand.nextInt( 48 );
                    GeneralOddFinder.swap(indexes, xComC, 47);
                    int xComD = rand.nextInt( 47 );
                    GeneralOddFinder.swap(indexes, xComD, 46);
                    int xComE = rand.nextInt( 46 );
                    GeneralOddFinder.swap(indexes, xComE, 45);

                    odds = odds.plus(computeOppOdds(
                                activeOpponents, indexes, cards, rand));

                    GeneralOddFinder.swap(indexes, xComE, 45);
                    GeneralOddFinder.swap(indexes, xComD, 46);
                    GeneralOddFinder.swap(indexes, xComC, 47);
                    GeneralOddFinder.swap(indexes, xComB, 48);
                    GeneralOddFinder.swap(indexes, xComA, 49);
                }
                break;
            case 3:
                for (int i = 0; i < FLOPS; i++)
                {
                    int xComD = rand.nextInt( 47 );
                    GeneralOddFinder.swap(indexes, xComD, 46);
                    int xComE = rand.nextInt( 46 );
                    GeneralOddFinder.swap(indexes, xComE, 45);

                    odds = odds.plus(computeOppOdds(
                                activeOpponents, indexes, cards, rand));

                    GeneralOddFinder.swap(indexes, xComE, 45);
                    GeneralOddFinder.swap(indexes, xComD, 46);
                }
                break;
            case 4:
                for (int i = 0; i < FLOPS; i++)
                {
                    int xComE = rand.nextInt( 46 );
                    GeneralOddFinder.swap(indexes, xComE, 45);

                    odds = odds.plus(computeOppOdds(
                                activeOpponents, indexes, cards, rand));

                    GeneralOddFinder.swap(indexes, xComE, 45);
                }
                break;
            case 5:
                for (int i = 0; i < FLOPS; i++)
                {
                    odds = odds.plus(computeOppOdds(
                                activeOpponents, indexes, cards, rand));
                }
                break;
        }

        return odds;
    }

    private static class CommunityVisitor5
            implements FastIntCombiner.CombinationVisitor5
    {
        private Card cards[];
        private int  indexes[];
        private int  activeOpponents;
        private Odds odds = new Odds();
        private MersenneTwisterFast rand;

        public CommunityVisitor5(
                int  activeOpps,
                int  indexes[],
                Card cards[],
                MersenneTwisterFast rand)
        {
            activeOpponents = activeOpps;
            this.indexes    = indexes;
            this.cards      = cards;
            this.rand       = rand;
        }

        public void visit(int a, int b, int c, int d, int e)
        {
            GeneralOddFinder.swap(indexes, a, GeneralOddFinder.COM_A);
            GeneralOddFinder.swap(indexes, b, GeneralOddFinder.COM_B);
            GeneralOddFinder.swap(indexes, c, GeneralOddFinder.COM_C);
            GeneralOddFinder.swap(indexes, d, GeneralOddFinder.COM_D);
            GeneralOddFinder.swap(indexes, e, GeneralOddFinder.COM_E);

            odds = odds.plus(
                    computeOppOdds(
                                activeOpponents, indexes, cards, rand));

            GeneralOddFinder.swap(indexes, e, GeneralOddFinder.COM_E);
            GeneralOddFinder.swap(indexes, d, GeneralOddFinder.COM_D);
            GeneralOddFinder.swap(indexes, c, GeneralOddFinder.COM_C);
            GeneralOddFinder.swap(indexes, b, GeneralOddFinder.COM_B);
            GeneralOddFinder.swap(indexes, a, GeneralOddFinder.COM_A);
        }

        public Odds odds()
        {
            return odds;
        }
    }

    private static Odds computeOppOdds(
            int  activeOpps,
            int  indexes[],
            Card cards[],
            MersenneTwisterFast rand)
    {
        Card comA = cards[ indexes[49] ];
        Card comB = cards[ indexes[48] ];
        Card comC = cards[ indexes[47] ];
        Card comD = cards[ indexes[46] ];
        Card comE = cards[ indexes[45] ];

        int wins   = 0;
        int losses = 0;
        int splits = 0;

        short myVal =
                GeneralOddFinder.evalHand(
                        comA, comB, comC, comD, comE,
                        cards[ indexes[50] ],
                        cards[ indexes[51] ]);

        for (int i = 0; i < HOLES; i++)
        {
            short oppVal =
                    approxMaxOppVal(0, activeOpps, indexes, cards,
                                    comA, comB, comC, comD, comE,
                                    rand);

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
            Card comA, Card comB, Card comC, Card comD, Card comE,
            MersenneTwisterFast rand)
    {
        if (atOpp >= activeOpps)
        {
            return Short.MIN_VALUE;
        }

        int holeDestA = 44 - atOpp*2;
        int holeDestB = holeDestA - 1;

        int xOppA = rand.nextInt( holeDestA + 1 );
        int xOppB = rand.nextInt( holeDestB + 1 );

        GeneralOddFinder.swap(indexes, xOppA, holeDestA);
        GeneralOddFinder.swap(indexes, xOppB, holeDestB);

        Card oppA = cards[ indexes[holeDestA] ];
        Card oppB = cards[ indexes[holeDestB] ];

        short oppVal = GeneralOddFinder.evalHand(
                oppA, oppB,
                comA, comB, comC, comD, comE);

        short maxOtherOppVal =
                approxMaxOppVal(atOpp + 1, activeOpps, indexes, cards,
                                comA, comB, comC, comD, comE,
                                rand);

        GeneralOddFinder.swap(indexes, xOppB, holeDestB);
        GeneralOddFinder.swap(indexes, xOppA, holeDestA);

        return (short) Math.max(oppVal, maxOtherOppVal);
    }
}
