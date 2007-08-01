package ao.holdem.bots.opp_model.decision;

import java.util.*;


/**
 * Properties based on which to make predictions:
 *
 * Immedate pot odds. (20/2)
 * Bet Ratio: bets/(bets+calls). (20/2)
 * Pot Ratio: amount_in / pot_size. (20/2)
 * Last-Bets-To-Call > 0. (2)
 * Last-Action == BET/RAISE (2)
 *
 * Ace on Board (2)
 * King on Board (2)
 * (#AKQ on Board) / (# Board Cards). (20/2)
 * 
 * Number of opponents. (1..9 = 9)
 * Nplaumber of active opponents. (1..9 = 9)
 * Number of unacted opponents. (1..9 = 9)
 * Position (in card reciept order). (0..9 = 10)
 * Position within active opponents. (0..9 = 10)
 * Betting round. (4)
 * Remaining bets in round. (0..4 = 5)
 * Pot size. ()
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
