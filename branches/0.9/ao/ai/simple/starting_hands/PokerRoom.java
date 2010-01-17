package ao.ai.simple.starting_hands;

import ao.holdem.model.card.Hole;
import static ao.holdem.model.card.Rank.*;

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
        if (    hole.ranks(ACE,   ACE)   ||
                hole.ranks(KING,  KING)  ||
                hole.ranks(QUEEN, QUEEN) ||
                hole.ranks(JACK,  JACK)  ||
                hole.ranks(ACE,   KING)  && hole.suited())
        {
            return 1;
        }

        // 2  	AQs, TT, AK, AJs, KQs, 99
        if (    hole.ranks(ACE,  QUEEN) && hole.suited() ||
                hole.ranks(TEN,  TEN)                    ||
                hole.ranks(ACE,  JACK)  && hole.suited() ||
                hole.ranks(KING, QUEEN) && hole.suited() ||
                hole.ranks(NINE, NINE)                   ||
                hole.ranks(ACE,  KING))
        {
            return 2;
        }

        // 3  	ATs, AQ, KJs, 88, KTs, QJs
        if (    hole.ranks(QUEEN, JACK) && hole.suited() ||
                hole.ranks(KING,  JACK) && hole.suited() ||
                hole.ranks(ACE,   TEN)  && hole.suited() ||
                hole.ranks(EIGHT, EIGHT)                 ||
                hole.ranks(KING,  TEN)  && hole.suited() ||
                hole.ranks(ACE,   QUEEN))
        {
            return 3;
        }

        // 4  	A9s, AJ, QTs, KQ, 77, JTs
        if (    hole.ranks(ACE,   NINE)  && hole.suited() ||
                hole.ranks(QUEEN, TEN)   && hole.suited() ||
                hole.ranks(JACK,  TEN)   && hole.suited() ||
                hole.ranks(KING,  QUEEN)                  ||
                hole.ranks(SEVEN, SEVEN)                  ||
                hole.ranks(ACE,   JACK))
        {
            return 4;
        }

        // 5  	A8s, K9s, AT, A5s, A7s
        if (    hole.ranks(ACE,  EIGHT) && hole.suited() ||
                hole.ranks(KING, NINE)  && hole.suited() ||
                hole.ranks(ACE,  TEN)                    ||
                hole.ranks(ACE,  FIVE)  && hole.suited() ||
                hole.ranks(ACE,  SEVEN) && hole.suited())
        {
            return 5;
        }

        // 6  	KJ, 66, T9s, A4s, Q9s
        if (    hole.ranks(KING,  JACK)                  ||
                hole.ranks(SIX,   SIX)                   ||
                hole.ranks(TEN,   NINE) && hole.suited() ||
                hole.ranks(ACE,   FOUR) && hole.suited() ||
                hole.ranks(QUEEN, NINE) && hole.suited())
        {
            return 6;
        }

        // 7  	J9s, QJ, A6s, 55, A3s, K8s, KT
        if (    hole.ranks(JACK,  NINE)  && hole.suited() ||
                hole.ranks(QUEEN, JACK)                   ||
                hole.ranks(ACE,   SIX)   && hole.suited() ||
                hole.ranks(FIVE,  FIVE)                   ||
                hole.ranks(ACE,   THREE) && hole.suited() ||
                hole.ranks(KING,  EIGHT) && hole.suited() ||
                hole.ranks(KING,  TEN))
        {
            return 7;
        }

        // 8  	98s, T8s, K7s, A2s
        if (    hole.ranks(NINE, EIGHT) && hole.suited() ||
                hole.ranks(TEN,  EIGHT) && hole.suited() ||
                hole.ranks(KING, SEVEN) && hole.suited() ||
                hole.ranks(ACE,  TWO)   && hole.suited())
        {
            return 8;
        }

        // 9  	87s, QT, Q8s, 44, A9, J8s, 76s, JT
        if (    hole.ranks(EIGHT, SEVEN) && hole.suited() ||
                hole.ranks(QUEEN, TEN)                    ||
                hole.ranks(QUEEN, EIGHT) && hole.suited() ||
                hole.ranks(FOUR,  FOUR)                   ||
                hole.ranks(ACE,   NINE)                   ||
                hole.ranks(JACK,  EIGHT) && hole.suited() ||
                hole.ranks(SEVEN, SIX)   && hole.suited() ||
                hole.ranks(JACK,  TEN))
        {
            return 9;
        }

        return 10;
    }
}
