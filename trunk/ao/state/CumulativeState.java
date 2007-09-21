package ao.state;

import ao.holdem.model.act.RealAction;
import ao.holdem.model.Community;

/**
 * 
 */
public interface CumulativeState
{
    public void init(HandState startOfHand);

    public void advance(
            PlayerState actor,
            RealAction  act,
            HandState   afterAct,
            Community   community);
}
