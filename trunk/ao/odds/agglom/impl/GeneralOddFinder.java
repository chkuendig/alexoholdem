package ao.odds.agglom.impl;

import ao.holdem.model.card.Card;
import ao.holdem.model.card.Community;
import ao.holdem.model.card.Hole;
import static ao.holdem.model.card.Rank.THREE;
import static ao.holdem.model.card.Rank.TWO;
import static ao.holdem.model.card.Suit.CLUBS;
import static ao.holdem.model.card.Suit.SPADES;
import ao.odds.agglom.OddFinder;
import ao.odds.agglom.Odds;
import ao.odds.eval.eval7.Eval7Faster;
import static ao.util.data.Arr.swap;

import java.util.EnumSet;

/**
 * does NOT match numbers from:
 *  http://wizardofodds.com/holdem/2players.html
 * wtf?!?!
 */
public class GeneralOddFinder implements OddFinder
{
    //--------------------------------------------------------------------
    private static final int HOLE_A = 51 - 1,
                             HOLE_B = 51,

                             FLOP_A = 51 - 4,
                             FLOP_B = 51 - 3,
                             FLOP_C = 51 - 2,

                             TURN   = 51 - 5,

                             RIVER  = 51 - 6,

                             OPP_A  = 51 - 8,
                             OPP_B  = 51 - 7;


    //--------------------------------------------------------------------
    public Odds compute(Hole      hole,
                        Community community,
                        int       activeOpponents)
    {
        assert activeOpponents == 1 : "must be heads up";
        return compute(hole, community);
    }

    public Odds compute(Hole      hole,
                        Community community)
    {

        Card cards[] = initKnownCardsToEnd(hole, community);
        return rollOutCommunity(
                cards,
                community.knownCount());
    }


    //--------------------------------------------------------------------
    private static Odds rollOutCommunity(
            Card cards[],
            int  knownCount)
    {
        return   knownCount == 0
               ? rollOutFlopTurnRiver(cards)
               : knownCount == 3
               ? rollOutTurnRiver(cards)
               : knownCount == 4
               ? rollOutRiver(cards)
               : null;
    }


    //--------------------------------------------------------------------
    private static Odds rollOutFlopTurnRiver(Card cards[])
    {
        Odds odds = new Odds();
        for (int flopIndexC = 4; flopIndexC <= FLOP_C; flopIndexC++)
        {
            Card flopC = cards[ flopIndexC ];
            swap(cards, flopIndexC, FLOP_C);

            for (int flopIndexB = 3;
                     flopIndexB < flopIndexC; flopIndexB++)
            {
                Card flopB = cards[ flopIndexB ];
                swap(cards, flopIndexB, FLOP_B);

                for (int flopIndexA = 2;
                         flopIndexA < flopIndexB; flopIndexA++)
                {
                    Card flopA = cards[ flopIndexA ];
                    swap(cards, flopIndexA, FLOP_A);

                    for (int turnIndex = 1;
                             turnIndex < flopIndexA; turnIndex++)
                    {
                        Card turn = cards[ turnIndex ];
                        swap(cards, turnIndex, TURN);

                        for (int riverIndex = 0;
                                 riverIndex < turnIndex; riverIndex++)
                        {
                            Card river = cards[ riverIndex ];
                            swap(cards, riverIndex, RIVER);

                            int   shortcut =
                            Eval7Faster.shortcutFor(
                                    flopA, flopB, flopC,
                                    turn, river);
                            short thisVal  =
                                    Eval7Faster.fastValueOf(
                                            shortcut,
                                            cards[ HOLE_A ],
                                            cards[ HOLE_B ]);

                            odds = odds.plus(
                                    rollOutOpp(cards, shortcut, thisVal));

                            swap(cards, riverIndex, RIVER);
                        }

                        swap(cards, turnIndex, TURN);
                    }

                    swap(cards, flopIndexA, FLOP_A);
                }

                swap(cards, flopIndexB, FLOP_B);
            }

            swap(cards, flopIndexC, FLOP_C);
        }
        return odds;
    }


    //--------------------------------------------------------------------
    private static Odds rollOutTurnRiver(Card cards[])
    {
        Odds odds = new Odds();
        for (int turnIndex =  1;
                 turnIndex <= TURN;
                 turnIndex++)
        {
            Card turnCard = cards[ turnIndex ];
            swap(cards, turnIndex, TURN);
            for (int riverIndex = 0;
                     riverIndex < turnIndex;
                     riverIndex++)
            {
                Card riverCard = cards[ riverIndex ];
                swap(cards, riverIndex, RIVER);

                int   shortcut =
                    Eval7Faster.shortcutFor(
                            cards[ FLOP_A ],
                            cards[ FLOP_B ],
                            cards[ FLOP_C ],
                            turnCard, riverCard);
                short thisVal  =
                    Eval7Faster.fastValueOf(
                            shortcut,
                            cards[ HOLE_A ], cards[ HOLE_B ]);

                odds = odds.plus(
                        rollOutOpp(cards, shortcut, thisVal));

                swap(cards, riverIndex, RIVER);
            }
            swap(cards, turnIndex, TURN);
        }
        return odds;
    }


    //--------------------------------------------------------------------
    private static Odds rollOutRiver(
            Card  cards[])
    {
        Odds odds = new Odds();
        for (int riverIndex = 0;
                 riverIndex <= RIVER;
                 riverIndex++)
        {
            Card riverCard = cards[ riverIndex ];
            swap(cards, riverIndex, RIVER);

            int   shortcut =
                Eval7Faster.shortcutFor(
                        cards[ FLOP_A ],
                        cards[ FLOP_B ],
                        cards[ FLOP_C ],
                        cards[ TURN ],
                        riverCard);
            short thisVal  =
                Eval7Faster.fastValueOf(
                        shortcut,
                        cards[ HOLE_A ], cards[ HOLE_B ]);

            odds = odds.plus(
                    rollOutOpp(cards, shortcut, thisVal));

            swap(cards, riverIndex, RIVER);
        }
        return odds;
    }


    //--------------------------------------------------------------------
    private static Odds rollOutOpp(
            Card  cards[],
            int   shortcut,
            short thisVal)
    {
        Odds odds = new Odds();
        for (int oppIndexB = 1; oppIndexB <= OPP_B; oppIndexB++)
        {
            for (int oppIndexA = 0;
                     oppIndexA < oppIndexB;
                     oppIndexA++)
            {
                short thatVal =
                        Eval7Faster.fastValueOf(
                                shortcut,
                                cards[ oppIndexA ], cards[ oppIndexB ]);
                odds = odds.plus(
                        Odds.valueOf(thisVal, thatVal));
            }
        }
        return odds;
    }


    //--------------------------------------------------------------------
    public static Card[] initKnownCardsToEnd(
            Hole hole, Community community)
    {
        EnumSet<Card> known = asSet(hole, community);

        int  index   = 0;
        Card cards[] = new Card[ Card.VALUES.length ];
        for (Card card : Card.VALUES)
        {
            if (! known.contains(card))
            {
                cards[ index++ ] = card;
            }
        }

        cards[ HOLE_A ] = hole.a();
        cards[ HOLE_B ] = hole.b();
        switch (community.knownCount())
        {
            case 5:
                cards[ RIVER  ] = community.river();

            case 4:
                cards[ TURN   ] = community.turn();

            case 3:
                cards[ FLOP_A ] = community.flopA();
                cards[ FLOP_B ] = community.flopB();
                cards[ FLOP_C ] = community.flopC();
        }
        return cards;
    }

    private static EnumSet<Card> asSet(
            Hole hole, Community community)
    {
        EnumSet<Card> seq = EnumSet.of(hole.a(), hole.b());
        if (community.hasRiver()) {
            seq.add( community.river() );
        }
        if (community.hasTurn()) {
            seq.add( community.turn() );
        }
        if (community.hasFlop()) {
            seq.add( community.flopA() );
            seq.add( community.flopB() );
            seq.add( community.flopC() );
        }
        return seq;
    }

    
    //--------------------------------------------------------------------
    public static void main(String args[])
    {
        OddFinder oddFinder = new GeneralOddFinder();

        System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
        Odds oddsA =
                oddFinder.compute(
                    Hole.valueOf(Card.valueOf(TWO,   CLUBS),
                                 Card.valueOf(THREE, CLUBS)),
                    new Community(),
                    1);

        System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
        Odds oddsB =
                oddFinder.compute(
                    Hole.valueOf(Card.valueOf(TWO,   SPADES),
                                 Card.valueOf(THREE, SPADES)),
                    new Community(),
                    1);

        System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
        System.out.println( oddsA );
        System.out.println( oddsB );
    }
}
