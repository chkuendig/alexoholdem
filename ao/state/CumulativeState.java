package ao.state;

import ao.holdem.model.Community;
import ao.holdem.model.Hole;
import ao.holdem.model.act.RealAction;

/**
 * 
 */
public interface CumulativeState
{
    public void reset();

    public void advance(
            HandState   stateBeforeAct,
            PlayerState actor,
            RealAction  act,
            Community   communityBeforeAct,
            Hole        holeCards);
}
