package ao.holdem.bots.util;

import ao.holdem.def.model.card.Card;
import ao.holdem.def.model.card.eval7.Eval7Fast;
import ao.holdem.def.model.cards.Hole;
import ao.holdem.def.model.cards.community.Community;

/**
 * Calculates odds of winning/losing.
 */
public class HeadsUpOddFinder implements OddFinder
{
    //--------------------------------------------------------------------
    public HeadsUpOddFinder() {}


    //--------------------------------------------------------------------
    public Odds compute(Hole      hole,
                        Community community,
                        int       activeOpponents)
    {
        Card cards[]      = Card.values().clone();
        int  unknownCount = 52 - 2 - community.knownCount();

        moveHolesToEnd(cards, hole);
        moveCommunityToEnd(cards, community);

        switch (community.knownCount())
        {
            case 0:
                switch (activeOpponents)
                {
                    case 1: return oddsForFive(cards, unknownCount);
                }
                break;
            case 3:
                switch (activeOpponents)
                {
                    case 1: return oddsFor3x1(cards, unknownCount);
                }
                break;
            case 4:
                switch (activeOpponents)
                {
                    case 1: return oddsFor4x1(cards, unknownCount);
                }
                break;
            case 5:
                switch (activeOpponents)
                {
                    case 1: return oddsFor5x1(cards, unknownCount);
                }
                break;
        }

        return new Odds(-1, -1, -1);
    }

    // 0 known community
    // 1 opponent
    private Odds oddsForFive(Card cards[], int unknown)
    {
        int win   = 0;
        int lose  = 0;
        int split = 0;

        Card myHole1 = cards[51];
        Card myHole2 = cards[50];

        for (int c0 = 0; c0 < unknown; c0++) {
            Card com1 = cards[c0]; // communities
            for (int c1 = c0 + 1; c1 < unknown; c1++) {
                Card com2 = cards[c1];
                for (int c2 = c1 + 1; c2 < unknown; c2++) {
                    Card com3 = cards[c2];
                    for (int c3 = c2 + 1; c3 < unknown; c3++) {
                        Card com4 = cards[c3];
                        for (int c4 = c3 + 1; c4 < unknown; c4++) {
                            Card com5 = cards[c4];
        {
            short myVal =
                    Eval7Fast.valueOf(myHole1, myHole2,
                        com1, com2, com3, com4, com5);

            swap(cards, com1.ordinal(), cards.length - 3);

        }
//                            for (int c5 = c4 + 1; c5 < unknown; c5++) {
//                                Card card5 = cards[c5]; // opp hole 1
//                                for (int c6 = c5 + 1; c6 < unknown; c6++) {
//                                    Card card6 = cards[c6]; // opp hole 2
//            short oppVal =
//                    Eval7Fast.valueOf(com1, com2, com3, com4,
//                                        com5, card5, card6);
//
//            if      (oppVal > myVal) { lose++;  }
//            else if (oppVal < myVal) { win++;   }
//            else                     { split++; }
//        }}
                        }}}}}

        return new Odds(win, lose, split);
    }


    // 3 known community
    // 1 opponent
    private Odds oddsFor3x1(Card cards[], int unknown)
    {
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
    private Odds oddsFor4x1(Card cards[], int unknown)
    {
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

    private Odds oddsFor5x1(Card cards[], int unknown)
    {
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
}
