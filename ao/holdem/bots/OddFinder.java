package ao.holdem.bots;

import ao.holdem.def.model.card.Card;
import ao.holdem.def.model.card.eval7.Eval7Fast;
import ao.holdem.def.model.cards.Hole;
import ao.holdem.def.model.cards.community.Community;
import ao.util.stats.Combiner;

/**
 * Calculates odds of winning/losing.
 */
public class OddFinder
{
    //--------------------------------------------------------------------
    private final int  knownCommunity;
    private final Card cards[] = Card.values().clone();
    private final int  opponents;


    //--------------------------------------------------------------------
    public OddFinder(Hole      hole,
                     Community community,
                     int       opponents)
    {
        moveHolesToEnd(cards, hole);
        knownCommunity = moveCommunityToEnd(cards, community);

        this.opponents = opponents;
    }


    //--------------------------------------------------------------------
    public long combos()
    {
        int oppCards         = 2 * opponents;
        int unknownCommunity = (5 - knownCommunity);
        int choose           = oppCards + unknownCommunity;

        return Combiner.choose(unknownCount(), choose);
    }

    private int unknownCount()
    {
        return 52 - knownCount();
    }

    private int knownCount()
    {
        return 2 + knownCommunity;
    }


    //--------------------------------------------------------------------
    // expected percent of not losing. (ie. winnin or tying)
    public Odds compute()
    {
        switch (knownCommunity)
        {
            case 0:
                switch (opponents)
                {
                    case 1: return oddsForFive();
                }
                break;
            case 3:
                switch (opponents)
                {
                    case 1: return oddsFor3x1();
                }
                break;
            case 4:
                switch (opponents)
                {
                    case 1: return oddsFor4x1();
                }
                break;
            case 5:
                switch (opponents)
                {
                    case 1: return oddsFor5x1();
                }
                break;
        }

        return new Odds(-1, -1, -1);
    }

    // 0 known community
    // 1 opponent
    private Odds oddsForFive()
    {
        int unknown = unknownCount();
        int win     = 0;
        int lose    = 0;
        int split   = 0;

        for (int c0 = 0; c0 < unknown; c0++) {
            Card card0 = cards[c0]; // communities
            for (int c1 = c0 + 1; c1 < unknown; c1++) {
                Card card1 = cards[c1];
                for (int c2 = c1 + 1; c2 < unknown; c2++) {
                    Card card2 = cards[c2];
                    for (int c3 = c2 + 1; c3 < unknown; c3++) {
                        Card card3 = cards[c3];
                        for (int c4 = c3 + 1; c4 < unknown; c4++) {
                            Card card4 = cards[c4];
                            short myVal =
                                    Eval7Fast.valueOf(cards[51], cards[50],
                                        card0, card1, card2, card3, card4);

                            for (int c5 = c4 + 1; c5 < unknown; c5++) {
                                Card card5 = cards[c5]; // opp hole 1
                                for (int c6 = c5 + 1; c6 < unknown; c6++) {
                                    Card card6 = cards[c6]; // opp hole 2
            short oppVal =
                    Eval7Fast.valueOf(card0, card1, card2, card3,
                                        card4, card5, card6);

            if      (oppVal > myVal) { lose++;  }
            else if (oppVal < myVal) { win++;   }
            else                     { split++; }
        }}}}}}}

        return new Odds(win, lose, split);
    }

//    private Odds opponentOdds(
//            int atOpponent,
//            Card community1, Card community2, Card community3,
//            Card community4, Card community5,
//            int  startAt, short vsVal)
//    {
//        int unknown = unknownCount();
//
//        Odds odds = new Odds();
//        for (int hole1 = startAt; hole1 < unknown; hole1++) {
//            Card firstHole = cards[hole1];
//            for (int hole2 = hole1 + 1; hole2 < unknown; hole2++) {
//                Card secondHole = cards[hole2];
//
//                short oppVal =
//                    Eval7Fast.valueOf(
//                            community1, community2, community3,
//                            community4, community5,
//                            firstHole,  secondHole);
//
//                if      (oppVal > vsVal) { lose++;  }
//                else if (oppVal < myVal) { win++;   }
//                else                     { split++; }
//
//                if (atOpponent == 1)
//                {
//                    odds = odds.min();
//                }                          
//                else
//                {
//                    odds = odds.min(
//                            opponentOdds(atOpponent - 1,
//                                         community1, community2,
//                                         community3, community4,
//                                         community5,
//                                         hole2 + 1, vsVal));
//                }
//            }
//        }
//
//        return null;
//    }



    // 3 known community
    // 1 opponent
    private Odds oddsFor3x1()
    {
        int unknown = unknownCount();
        int win     = 0;
        int lose    = 0;
        int split   = 0;

        for (int c0 = 0; c0 < unknown; c0++) {
            Card card0 = cards[c0]; // communities
            for (int c1 = c0 + 1; c1 < unknown; c1++) {
                Card card1 = cards[c1];
                short myVal =
                        Eval7Fast.valueOf(cards[51], cards[50],
                            card0, card1, cards[49], cards[48], cards[47]);

                    for (int c5 = c1 + 1; c5 < unknown; c5++) {
                        Card card5 = cards[c5]; // opp hole 1
                        for (int c6 = c5 + 1; c6 < unknown; c6++) {
                            Card card6 = cards[c6]; // opp hole 2
            short oppVal =
                    Eval7Fast.valueOf(card0, card1, // community
                                      card5, card6, // holes
                                      cards[49], cards[48], cards[47]);

            if      (oppVal > myVal) { lose++;  }
            else if (oppVal < myVal) { win++;   }
            else                     { split++; }
        }}}}

        return new Odds(win, lose, split);
    }

    // 4 known community
    // 1 opponent
    private Odds oddsFor4x1()
    {
        int unknown = unknownCount();
        int win     = 0;
        int lose    = 0;
        int split   = 0;

        for (int c0 = 0; c0 < unknown; c0++) {
            Card card0 = cards[c0]; // communities
            short myVal =
                    Eval7Fast.valueOf(cards[51], cards[50],
                        card0, cards[49], cards[48], cards[47], cards[46]);

                for (int c5 = c0 + 1; c5 < unknown; c5++) {
                    Card card5 = cards[c5]; // opp hole 1
                    for (int c6 = c5 + 1; c6 < unknown; c6++) {
                        Card card6 = cards[c6]; // opp hole 2
            short oppVal =
                    Eval7Fast.valueOf(card0,        // community
                                      card5, card6, // holes
                                      cards[49], cards[48], cards[47], cards[46]);

            if      (oppVal > myVal) { lose++;  }
            else if (oppVal < myVal) { win++;   }
            else                     { split++; }
        }}}

        return new Odds(win, lose, split);
    }

    private Odds oddsFor5x1()
    {
        int unknown = unknownCount();
        int win     = 0;
        int lose    = 0;
        int split   = 0;

        short myVal =
                Eval7Fast.valueOf(cards[51], cards[50],
                                  cards[49], cards[48], cards[47],
                                  cards[46], cards[45]);

        for (int c5 = 0; c5 < unknown; c5++) {
            Card card5 = cards[c5]; // opp hole 1
            for (int c6 = c5 + 1; c6 < unknown; c6++) {
                Card card6 = cards[c6]; // opp hole 2
            short oppVal =
                    Eval7Fast.valueOf(
                            card5, card6, // holes
                            cards[49], cards[48], cards[47],
                            cards[46], cards[45]);

            if      (oppVal > myVal) { lose++;  }
            else if (oppVal < myVal) { win++;   }
            else                     { split++; }
        }}

        return new Odds(win, lose, split);
    }

    //--------------------------------------------------------------------
    private void moveHolesToEnd(Card cards[], Hole hole)
    {
        swap(cards, hole.first().ordinal(),  cards.length - 1);
        swap(cards, hole.second().ordinal(), cards.length - 2);
    }

    private int moveCommunityToEnd(Card cards[], Community community)
    {
        int communityCards = 0;
        if (community.flop() != null)
        {
            communityCards = 3;
            swap(cards, community.flop().first().ordinal(),  cards.length - 3);
            swap(cards, community.flop().second().ordinal(), cards.length - 4);
            swap(cards, community.flop().third().ordinal(),  cards.length - 5);
        }
        if (community.turn() != null)
        {
            communityCards = 4;
            swap(cards, community.turn().fourth().ordinal(), cards.length - 6);
        }
        if (community.river() != null)
        {
            communityCards = 5;
            swap(cards, community.turn().fourth().ordinal(), cards.length - 7);
        }
        return communityCards;
    }


    //--------------------------------------------------------------------
    private static void swap(Card cards[], int i, int j)
    {
        Card temp = cards[i];
        cards[i] = cards[j];
        cards[j] = temp;
    }


    //--------------------------------------------------------------------
    public static class Odds
    {
        private final int WIN;
        private final int LOSE;
        private final int SPLIT;

        public Odds()
        {
            this(-1, -1, -1);
        }

        public Odds(int winOdds, int loseOdds, int splitOdds)
        {
            WIN   = winOdds;
            LOSE  = loseOdds;
            SPLIT = splitOdds;
        }


        public Odds min(Odds between)
        {
            return (winPercent() < between.winPercent())
                    ? this
                    : between;
//            return new Odds(WIN   + addend.WIN,
//                            LOSE  + addend.LOSE,
//                            SPLIT + addend.SPLIT);
        }


        public int winOdds()
        {
            return WIN;
        }
        public double winPercent()
        {
            return ((double) WIN) / (WIN + LOSE + SPLIT);
        }


        public int loseOdds()
        {
            return LOSE;
        }
        public double losePercent()
        {
            return ((double) LOSE) / (WIN + LOSE + SPLIT);
        }


        public int splitOdds()
        {
            return SPLIT;
        }
        public double splitPercent()
        {
            return ((double) SPLIT) / (WIN + LOSE + SPLIT);
        }

        @Override
        public String toString()
        {
            return "[win: "    + WIN   +
                    " (" + Math.round(winPercent()  * 100) + ")" +
                   ", lose: "  + LOSE  +
                    " (" + Math.round(losePercent() * 100) + ")" +
                   ", split: " + SPLIT +
                   " (" + Math.round(splitPercent() * 100) + ")" + "]";
        }
    }
}
