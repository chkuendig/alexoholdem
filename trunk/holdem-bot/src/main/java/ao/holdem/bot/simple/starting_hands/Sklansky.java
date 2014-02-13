package ao.holdem.bot.simple.starting_hands;

import ao.holdem.model.card.Hole;
import static ao.holdem.model.card.Rank.*;


/**
 *
 */
public enum Sklansky
{;
    //--------------------------------------------------------------------
    public static final int LOW  = 1;
    public static final int HIGH = 9;


    //--------------------------------------------------------------------
    // - what is an X card?
    // - if it doesn't say "suited", does that mean its unsuited,
    //                               or that suit doesn't matter?
    //      ^ doesn't actually matter, coz a suited variant would
    //          always be slurped up by a higher Sklansky group.

    public static int groupOf(Hole hole)
    {
        // 1  	AA, KK, QQ, JJ, AKs
        if (    hole.hasRanks(ACE, ACE)   ||
                hole.hasRanks(KING, KING)  ||
                hole.hasRanks(QUEEN, QUEEN) ||
                hole.hasRanks(JACK, JACK)  ||
                hole.hasRanks(ACE, KING)  && hole.isSuited())
        {
            return 1;
        }

        // 2  	TT, AQs, AJs, KQs, AK
        if (    hole.hasRanks(TEN, TEN)                    ||
                hole.hasRanks(ACE, QUEEN) && hole.isSuited() ||
                hole.hasRanks(ACE, JACK)  && hole.isSuited() ||
                hole.hasRanks(KING, QUEEN) && hole.isSuited() ||
                hole.hasRanks(ACE, KING))
        {
            return 2;
        }

        // 3  	99, JTs, QJs, KJs, ATs, AQ
        if (    hole.hasRanks(NINE, NINE)                  ||
                hole.hasRanks(JACK, TEN)  && hole.isSuited() ||
                hole.hasRanks(QUEEN, JACK) && hole.isSuited() ||
                hole.hasRanks(KING, JACK) && hole.isSuited() ||
                hole.hasRanks(ACE, TEN)  && hole.isSuited() ||
                hole.hasRanks(ACE, QUEEN))
        {
            return 3;
        }

        // 4  	T9s, KQ, 88, QTs, 98s, J9s, AJ, KTs
        if (    hole.hasRanks(TEN, NINE)  && hole.isSuited() ||
                hole.hasRanks(KING, QUEEN)                  ||
                hole.hasRanks(EIGHT, EIGHT)                  ||
                hole.hasRanks(QUEEN, TEN)   && hole.isSuited() ||
                hole.hasRanks(NINE, EIGHT) && hole.isSuited() ||
                hole.hasRanks(JACK, NINE)  && hole.isSuited() ||
                hole.hasRanks(ACE, JACK)                   ||
                hole.hasRanks(KING, TEN)   && hole.isSuited())
        {
            return 4;
        }

        // 5  	77, 87s, Q9s, T8s, KJ, QJ, JT, 76s, 97s, Axs, 65s
        if (    hole.hasRanks(SEVEN, SEVEN)                  ||
                hole.hasRanks(EIGHT, SEVEN) && hole.isSuited() ||
                hole.hasRanks(QUEEN, NINE)  && hole.isSuited() ||
                hole.hasRanks(TEN, EIGHT) && hole.isSuited() ||
                hole.hasRanks(KING, JACK)                   ||
                hole.hasRanks(QUEEN, JACK)                   ||
                hole.hasRanks(JACK, TEN)                    ||
                hole.hasRanks(SEVEN, SIX)   && hole.isSuited() ||
                hole.hasRanks(NINE, SEVEN) && hole.isSuited() ||
                hole.hasRanks(SIX, FIVE)    && hole.isSuited() ||
                hole.hasRank(ACE) && hole.hasXcard() && hole.isSuited())
        {
            return 5;
        }

        // 6  	66, AT, 55, 86s, KT, QT, 54s, K9s, J8s, 75s
        if (    hole.hasRanks(SIX, SIX)                    ||
                hole.hasRanks(ACE, TEN)                    ||
                hole.hasRanks(FIVE, FIVE)                   ||
                hole.hasRanks(EIGHT, SIX)   && hole.isSuited() ||
                hole.hasRanks(KING, TEN)                    ||
                hole.hasRanks(QUEEN, TEN)                    ||
                hole.hasRanks(FIVE, FOUR)  && hole.isSuited() ||
                hole.hasRanks(KING, NINE)  && hole.isSuited() ||
                hole.hasRanks(JACK, EIGHT) && hole.isSuited() ||
                hole.hasRanks(SEVEN, FIVE)  && hole.isSuited() )
        {
            return 6;
        }

        // 7  	44, J9, 64s, T9, 53s, 33, 98, 43s, 22, Kxs, T7s, Q8s
        if (    hole.hasRanks(FOUR, FOUR)                   ||
                hole.hasRanks(JACK, NINE)                   ||
                hole.hasRanks(SIX, FOUR) && hole.isSuited()  ||
                hole.hasRanks(TEN, NINE)                   ||
                hole.hasRanks(FIVE, THREE) && hole.isSuited() ||
                hole.hasRanks(THREE, THREE)                  ||
                hole.hasRanks(NINE, EIGHT)                  ||
                hole.hasRanks(FOUR, THREE) && hole.isSuited() ||
                hole.hasRanks(TWO, TWO)                    ||
                hole.hasRanks(TEN, SEVEN) && hole.isSuited() ||
                hole.hasRanks(QUEEN, EIGHT) && hole.isSuited() ||
                hole.hasRank(KING) && hole.hasXcard() && hole.isSuited())
        {
            return 7;
        }

        // 8  	87, A9, Q9, 76, 42s, 32s, 96s, 85s, J8, J7s, 65, 54,
        //          74s, K9, T8
        if (    hole.hasRanks(EIGHT, SEVEN)                  ||
                hole.hasRanks(ACE, NINE)                   ||
                hole.hasRanks(QUEEN, NINE)                   ||
                hole.hasRanks(SEVEN, SIX)                    ||
                hole.hasRanks(FOUR, TWO)   && hole.isSuited() ||
                hole.hasRanks(THREE, TWO)   && hole.isSuited() ||
                hole.hasRanks(NINE, SIX)   && hole.isSuited() ||
                hole.hasRanks(EIGHT, FIVE)  && hole.isSuited() ||
                hole.hasRanks(JACK, EIGHT)                  ||
                hole.hasRanks(JACK, SEVEN) && hole.isSuited() ||
                hole.hasRanks(SIX, FIVE)                   ||
                hole.hasRanks(FIVE, FOUR)                   ||
                hole.hasRanks(SEVEN, FOUR)  && hole.isSuited() ||
                hole.hasRanks(KING, NINE)                   ||
                hole.hasRanks(TEN, EIGHT))
        {
            return 8;
        }

        return 9;
    }
}
