package ao.holdem.canon.card;

import ao.holdem.model.card.Rank;

import static ao.holdem.canon.card.CanonSuit.*;
import static ao.holdem.model.card.Rank.*;

/**
 * Date: Sep 11, 2008
 * Time: 8:51:04 PM
 *
 * Note: MUST be in [suit -> rank] order!!
 */
public enum CanonCard
{
    //--------------------------------------------------------------------
    TWO_OF_FIRST  (TWO,   FIRST),
    THREE_OF_FIRST(THREE, FIRST),
    FOUR_OF_FIRST (FOUR,  FIRST),
    FIVE_OF_FIRST (FIVE,  FIRST),
    SIX_OF_FIRST  (SIX,   FIRST),
    SEVEN_OF_FIRST(SEVEN, FIRST),
    EIGHT_OF_FIRST(EIGHT, FIRST),
    NINE_OF_FIRST (NINE,  FIRST),
    TEN_OF_FIRST  (TEN,   FIRST),
    JACK_OF_FIRST (JACK,  FIRST),
    QUEEN_OF_FIRST(QUEEN, FIRST),
    KING_OF_FIRST (KING,  FIRST),
    ACE_OF_FIRST  (ACE,   FIRST),

    TWO_OF_SECOND  (TWO,   SECOND),
    THREE_OF_SECOND(THREE, SECOND),
    FOUR_OF_SECOND (FOUR,  SECOND),
    FIVE_OF_SECOND (FIVE,  SECOND),
    SIX_OF_SECOND  (SIX,   SECOND),
    SEVEN_OF_SECOND(SEVEN, SECOND),
    EIGHT_OF_SECOND(EIGHT, SECOND),
    NINE_OF_SECOND (NINE,  SECOND),
    TEN_OF_SECOND  (TEN,   SECOND),
    JACK_OF_SECOND (JACK,  SECOND),
    QUEEN_OF_SECOND(QUEEN, SECOND),
    KING_OF_SECOND (KING,  SECOND),
    ACE_OF_SECOND  (ACE,   SECOND),

    TWO_OF_THIRD  (TWO,   THIRD),
    THREE_OF_THIRD(THREE, THIRD),
    FOUR_OF_THIRD (FOUR,  THIRD),
    FIVE_OF_THIRD (FIVE,  THIRD),
    SIX_OF_THIRD  (SIX,   THIRD),
    SEVEN_OF_THIRD(SEVEN, THIRD),
    EIGHT_OF_THIRD(EIGHT, THIRD),
    NINE_OF_THIRD (NINE,  THIRD),
    TEN_OF_THIRD  (TEN,   THIRD),
    JACK_OF_THIRD (JACK,  THIRD),
    QUEEN_OF_THIRD(QUEEN, THIRD),
    KING_OF_THIRD (KING,  THIRD),
    ACE_OF_THIRD  (ACE,   THIRD),

    TWO_OF_FOURTH  (TWO,   FOURTH),
    THREE_OF_FOURTH(THREE, FOURTH),
    FOUR_OF_FOURTH (FOUR,  FOURTH),
    FIVE_OF_FOURTH (FIVE,  FOURTH),
    SIX_OF_FOURTH  (SIX,   FOURTH),
    SEVEN_OF_FOURTH(SEVEN, FOURTH),
    EIGHT_OF_FOURTH(EIGHT, FOURTH),
    NINE_OF_FOURTH (NINE,  FOURTH),
    TEN_OF_FOURTH  (TEN,   FOURTH),
    JACK_OF_FOURTH (JACK,  FOURTH),
    QUEEN_OF_FOURTH(QUEEN, FOURTH),
    KING_OF_FOURTH (KING,  FOURTH),
    ACE_OF_FOURTH  (ACE,   FOURTH),

      TWO_OF_WILD(  TWO, WILD),
    THREE_OF_WILD(THREE, WILD),
     FOUR_OF_WILD( FOUR, WILD),
     FIVE_OF_WILD( FIVE, WILD),
      SIX_OF_WILD(  SIX, WILD),
    SEVEN_OF_WILD(SEVEN, WILD),
    EIGHT_OF_WILD(EIGHT, WILD),
     NINE_OF_WILD( NINE, WILD),
      TEN_OF_WILD(  TEN, WILD),
     JACK_OF_WILD( JACK, WILD),
    QUEEN_OF_WILD(QUEEN, WILD),
     KING_OF_WILD( KING, WILD),
      ACE_OF_WILD(  ACE, WILD),
    ;

    public static final CanonCard VALUES[] = values();

    private static final CanonCard INDEX[][];
    static
    {
        INDEX = new CanonCard[      Rank.VALUES.length ]
                             [ CanonSuit.VALUES.length ];
        for (Rank rank : Rank.VALUES)
        {
            for (CanonSuit suit : CanonSuit.VALUES)
            {
                INDEX[ rank.ordinal() ]
                     [ suit.ordinal() ] =
                        computeValueOf(rank, suit);
            }
        }
    }
    public static CanonCard valueOf(Rank rank, CanonSuit suit)
    {
        return INDEX[ rank.ordinal() ][ suit.ordinal() ];
    }
    private static CanonCard computeValueOf(
            Rank rank, CanonSuit suit)
    {
        for (CanonCard wildCard : VALUES)
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
    private final Rank RANK;
    private final CanonSuit SUIT;


    //--------------------------------------------------------------------
    private CanonCard(Rank rank, CanonSuit suit)
    {
        RANK = rank;
        SUIT = suit;
    }


    //--------------------------------------------------------------------
    public Rank rank()
    {
        return RANK;
    }

    public CanonSuit suit()
    {
        return SUIT;
    }


    //--------------------------------------------------------------------
    public boolean isWild()
    {
        return SUIT == WILD;
    }
}
