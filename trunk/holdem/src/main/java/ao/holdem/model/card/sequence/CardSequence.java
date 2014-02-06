package ao.holdem.model.card.sequence;

import ao.holdem.model.card.Card;
import ao.holdem.model.card.Community;
import ao.holdem.model.card.Hole;

import java.util.Arrays;

/**
 *
 */
public interface CardSequence
{
    //--------------------------------------------------------------------
    /**
     * @return hole cards corresponding to this player.
     */
    Hole hole();


    /**
     * @return shared community cards for this stage of the hand.
     */
    Community community();


    //--------------------------------------------------------------------
    public static class Util
    {
        //----------------------------------------------------------------
        private Util() {}


        //----------------------------------------------------------------
        public static Card[] knowCards(CardSequence seq) {
            Card asArr[] = {
                         seq.hole().a(),
                         seq.hole().b(),
                         seq.community().flopA(),
                         seq.community().flopB(),
                         seq.community().flopC(),
                         seq.community().turn(),
                         seq.community().river()};
            return Arrays.copyOf(
                    asArr, 2 + seq.community().knownCount());
        }
    }
}
