package ao.holdem.model.card.sequence;

import ao.holdem.model.card.Card;
import ao.holdem.model.card.Community;
import ao.holdem.model.card.Hole;

import java.util.Arrays;

/**
 *
 */
public class CardSequence
{
    //--------------------------------------------------------------------
    private final Hole hole;
    private final Community community;


    //--------------------------------------------------------------------
    public CardSequence(Hole hole, Community community) {
        this.hole = hole;
        this.community = community;
    }

    public CardSequence(Hole hole) {
        this(hole, Community.PREFLOP);
    }


    //--------------------------------------------------------------------
    /**
     * @return hole cards corresponding to this player.
     */
    public Hole hole() {
        return hole;
    }


    /**
     * @return shared community cards for this stage of the hand.
     */
    public Community community() {
        return community;
    }


    //--------------------------------------------------------------------
    public Card[] knowCards() {
        Card asArr[] = {
                hole().a(),
                hole().b(),
                community().flopA(),
                community().flopB(),
                community().flopC(),
                community().turn(),
                community().river()};
        return Arrays.copyOf(
                asArr, 2 + community().knownCount());
    }





    //--------------------------------------------------------------------
    @Override public String toString()
    {
        return hole + " | " + community;
    }


    //--------------------------------------------------------------------
    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CardSequence that = (CardSequence) o;
        return !(community != null
                ? !community.equals(that.community())
                : that.community() != null) &&
                !(hole != null ? !hole.equals(that.hole()) : that.hole() != null);
    }

    @Override public int hashCode() {
        int result = hole != null ? hole.hashCode() : 0;
        result = 31 * result + (community != null ? community.hashCode() : 0);
        return result;
    }
}
