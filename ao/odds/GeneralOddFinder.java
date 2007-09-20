package ao.odds;

import ao.holdem.model.Card;
import ao.hand_eval.eval7.Eval7Faster;
import ao.holdem.model.Hole;
import ao.holdem.model.Community;
import ao.util.stats.FastIntCombiner;
import ao.odds.Odds;
import ao.odds.OddFinder;
import sun.plugin.dom.exception.InvalidStateException;

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


    //--------------------------------------------------------------------
    public Odds compute(Hole      hole,
                        Community community,
                        int       activeOpponents)
    {
        Card cards[]   = Card.values();
        int  indexes[] = cardIndexes();

        moveKnownCardsToEnd(indexes, hole, community);

        int unknownCount = 52 - 2 - community.knownCount();
        FastIntCombiner fc =
                    new FastIntCombiner(indexes, unknownCount);

        switch (community.knownCount())
        {
            case 0:
                CommunityVisitor5 c5 =
                        new CommunityVisitor5(
                                activeOpponents, indexes, cards);
                fc.combine(c5);
                return c5.odds();

            case 3:
                CommunityVisitor2 c2 =
                        new CommunityVisitor2(
                                activeOpponents, indexes, cards);
                fc.combine(c2);
                return c2.odds();

            case 4:
                CommunityVisitor1 c1 =
                        new CommunityVisitor1(
                                activeOpponents, indexes, cards);
                fc.combine(c1);
                return c1.odds();

            case 5:
                return computeOppCards(activeOpponents, indexes, cards);
        }
        return null;
    }


    //--------------------------------------------------------------------
    private static class CommunityVisitor5
            implements FastIntCombiner.CombinationVisitor5
    {
        private Card cards[];
        private int  indexes[];
        private int  activeOpponents;
        private Odds odds = new Odds();

        public CommunityVisitor5(
                int  activeOpps,
                int  indexes[],
                Card cards[])
        {
            activeOpponents = activeOpps;
            this.indexes    = indexes;
            this.cards      = cards;
        }

        public void visit(int a, int b, int c, int d, int e)
        {
            swap(indexes, a, COM_A);
            swap(indexes, b, COM_B);
            swap(indexes, c, COM_C);
            swap(indexes, d, COM_D);
            swap(indexes, e, COM_E);

            odds = odds.plus(
                    computeOppCards(activeOpponents, indexes, cards));

            swap(indexes, e, COM_E);
            swap(indexes, d, COM_D);
            swap(indexes, c, COM_C);
            swap(indexes, b, COM_B);
            swap(indexes, a, COM_A);
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
        private int  indexes[];
        private int  activeOpponents;
        private Odds odds = new Odds();

        public CommunityVisitor2(
                int  activeOpps,
                int  indexes[],
                Card cards[])
        {
            activeOpponents = activeOpps;
            this.indexes    = indexes;
            this.cards      = cards;
        }

        public void visit(int d, int e)
        {
            swap(indexes, d, COM_D);
            swap(indexes, e, COM_E);

            odds = odds.plus(
                    computeOppCards(activeOpponents, indexes, cards));

            swap(indexes, e, COM_E);
            swap(indexes, d, COM_D);
        }

        public Odds odds() {  return odds;  }
    }
    private static class CommunityVisitor1
            implements FastIntCombiner.CombinationVisitor1
    {
        private Card cards[];
        private int  indexes[];
        private int  activeOpponents;
        private Odds odds = new Odds();

        public CommunityVisitor1(
                int  activeOpps,
                int  indexes[],
                Card cards[])
        {
            activeOpponents = activeOpps;
            this.indexes    = indexes;
            this.cards      = cards;
        }

        public void visit(int e)
        {
            swap(indexes, e, COM_E);
            odds = odds.plus(
                    computeOppCards(activeOpponents, indexes, cards));
            swap(indexes, e, COM_E);
        }

        public Odds odds() {  return odds;  }
    }


    //--------------------------------------------------------------------
    private static Odds computeOppCards(
            int  activeOpps,
            int  indexes[],
            Card cards[])
    {
        Card comA = cards[ indexes[COM_A] ],
             comB = cards[ indexes[COM_B] ],
             comC = cards[ indexes[COM_C] ],
             comD = cards[ indexes[COM_D] ],
             comE = cards[ indexes[COM_E] ];

        int shortcut =
                Eval7Faster.shortcutFor(comA, comB, comC, comD, comE);

//        short myVal = evalHand(comA, comB, comC, comD, comE,
//                               cards[ indexes[HOLE_A] ],
//                               cards[ indexes[HOLE_B] ]);
        short myVal = Eval7Faster.fastValueOf(shortcut,
                                          cards[ indexes[HOLE_A] ],
                                          cards[ indexes[HOLE_B] ]);

        FastIntCombiner fc = new FastIntCombiner(indexes, 52 - 2 - 5);
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
                throw new InvalidStateException(
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
    public static void moveKnownCardsToEnd(
            int cards[], Hole hole, Community community)
    {
        swap(cards, hole.first().ordinal(),  HOLE_A);
        swap(cards, hole.second().ordinal(), HOLE_B);

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

    // selected from right [51] to left [0]
    //  meaning rightmost elements of indexes[] are
    //  personal hole cards, and common community cards,
    //  and to the left of those (0..44, ie. < 52 - 2 - 5)
    //  are opponents' simulated cards.
    public static int[] cardIndexes()
    {
        int  indexes[] = new int[52];
        for (int i = 0; i < 52; i++) {  indexes[i] = i;  }
        return indexes;
    }

    public static void swap(int cards[], int i, int j)
    {
        int temp = cards[i];
        cards[i] = cards[j];
        cards[j] = temp;
    }
}
