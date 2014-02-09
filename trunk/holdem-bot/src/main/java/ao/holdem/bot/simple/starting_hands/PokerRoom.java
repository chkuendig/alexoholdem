package ao.holdem.bot.simple.starting_hands;

import static ao.holdem.model.card.Rank.*;
import ao.holdem.model.card.Hole;

/**
 * User: alex
 * Date: 6-Apr-2009
 * Time: 12:26:41 AM
 *
 * See http://en.wikipedia.org/wiki/Texas_hold_%27em_starting_hands
 *      #Statistics_based_on_real_play
 *
 */
public class PokerRoom
{
    //--------------------------------------------------------------------
    private PokerRoom() {}


    //--------------------------------------------------------------------
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

        // 2  	AQs, TT, AK, AJs, KQs, 99
        if (    hole.hasRanks(ACE, QUEEN) && hole.isSuited() ||
                hole.hasRanks(TEN, TEN)                    ||
                hole.hasRanks(ACE, JACK)  && hole.isSuited() ||
                hole.hasRanks(KING, QUEEN) && hole.isSuited() ||
                hole.hasRanks(NINE, NINE)                   ||
                hole.hasRanks(ACE, KING))
        {
            return 2;
        }

        // 3  	ATs, AQ, KJs, 88, KTs, QJs
        if (    hole.hasRanks(QUEEN, JACK) && hole.isSuited() ||
                hole.hasRanks(KING, JACK) && hole.isSuited() ||
                hole.hasRanks(ACE, TEN)  && hole.isSuited() ||
                hole.hasRanks(EIGHT, EIGHT)                 ||
                hole.hasRanks(KING, TEN)  && hole.isSuited() ||
                hole.hasRanks(ACE, QUEEN))
        {
            return 3;
        }

        // 4  	A9s, AJ, QTs, KQ, 77, JTs
        if (    hole.hasRanks(ACE, NINE)  && hole.isSuited() ||
                hole.hasRanks(QUEEN, TEN)   && hole.isSuited() ||
                hole.hasRanks(JACK, TEN)   && hole.isSuited() ||
                hole.hasRanks(KING, QUEEN)                  ||
                hole.hasRanks(SEVEN, SEVEN)                  ||
                hole.hasRanks(ACE, JACK))
        {
            return 4;
        }

        // 5  	A8s, K9s, AT, A5s, A7s
        if (    hole.hasRanks(ACE, EIGHT) && hole.isSuited() ||
                hole.hasRanks(KING, NINE)  && hole.isSuited() ||
                hole.hasRanks(ACE, TEN)                    ||
                hole.hasRanks(ACE, FIVE)  && hole.isSuited() ||
                hole.hasRanks(ACE, SEVEN) && hole.isSuited())
        {
            return 5;
        }

        // 6  	KJ, 66, T9s, A4s, Q9s
        if (    hole.hasRanks(KING, JACK)                  ||
                hole.hasRanks(SIX, SIX)                   ||
                hole.hasRanks(TEN, NINE) && hole.isSuited() ||
                hole.hasRanks(ACE, FOUR) && hole.isSuited() ||
                hole.hasRanks(QUEEN, NINE) && hole.isSuited())
        {
            return 6;
        }

        // 7  	J9s, QJ, A6s, 55, A3s, K8s, KT
        if (    hole.hasRanks(JACK, NINE)  && hole.isSuited() ||
                hole.hasRanks(QUEEN, JACK)                   ||
                hole.hasRanks(ACE, SIX)   && hole.isSuited() ||
                hole.hasRanks(FIVE, FIVE)                   ||
                hole.hasRanks(ACE, THREE) && hole.isSuited() ||
                hole.hasRanks(KING, EIGHT) && hole.isSuited() ||
                hole.hasRanks(KING, TEN))
        {
            return 7;
        }

        // 8  	98s, T8s, K7s, A2s
        if (    hole.hasRanks(NINE, EIGHT) && hole.isSuited() ||
                hole.hasRanks(TEN, EIGHT) && hole.isSuited() ||
                hole.hasRanks(KING, SEVEN) && hole.isSuited() ||
                hole.hasRanks(ACE, TWO)   && hole.isSuited())
        {
            return 8;
        }

        // 9  	87s, QT, Q8s, 44, A9, J8s, 76s, JT
        if (    hole.hasRanks(EIGHT, SEVEN) && hole.isSuited() ||
                hole.hasRanks(QUEEN, TEN)                    ||
                hole.hasRanks(QUEEN, EIGHT) && hole.isSuited() ||
                hole.hasRanks(FOUR, FOUR)                   ||
                hole.hasRanks(ACE, NINE)                   ||
                hole.hasRanks(JACK, EIGHT) && hole.isSuited() ||
                hole.hasRanks(SEVEN, SIX)   && hole.isSuited() ||
                hole.hasRanks(JACK, TEN))
        {
            return 9;
        }

        return 10;
    }
}
