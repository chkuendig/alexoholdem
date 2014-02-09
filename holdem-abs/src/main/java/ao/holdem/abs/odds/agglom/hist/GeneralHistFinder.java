package ao.holdem.abs.odds.agglom.hist;

import ao.holdem.canon.flop.Flop;
import ao.holdem.canon.turn.Turn;
import ao.holdem.model.card.Card;
import ao.holdem.model.card.Community;
import ao.holdem.model.card.Hole;
import ao.holdem.model.card.sequence.CardSequence;
import ao.holdem.abs.odds.eval.eval7.Eval7Faster;

import java.util.EnumSet;

/**
 * Date: Sep 30, 2008
 * Time: 12:29:40 PM
 */
public class GeneralHistFinder implements HistFinder
{
    //--------------------------------------------------------------------
    private static final int HOLE_A = 51 - 1,
                             HOLE_B = 51,

                             FLOP_A = 51 - 4,
                             FLOP_B = 51 - 3,
                             FLOP_C = 51 - 2,

                             TURN   = 51 - 5,

                             RIVER  = 51 - 6;

    public static final GeneralHistFinder INSTANCE =
            new GeneralHistFinder();


    //--------------------------------------------------------------------
    public StrengthHist compute(Hole      hole,
                                Community community,
                                int       activeOpponents)
    {
        assert activeOpponents == 1 : "must be heads up";
        return compute(hole, community);
    }

    public StrengthHist compute(CardSequence cards, int activeOpponents)
    {
        return compute(cards.hole(), cards.community(), activeOpponents);
    }

    public StrengthHist compute(CardSequence cards)
    {
        return compute(cards.hole(), cards.community());
    }


    public StrengthHist compute(Hole      hole,
                                Community community)
    {

        Card cards[] = initKnownCardsToEnd(hole, community);
        return rollOutCommunity(
                cards,
                community.knownCount());
    }

//    public StrengthHist compute(CanonHole hole)
//    {
//        return compute(hole.reify(), Community.PREFLOP);
//    }
    public StrengthHist compute(Flop flop)
    {
        return compute(flop.hole().reify(), flop.community());
    }
    public StrengthHist compute(Turn turn)
    {
        return compute(turn.hole().reify(),
                       turn.community());
    }


    //--------------------------------------------------------------------
    private static StrengthHist rollOutCommunity(
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
    private static StrengthHist rollOutFlopTurnRiver(Card cards[])
    {
        int holeShortcut = Eval7Faster.shortcutFor(
                                cards[ HOLE_A ], cards[ HOLE_B ]);

        StrengthHist odds = new StrengthHist();
        for (int flopIndexC = 4; flopIndexC <= FLOP_C; flopIndexC++)
        {
            int flopShortcutC = Eval7Faster.nextShortcut(
                                    holeShortcut, cards[ flopIndexC ]);

            for (int flopIndexB = 3;
                     flopIndexB < flopIndexC; flopIndexB++)
            {
                int flopShortcutB = Eval7Faster.nextShortcut(
                        flopShortcutC, cards[ flopIndexB ]);

                for (int flopIndexA = 2;
                         flopIndexA < flopIndexB; flopIndexA++)
                {
                    int flopShortcutA = Eval7Faster.nextShortcut(
                            flopShortcutB, cards[ flopIndexA ]);

                    for (int turnIndex = 1;
                             turnIndex < flopIndexA; turnIndex++)
                    {
                        int turnShortcut = Eval7Faster.nextShortcut(
                            flopShortcutA, cards[ turnIndex ]);

                        for (int riverIndex = 0;
                                 riverIndex < turnIndex; riverIndex++)
                        {
                            Card river = cards[ riverIndex ];

                            short val  =
                                    Eval7Faster.fastValueOf(
                                            turnShortcut, river);
                            odds.count(val);
                        }
                    }
                }
            }
        }
        return odds;
    }


    //--------------------------------------------------------------------
    private static StrengthHist rollOutTurnRiver(Card cards[])
    {
        int flopShortcut = Eval7Faster.shortcutFor(
                cards[ HOLE_A ], cards[ HOLE_B ],
                cards[ FLOP_A ], cards[ FLOP_B ], cards[ FLOP_C ]);

        StrengthHist odds = new StrengthHist();
        for (int turnIndex =  1;
                 turnIndex <= TURN;
                 turnIndex++)
        {
            int  turnShortcut = Eval7Faster.nextShortcut(
                                    flopShortcut, cards[ turnIndex ]);
            for (int riverIndex = 0;
                     riverIndex < turnIndex;
                     riverIndex++)
            {
                Card riverCard     = cards[ riverIndex ];
                short val  =
                    Eval7Faster.fastValueOf(
                            turnShortcut, riverCard);
                odds.count( val );
            }
        }
        return odds;
    }


    //--------------------------------------------------------------------
    private static StrengthHist rollOutRiver(
            Card  cards[])
    {
        int turnShorcut = Eval7Faster.shortcutFor(
                cards[ HOLE_A ], cards[ HOLE_B ],
                cards[ FLOP_A ], cards[ FLOP_B ], cards[ FLOP_C ],
                cards[ TURN ]);

        StrengthHist odds = new StrengthHist();
        for (int riverIndex = 0;
                 riverIndex <= RIVER;
                 riverIndex++)
        {
            short val  =
                Eval7Faster.fastValueOf(
                    turnShorcut, cards[ riverIndex ]);
            odds.count( val );
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
}
