package ao.holdem.engine.eval.odds;

import ao.holdem.engine.eval.EvalBy5;
import ao.holdem.model.card.Card;
import ao.holdem.model.card.Community;
import ao.holdem.model.card.Hole;
import ao.holdem.model.card.sequence.CardSequence;

import java.util.EnumSet;
import java.util.Random;

/**
 * Approximate odds finder using EvalBy5
 */
public enum OddsBy5 implements OddsEvaluator
{
    INSTANCE;

    private static final Random RANDOM = new Random();

    private static final int FLOP_ITERATIONS = 10000;
    private static final int TURN_ITERATIONS = 5000;

    private static final int HOLE_A_INDEX = Card.COUNT - 1;
    private static final int HOLE_B_INDEX = Card.COUNT - 2;

    private static final int FLOP_A_INDEX = Card.COUNT - 3;
//    private static final int FLOP_A_RANGE = FLOP_A_INDEX + 1;

    private static final int FLOP_B_INDEX = Card.COUNT - 4;
//    private static final int FLOP_B_RANGE = FLOP_B_INDEX + 1;

    private static final int FLOP_C_INDEX = Card.COUNT - 5;
//    private static final int FLOP_C_RANGE = FLOP_C_INDEX + 1;

    private static final int TURN_INDEX = Card.COUNT - 6;
    private static final int TURN_RANGE = TURN_INDEX + 1;

    private static final int RIVER_INDEX = Card.COUNT - 7;
    private static final int RIVER_RANGE = RIVER_INDEX + 1;

    private static final int OPP_A_INDEX = Card.COUNT - 8;
    private static final int OPP_A_RANGE = OPP_A_INDEX + 1;

    private static final int OPP_B_INDEX = Card.COUNT - 9;
    private static final int OPP_B_RANGE = OPP_B_INDEX + 1;


    public double approximateHeadsUpHandStrength(CardSequence cards)
    {
        if (cards.community().isPreflop()) {
            return HeadsUpPreflopOdds.handStrength(cards.hole());
        }

        Card[] buffer = initKnownCardsToEnd(cards.hole(), cards.community());

        switch (cards.community().knownCount())
        {
            case 3:
                return flopHandStrength(buffer);

            case 4:
                return turnHandStrength(buffer);

            case 5:
                return riverHandStrength(buffer);

            default:
                throw new Error();
        }
    }


    //--------------------------------------------------------------------
    private static double flopHandStrength(Card[] buffer) {
        double sum = 0;

        int iteration = 1;
        for (; iteration <= FLOP_ITERATIONS; iteration++) {
            swap(buffer, RANDOM.nextInt(TURN_RANGE), TURN_INDEX);
            swap(buffer, RANDOM.nextInt(RIVER_RANGE), RIVER_INDEX);

            sum += sampleVsOpponent(buffer);
        }

        return sum / iteration;
    }


    //--------------------------------------------------------------------
    private static double turnHandStrength(Card[] buffer) {
        double sum = 0;

        int iteration = 1;
        for (; iteration <= TURN_ITERATIONS; iteration++) {
            swap(buffer, RANDOM.nextInt(RIVER_RANGE), RIVER_INDEX);

            sum += sampleVsOpponent(buffer);
        }

        return sum / iteration;
    }


    private static double sampleVsOpponent(Card[] buffer) {
        swap(buffer, RANDOM.nextInt(OPP_A_RANGE), OPP_A_INDEX);
        swap(buffer, RANDOM.nextInt(OPP_B_RANGE), OPP_B_INDEX);

        short value = EvalBy5.valueOf(
                buffer[HOLE_A_INDEX], buffer[HOLE_B_INDEX],
                buffer[FLOP_A_INDEX], buffer[FLOP_B_INDEX], buffer[FLOP_C_INDEX],
                buffer[TURN_INDEX], buffer[RIVER_INDEX]);

        short oppValue = EvalBy5.valueOf(
                buffer[OPP_A_INDEX], buffer[OPP_B_INDEX],
                buffer[FLOP_A_INDEX], buffer[FLOP_B_INDEX], buffer[FLOP_C_INDEX],
                buffer[TURN_INDEX], buffer[RIVER_INDEX]);

        return (value > oppValue) ? 1 :
                (value < oppValue) ? 0 : 0.5;
    }



    //--------------------------------------------------------------------
    private static double riverHandStrength(Card[] buffer) {
        short value = EvalBy5.valueOf(
                buffer[HOLE_A_INDEX], buffer[HOLE_B_INDEX],
                buffer[FLOP_A_INDEX], buffer[FLOP_B_INDEX], buffer[FLOP_C_INDEX],
                buffer[TURN_INDEX], buffer[RIVER_INDEX]);

        double sum = 0;
        int iteration = 0;
        for (int a = 0; a < OPP_A_RANGE; a++) {
            for (int b = 0; b < a; b++) {
                short oppValue = EvalBy5.valueOf(
                        buffer[a], buffer[b],
                        buffer[FLOP_A_INDEX], buffer[FLOP_B_INDEX], buffer[FLOP_C_INDEX],
                        buffer[TURN_INDEX], buffer[RIVER_INDEX]);

                double delta =
                        (value > oppValue) ? 1 :
                        (value < oppValue) ? 0 : 0.5;

                sum += delta;
                iteration++;
            }
        }

        return sum / iteration;
    }


    //--------------------------------------------------------------------
    public static Card[] initKnownCardsToEnd(
            Hole hole, Community community)
    {
        EnumSet<Card> known = asSet(hole, community);

        int index = 0;
        Card[] cards = new Card[ Card.VALUES.length ];
        for (Card card : Card.VALUES) {
            if (! known.contains(card)) {
                cards[ index++ ] = card;
            }
        }

        cards[ HOLE_A_INDEX ] = hole.a();
        cards[ HOLE_B_INDEX ] = hole.b();
        switch (community.knownCount())
        {
            case 5:
                cards[ RIVER_INDEX ] = community.river();

            case 4:
                cards[ TURN_INDEX ] = community.turn();

            case 3:
                cards[ FLOP_A_INDEX ] = community.flopA();
                cards[ FLOP_B_INDEX ] = community.flopB();
                cards[ FLOP_C_INDEX ] = community.flopC();
        }
        return cards;
    }

    private static EnumSet<Card> asSet(
            Hole hole, Community community)
    {
        EnumSet<Card> seq = EnumSet.of(hole.a(), hole.b());
        seq.addAll(community.toSet());
        return seq;
    }


    private static void swap(Card[] cards, int indexA, int indexB) {
        Card temp = cards[indexA];
        cards[indexA] = cards[indexB];
        cards[indexB] = temp;
    }
}
