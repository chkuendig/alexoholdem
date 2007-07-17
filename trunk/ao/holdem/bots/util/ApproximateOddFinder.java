package ao.holdem.bots.util;

import ao.holdem.def.model.card.Card;
import ao.holdem.def.model.cards.Hole;
import ao.holdem.def.model.cards.community.Community;
import ao.util.rand.MersenneTwisterFast;

/**
 * Threadsafe!!
 */
public class ApproximateOddFinder implements OddFinder
{
    //--------------------------------------------------------------------
    private static final int FLOP_ITR = 500; //4000
    private static final int HOLE_ITR = 100;  //500


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

                for (int i = 0; i < FLOP_ITR; i++)
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
                                activeOpponents, indexes, cards, rand));

                    GeneralOddFinder.swap(indexes, xComE, GeneralOddFinder.COM_E);
                    GeneralOddFinder.swap(indexes, xComD, GeneralOddFinder.COM_D);
                    GeneralOddFinder.swap(indexes, xComC, GeneralOddFinder.COM_C);
                    GeneralOddFinder.swap(indexes, xComB, GeneralOddFinder.COM_B);
                    GeneralOddFinder.swap(indexes, xComA, GeneralOddFinder.COM_A);
                }
                break;
            case 3:
                for (int i = 0; i < FLOP_ITR; i++)
                {
                    int xComD = rand.nextInt( GeneralOddFinder.COM_D + 1 );
                    GeneralOddFinder.swap(indexes, xComD, GeneralOddFinder.COM_D);
                    int xComE = rand.nextInt( GeneralOddFinder.COM_E + 1 );
                    GeneralOddFinder.swap(indexes, xComE, GeneralOddFinder.COM_E);

                    odds = odds.plus(computeOppOdds(
                                activeOpponents, indexes, cards, rand));

                    GeneralOddFinder.swap(indexes, xComE, GeneralOddFinder.COM_E);
                    GeneralOddFinder.swap(indexes, xComD, GeneralOddFinder.COM_D);
                }
                break;
            case 4:
                for (int i = 0; i < FLOP_ITR; i++)
                {
                    int xComE = rand.nextInt( GeneralOddFinder.COM_E + 1 );
                    GeneralOddFinder.swap(indexes, xComE, GeneralOddFinder.COM_E);

                    odds = odds.plus(computeOppOdds(
                                activeOpponents, indexes, cards, rand));

                    GeneralOddFinder.swap(indexes, xComE, GeneralOddFinder.COM_E);
                }
                break;
            case 5:
                for (int i = 0; i < FLOP_ITR; i++)
                {
                    odds = odds.plus(computeOppOdds(
                                activeOpponents, indexes, cards, rand));
                }
                break;
        }

        return odds;
    }

    private static Odds computeOppOdds(
            int  activeOpps,
            int  indexes[],
            Card cards[],
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

        short myVal =
                GeneralOddFinder.evalHand(
                        comA, comB, comC, comD, comE,
                        cards[ indexes[GeneralOddFinder.HOLE_A] ],
                        cards[ indexes[GeneralOddFinder.HOLE_B] ]);

        for (int i = 0; i < HOLE_ITR; i++)
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

        int holeDestA = GeneralOddFinder.OPPS - atOpp*2;
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
