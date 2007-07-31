package ao.holdem.bots.opp_model.decision;

import java.util.*;


/**
 * Properties based on which to make predictions:
 *
 * Immedate pot odds.
 * Bet Ratio: bets/(bets+calls).
 * Pot Ratio: amount_in / pot_size.
 * Last-Bets-To-Call > 0
 * Last-Action == BET/RAISE
 *
 * Ace on Board
 * King on Board
 * (#AKQ on Board) / (# Board Cards)
 * 
 * Number of opponents.
 * Number of active opponents.
 * Number of unacted opponents.
 * Position (in card reciept order).
 * Position within active opponents.
 * Betting round.
 * Remaining bets in round.
 * Pot size.
 * Stakes.
 * Money to call.
 * Previouse action (0..4).
 * Round of previouse actions (0..4).
 * Hand strength (at start of each betting round, as per assumed cards).
 * 
 */
public class CategorySet
{
    //--------------------------------------------------------------------
    private Map<Attribute, SplitPoint> splitPoints =
            new HashMap<Attribute, SplitPoint>();

    
    //--------------------------------------------------------------------
    @SuppressWarnings("unchecked")
    public Collection<Set<SplitPoint>>
            availAttributes(Collection<Attribute> forAttributes)
    {
        Collection<Set<SplitPoint>> availAttrs =
                new ArrayList<Set<SplitPoint>>();
        for (Attribute attr : forAttributes)
        {
            availAttrs.add(
                    attr.splitGiven( this ));
        }
        return availAttrs;
    }


    //--------------------------------------------------------------------
    public void set(SplitPoint point)
    {
        splitPoints.put( point.attribute(), point );
    }



    //--------------------------------------------------------------------
    @SuppressWarnings("unchecked")
    public <T extends SplitPoint<T>> T valueOf(Attribute<T> attr)
    {
        return (T)splitPoints.get( attr );
    }

}
