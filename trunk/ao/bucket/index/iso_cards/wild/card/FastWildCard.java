package ao.bucket.index.iso_cards.wild.card;

import ao.bucket.index.iso_cards.wild.suit.WildSuit;
import static ao.bucket.index.iso_cards.wild.suit.WildSuit.*;
import ao.holdem.model.card.Rank;
import static ao.holdem.model.card.Rank.*;

/**
 * Date: Sep 11, 2008
 * Time: 8:51:04 PM
 */
public enum FastWildCard
{
    //--------------------------------------------------------------------
    TWO_OF_FIRST (TWO, FIRST),
    TWO_OF_SECOND(TWO, SECOND),
    TWO_OF_THIRD (TWO, THIRD),
    TWO_OF_FOURTH(TWO, FOURTH),
    TWO_OF_WILD  (TWO, WILD),

    THREE_OF_FIRST (THREE, FIRST),
    THREE_OF_SECOND(THREE, SECOND),
    THREE_OF_THIRD (THREE, THIRD),
    THREE_OF_FOURTH(THREE, FOURTH),
    THREE_OF_WILD  (THREE, WILD),

    FOUR_OF_FIRST (FOUR, FIRST),
    FOUR_OF_SECOND(FOUR, SECOND),
    FOUR_OF_THIRD (FOUR, THIRD),
    FOUR_OF_FOURTH(FOUR, FOURTH),
    FOUR_OF_WILD  (FOUR, WILD),

    FIVE_OF_FIRST (FIVE, FIRST),
    FIVE_OF_SECOND(FIVE, SECOND),
    FIVE_OF_THIRD (FIVE, THIRD),
    FIVE_OF_FOURTH(FIVE, FOURTH),
    FIVE_OF_WILD  (FIVE, WILD),

    SIX_OF_FIRST (SIX, FIRST),
    SIX_OF_SECOND(SIX, SECOND),
    SIX_OF_THIRD (SIX, THIRD),
    SIX_OF_FOURTH(SIX, FOURTH),
    SIX_OF_WILD  (SIX, WILD),

    SEVEN_OF_FIRST (SEVEN, FIRST),
    SEVEN_OF_SECOND(SEVEN, SECOND),
    SEVEN_OF_THIRD (SEVEN, THIRD),
    SEVEN_OF_FOURTH(SEVEN, FOURTH),
    SEVEN_OF_WILD  (SEVEN, WILD),

    EIGHT_OF_FIRST (EIGHT, FIRST),
    EIGHT_OF_SECOND(EIGHT, SECOND),
    EIGHT_OF_THIRD (EIGHT, THIRD),
    EIGHT_OF_FOURTH(EIGHT, FOURTH),
    EIGHT_OF_WILD  (EIGHT, WILD),

    NINE_OF_FIRST (NINE, FIRST),
    NINE_OF_SECOND(NINE, SECOND),
    NINE_OF_THIRD (NINE, THIRD),
    NINE_OF_FOURTH(NINE, FOURTH),
    NINE_OF_WILD  (NINE, WILD),

    TEN_OF_FIRST (TEN, FIRST),
    TEN_OF_SECOND(TEN, SECOND),
    TEN_OF_THIRD (TEN, THIRD),
    TEN_OF_FOURTH(TEN, FOURTH),
    TEN_OF_WILD  (TEN, WILD),

    JACK_OF_FIRST (JACK, FIRST),
    JACK_OF_SECOND(JACK, SECOND),
    JACK_OF_THIRD (JACK, THIRD),
    JACK_OF_FOURTH(JACK, FOURTH),
    JACK_OF_WILD  (JACK, WILD),

    QUEEN_OF_FIRST (QUEEN, FIRST),
    QUEEN_OF_SECOND(QUEEN, SECOND),
    QUEEN_OF_THIRD (QUEEN, THIRD),
    QUEEN_OF_FOURTH(QUEEN, FOURTH),
    QUEEN_OF_WILD  (QUEEN, WILD),

    KING_OF_FIRST (KING, FIRST),
    KING_OF_SECOND(KING, SECOND),
    KING_OF_THIRD (KING, THIRD),
    KING_OF_FOURTH(KING, FOURTH),
    KING_OF_WILD  (KING, WILD),

    ACE_OF_FIRST (ACE, FIRST),
    ACE_OF_SECOND(ACE, SECOND),
    ACE_OF_THIRD (ACE, THIRD),
    ACE_OF_FOURTH(ACE, FOURTH),
    ACE_OF_WILD  (ACE, WILD),
    ;

    public static final FastWildCard VALUES[] = values();

    private static final FastWildCard INDEX[][];
    static
    {
        INDEX = new FastWildCard[Rank.VALUES.length]
                                [WildSuit.VALUES.length];
        for (Rank rank : Rank.VALUES)
        {
            for (WildSuit suit : WildSuit.VALUES)
            {
                INDEX[rank.ordinal()]
                     [suit.ordinal()] =
                        computeValueOf(rank, suit);
            }
        }
    }
    public static FastWildCard valueOf(Rank rank, WildSuit suit)
    {
        return INDEX[ rank.ordinal() ][ suit.ordinal() ];
    }
    private static FastWildCard computeValueOf(
            Rank rank, WildSuit suit)
    {
        for (FastWildCard wildCard : VALUES)
        {
            if (wildCard.RANK == rank &&
                    wildCard.SUIT == suit)
            {
                return wildCard;
            }
        }
        return null;
    }


    //--------------------------------------------------------------------
    private final Rank     RANK;
    private final WildSuit SUIT;


    //--------------------------------------------------------------------
    private FastWildCard(Rank rank, WildSuit suit)
    {
        RANK = rank;
        SUIT = suit;
    }


    //--------------------------------------------------------------------
}
