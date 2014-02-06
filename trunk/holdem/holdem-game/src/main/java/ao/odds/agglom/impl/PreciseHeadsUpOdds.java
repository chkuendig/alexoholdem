package ao.odds.agglom.impl;

import ao.bucket.index.detail.preflop.HoleOdds;
import ao.bucket.index.detail.preflop.HoleOddsDao;
import ao.holdem.model.card.Card;
import ao.holdem.model.card.Community;
import ao.holdem.model.card.Hole;
import ao.holdem.model.card.chance.Deck;
import ao.odds.agglom.OddFinder;
import ao.odds.agglom.Odds;
import ao.odds.eval.eval7.Eval7Faster;
import org.apache.log4j.Logger;

import java.util.EnumSet;

import static ao.util.data.Arrs.swap;


/**
 * matches with:
 *  http://wizardofodds.com/holdem/2players.html
 * with constant factor of 4.
 */
public class PreciseHeadsUpOdds implements OddFinder
{
    //--------------------------------------------------------------------
    private static final Logger LOG =
        Logger.getLogger(PreciseHeadsUpOdds.class);


    //--------------------------------------------------------------------
    private static final int HOLE_A = 51 - 1,
                             HOLE_B = 51,

                             FLOP_A = 51 - 4,
                             FLOP_B = 51 - 3,
                             FLOP_C = 51 - 2,

                             TURN   = 51 - 5,

                             RIVER  = 51 - 6,

//                             OPP_A  = 51 - 8,
                             OPP_B  = 51 - 7;


    //--------------------------------------------------------------------
    public static PreciseHeadsUpOdds INSTANCE = new PreciseHeadsUpOdds();


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
        if (community.equals( Community.PREFLOP ) &&
                HoleOddsDao.isMemoized())
        {
            return HoleOdds.lookup(
                        hole.asCanon().canonIndex());
        }

        Card cards[] = initKnownCardsToEnd(hole, community);
        return rollOutCommunityAndOpponent(
                cards,
                community.knownCount());
    }


    //--------------------------------------------------------------------
    private static Odds rollOutCommunityAndOpponent(
            Card cards[],
            int  knownCount)
    {
        return   knownCount == 0
               ? rollOutFlopTurnRiver(cards)
               : knownCount == 3
               ? rollOutTurnRiver(cards)
               : knownCount == 4
               ? rollOutRiver(cards)
               : rollOutOpp(cards);
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

    private static Odds rollOutOpp(Card cards[])
    {
        int   shortcut =
            Eval7Faster.shortcutFor(
                    cards[ FLOP_A ],
                    cards[ FLOP_B ],
                    cards[ FLOP_C ],
                    cards[ TURN   ],
                    cards[ RIVER  ]);

        short thisVal  =
            Eval7Faster.fastValueOf(
                    shortcut,
                    cards[ HOLE_A ], cards[ HOLE_B ]);

        return rollOutOpp(cards, shortcut, thisVal);
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
        OddFinder oddFinder = new PreciseHeadsUpOdds();

        long      time      = 0;
        int       warmup    = 20000;
        int       trials    = 100000;

        Deck d = new Deck();
        for (int i = (warmup + trials); i > 0; i--)
        {
            if (d.cardsDealt() > 40) d = new Deck();

            long before = System.currentTimeMillis();
            oddFinder.compute(
                    d.nextHole(),
                    new Community(
                            d.nextCard(), d.nextCard(), d.nextCard(),
                            d.nextCard(), d.nextCard()), 1);

            if (i > warmup) {
                time += (System.currentTimeMillis() - before);
            }
        }

        LOG.info(time + " " + ((double) time / trials));

//        System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
//        Odds oddsA =
//                oddFinder.compute(
//                    Hole.valueOf(Card.valueOf(ACE,   CLUBS),
//                                 Card.valueOf(THREE, CLUBS)),
//                    new Community(),
//                    1);
//
//        System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
//        Odds oddsB =
//                oddFinder.compute(
//                    Hole.valueOf(Card.valueOf(TWO,   SPADES),
//                                 Card.valueOf(THREE, SPADES)),
//                    new Community(),
//                    1);
//
//        System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
//        System.out.println( oddsA );
//        System.out.println( oddsB );
    }
}
