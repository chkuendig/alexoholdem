package ao.state;

import ao.holdem.model.act.RealAction;
import ao.holdem.model.Community;

/**
 * 
 */
public interface CumulativeState
{
    public void advance(
            HandState stateBeforeAct);
    
    public void advance(
            RealAction act,
            Community  communityBeforeAct);
}
