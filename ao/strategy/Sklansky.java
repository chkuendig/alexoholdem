package ao.strategy;

import static ao.holdem.model.card.Card.Rank.*;
import ao.holdem.model.card.Hole;

/**
 *
 */
public class Sklansky
{
    //--------------------------------------------------------------------
    private Sklansky() {}


    //--------------------------------------------------------------------
    // - what is an X card?
    // - if it doesn't say "suited", does that mean its unsuited,
    //                               or that suit doesn't matter?

    public static int groupOf(Hole hole)
    {
        // 1  	AA, KK, QQ, JJ, AKs
        if (    hole.ranks(ACE, ACE) ||
                hole.ranks(KING, KING) ||
                hole.ranks(QUEEN, QUEEN) ||
                hole.ranks(JACK, JACK) ||
                hole.ranks(ACE, KING) && hole.suited())
        {
            return 1;
        }

        // 2  	TT, AQs, AJs, KQs, AK
        if (    hole.ranks(TEN, TEN) ||
                hole.ranks(ACE, QUEEN) && hole.suited() ||
                hole.ranks(ACE, JACK) && hole.suited() ||
                hole.ranks(KING, QUEEN) && hole.suited() ||
                hole.ranks(ACE, KING))
        {
            return 2;
        }

        // 3  	99, JTs, QJs, KJs, ATs, AQ
        if (    hole.ranks(NINE, NINE) ||
                hole.ranks(JACK, TEN) && hole.suited() ||
                hole.ranks(QUEEN, JACK) && hole.suited() ||
                hole.ranks(KING, JACK) && hole.suited() ||
                hole.ranks(ACE, TEN) && hole.suited() ||
                hole.ranks(ACE, QUEEN))
        {
            return 3;
        }

        // 4  	T9s, KQ, 88, QTs, 98s, J9s, AJ, KTs
        if (    hole.ranks(TEN, NINE) && hole.suited() ||
                hole.ranks(KING, QUEEN) ||
                hole.ranks(EIGHT, EIGHT) ||
                hole.ranks(QUEEN, TEN) && hole.suited() ||
                hole.ranks(NINE, EIGHT) && hole.suited() ||
                hole.ranks(JACK, NINE) && hole.suited() ||
                hole.ranks(ACE, JACK) ||
                hole.ranks(KING, TEN) && hole.suited())
        {
            return 4;
        }

        // 5  	77, 87s, Q9s, T8s, KJ, QJ, JT, 76s, 97s, Axs, 65s
        if (    hole.ranks(SEVEN, SEVEN) ||
                hole.ranks(EIGHT, SEVEN) && hole.suited() ||
                hole.ranks(QUEEN, NINE) && hole.suited() ||
                hole.ranks(TEN, EIGHT) && hole.suited() ||
                hole.ranks(KING, JACK) ||
                hole.ranks(QUEEN, JACK) ||
                hole.ranks(JACK, TEN) ||
                hole.ranks(SEVEN, SIX) && hole.suited() ||
                hole.ranks(NINE, SEVEN) && hole.suited() ||
                hole.ranks(ACE) && hole.hasXcard() && hole.suited() ||
                hole.ranks(SIX, FIVE) && hole.suited())
        {
            return 5;
        }

        // 6  	66, AT, 55, 86s, KT, QT, 54s, K9s, J8s, 75s
        if (    hole.ranks(SIX, SIX) ||
                hole.ranks(ACE, TEN) ||
                hole.ranks(FIVE, FIVE) ||
                hole.ranks(EIGHT, SIX) && hole.suited() ||
                hole.ranks(KING, TEN) ||
                hole.ranks(QUEEN, TEN) ||
                hole.ranks(FIVE, FOUR) && hole.suited() ||
                hole.ranks(KING, NINE) && hole.suited() ||
                hole.ranks(JACK, EIGHT) && hole.suited() ||
                hole.ranks(SEVEN, FIVE) && hole.suited() )
        {
            return 6;
        }

        // 7  	44, J9, 64s, T9, 53s, 33, 98, 43s, 22, Kxs, T7s, Q8s
        if (    hole.ranks(FOUR, FOUR) ||
                hole.ranks(JACK, NINE) ||
                hole.ranks(SIX, FOUR) && hole.suited() ||
                hole.ranks(TEN, NINE) ||
                hole.ranks(FIVE, THREE) && hole.suited() ||
                hole.ranks(THREE, THREE) ||
                hole.ranks(NINE, EIGHT) ||
                hole.ranks(FOUR, THREE) && hole.suited() ||
                hole.ranks(TWO, TWO) ||
                hole.ranks(KING) && hole.hasXcard() && hole.suited() ||
                hole.ranks(TEN, SEVEN) && hole.suited() ||
                hole.ranks(QUEEN, EIGHT) && hole.suited())
        {
            return 7;
        }

        // 8  	87, A9, Q9, 76, 42s, 32s, 96s, 85s, J8, J7s, 65, 54, 74s, K9, T8
        if (    hole.ranks(EIGHT, SEVEN) ||
                hole.ranks(ACE, NINE) ||
                hole.ranks(QUEEN, NINE) ||
                hole.ranks(SEVEN, SIX) ||
                hole.ranks(FOUR, TWO) && hole.suited() ||
                hole.ranks(THREE, TWO) && hole.suited() ||
                hole.ranks(NINE, SIX) && hole.suited() ||
                hole.ranks(EIGHT, FIVE) && hole.suited() ||
                hole.ranks(JACK, EIGHT) ||
                hole.ranks(JACK, SEVEN) && hole.suited() ||
                hole.ranks(SIX, FIVE) ||
                hole.ranks(FIVE, FOUR) ||
                hole.ranks(SEVEN, FOUR) && hole.suited() ||
                hole.ranks(KING, NINE) ||
                hole.ranks(TEN, EIGHT))
        {
            return 8;
        }

        return 9;
    }
}
