package ao.odds.agglom.impl;

import ao.holdem.model.card.*;
import ao.odds.agglom.OddFinder;
import ao.odds.agglom.Odds;
import ao.odds.eval.eval7.Eval7Faster;
import static ao.util.data.Arr.sequence;
import static ao.util.data.Arr.swap;
import ao.util.stats.FastIntCombiner;

/**
 * does NOT match numbers from:
 *  http://wizardofodds.com/holdem/2players.html
 * wtf?!?!
 */
public class GeneralOddFinder implements OddFinder
{
    //--------------------------------------------------------------------
    public static final int HOLE_A = 52 - 1,
                            HOLE_B = 52 - 2,
                            COM_A  = 52 - (2 + 1),
                            COM_B  = 52 - (2 + 2),
                            COM_C  = 52 - (2 + 3),
                            COM_D  = 52 - (2 + 4),
                            COM_E  = 52 - (2 + 5),
                            OPPS   = 52 - (2 + 5 + 1);

    public static final int INDEXES[] = sequence( Card.VALUES.length );


    //--------------------------------------------------------------------
    public Odds compute(Hole      hole,
                        Community community,
                        int       activeOpponents)
    {
        // selected from right [51] to left [0]
        //  meaning rightmost elements of indexes[] are
        //  personal hole cards, and common community cards,
        //  and to the left of those (0..44, ie. < 52 - 2 - 5)
        //  are opponents' simulated cards.
        Card cards[]   = Card.values();

        initKnownCardsToEnd(cards, hole, community);

        int unknownCount = 52 - 2 - community.knownCount();
        FastIntCombiner fc =
                    new FastIntCombiner(INDEXES, unknownCount);

        switch (community.knownCount())
        {
            case 0:
                CommunityVisitor5 c5 =
                        new CommunityVisitor5(
                                activeOpponents, cards);
                fc.combine(c5);
                return c5.odds();

            case 3:
                CommunityVisitor2 c2 =
                        new CommunityVisitor2(
                                activeOpponents, cards);
                fc.combine(c2);
                return c2.odds();

            case 4:
                CommunityVisitor1 c1 =
                        new CommunityVisitor1(
                                activeOpponents, cards);
                fc.combine(c1);
                return c1.odds();

            case 5:
                return computeOppCards(activeOpponents, cards);
        }
        return null;
    }


    //--------------------------------------------------------------------
    private static class CommunityVisitor5
            implements FastIntCombiner.CombinationVisitor5
    {
        private Card cards[];
        private int  activeOpponents;
        private Odds odds = new Odds();

        public CommunityVisitor5(
                int  activeOpps,
                Card cards[])
        {
            activeOpponents = activeOpps;
            this.cards      = cards;
        }

        public void visit(int a, int b, int c, int d, int e)
        {
            swap(cards, a, COM_A);
            swap(cards, b, COM_B);
            swap(cards, c, COM_C);
            swap(cards, d, COM_D);
            swap(cards, e, COM_E);

            odds = odds.plus(
                    computeOppCards(activeOpponents, cards));

            swap(cards, e, COM_E);
            swap(cards, d, COM_D);
            swap(cards, c, COM_C);
            swap(cards, b, COM_B);
            swap(cards, a, COM_A);
        }

        public Odds odds()
        {
            return odds;
        }
    }
    private static class CommunityVisitor2
            implements FastIntCombiner.CombinationVisitor2
    {
        private Card cards[];
        private int  activeOpponents;
        private Odds odds = new Odds();

        public CommunityVisitor2(
                int  activeOpps,
                Card cards[])
        {
            activeOpponents = activeOpps;
            this.cards      = cards;
        }

        public void visit(int d, int e)
        {
            swap(cards, d, COM_D);
            swap(cards, e, COM_E);

            odds = odds.plus(
                    computeOppCards(activeOpponents, cards));

            swap(cards, e, COM_E);
            swap(cards, d, COM_D);
        }

        public Odds odds() {  return odds;  }
    }
    private static class CommunityVisitor1
            implements FastIntCombiner.CombinationVisitor1
    {
        private Card cards[];
        private int  activeOpponents;
        private Odds odds = new Odds();

        public CommunityVisitor1(
                int  activeOpps,
                Card cards[])
        {
            activeOpponents = activeOpps;
            this.cards      = cards;
        }

        public void visit(int e)
        {
            swap(cards, e, COM_E);
            odds = odds.plus(
                    computeOppCards(activeOpponents, cards));
            swap(cards, e, COM_E);
        }

        public Odds odds() {  return odds;  }
    }


    //--------------------------------------------------------------------
    private static Odds computeOppCards(
            int  activeOpps,
            Card cards[])
    {
        Card comA = cards[ COM_A ],
             comB = cards[ COM_B ],
             comC = cards[ COM_C ],
             comD = cards[ COM_D ],
             comE = cards[ COM_E ];

        int shortcut =
                Eval7Faster.shortcutFor(comA, comB, comC, comD, comE);

//        short myVal = evalHand(comA, comB, comC, comD, comE,
//                               cards[ indexes[HOLE_A] ],
//                               cards[ indexes[HOLE_B] ]);
        short myVal = Eval7Faster.fastValueOf(shortcut,
                                          cards[ HOLE_A ],
                                          cards[ HOLE_B ]);

        FastIntCombiner fc = new FastIntCombiner(INDEXES, 52 - 2 - 5);
        switch (activeOpps)
        {
            case 1:
//                HeadsUpVisitor huv =
//                        new HeadsUpVisitor(cards, myVal,
//                                           comA, comB, comC, comD, comE);
                HeadsUpVisitor huv =
                        new HeadsUpVisitor(cards, myVal, shortcut);
                fc.combine(huv);
                return huv.odds();

            case 2:
            case 3:
            default:
                throw new IllegalArgumentException(
                        "intractable # of opponents: " + activeOpps);
        }

//        return null;
    }

    //--------------------------------------------------------------------
    private static class HeadsUpVisitor
            implements FastIntCombiner.CombinationVisitor2,
                       OddsCalculator
    {
        private final short myVal;
        private final Card  cards[];
        //private final Card  comA, comB, comC, comD, comE;
        private final int shortcut;
        private int wins, losses, splits;

        public HeadsUpVisitor(Card  cards[],
                              short vsVal,
                              int   shortcut)
//                              Card  comA, Card comB,
//                              Card  comC, Card comD, Card comE)
        {
            this.cards    = cards;
            this.myVal    = vsVal;
            this.shortcut = shortcut;
//            this.comA = comA; this.comB = comB;
//            this.comC = comC; this.comD = comD; this.comE = comE;
        }

        public void visit(int a, int b)
        {
//            short oppVal = evalHand(comA, comB, comC, comD, comE,
//                                    cards[ a ], cards[ b ]);
            short oppVal = Eval7Faster.fastValueOf(
                                shortcut, cards[ a ], cards[ b ]);

            if      (myVal > oppVal) { wins++;   }
            else if (myVal < oppVal) { losses++; }
            else                     { splits++; }
        }

        public Odds odds()
        {
            return new Odds(wins, losses, splits);
        }
    }


    //--------------------------------------------------------------------
    private static interface OddsCalculator
    {
        public Odds odds();
    }

    public static short evalHand(Card a, Card b, Card c,
                                 Card d, Card e, Card f, Card g)
    {
        return Eval7Faster.valueOf(a, b, c, d, e, f, g);
    }


    //--------------------------------------------------------------------
    public static void initKnownCardsToEnd(
            Card cards[], Hole hole, Community community)
    {
        swap(cards, hole.a().ordinal(), HOLE_A);
        swap(cards, hole.b().ordinal(), HOLE_B);

        switch (community.knownCount())
        {
            case 5:
                swap(cards, community.river().ordinal(), COM_E);

            case 4:
                swap(cards, community.turn().ordinal(),  COM_D);

            case 3:
                swap(cards, community.flopC().ordinal(), COM_C);
                swap(cards, community.flopB().ordinal(), COM_B);
                swap(cards, community.flopA().ordinal(), COM_A);
        }
    }

    
    //--------------------------------------------------------------------
    public static void main(String args[])
    {
        OddFinder oddFinder = new GeneralOddFinder();

        Suit a = Suit.SPADES;
        Suit b = Suit.HEARTS;
        Suit c = Suit.DIAMONDS;
        Suit d = Suit.CLUBS;

        Odds odds = oddFinder.compute(
//                Hole.newInstance(Card.TWO_OF_HEARTS,
//                                 Card.TWO_OF_SPADES),
//                new Community(Card.TEN_OF_DIAMONDS,
//                              Card.TEN_OF_CLUBS,
//                              Card.JACK_OF_HEARTS,
//                              Card.JACK_OF_SPADES,
//                              Card.FIVE_OF_DIAMONDS),
                Hole.valueOf(Card.valueOf(Rank.TWO, a),
                                 Card.valueOf(Rank.TWO, b)),
                new Community(Card.valueOf(Rank.TEN, c),
                              Card.valueOf(Rank.TEN, d),
                              Card.valueOf(Rank.JACK, a),
                              Card.valueOf(Rank.JACK, b),
                              Card.valueOf(Rank.FIVE, c)),
                1);
        System.out.println( odds );
    }
}
