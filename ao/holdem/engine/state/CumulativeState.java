package ao.holdem.engine.state;

import ao.holdem.model.act.RealAction;
import ao.holdem.engine.persist.PlayerHandle;

/**
 * 
 */
public interface CumulativeState
{
    public void advance(
            HandState       stateBeforeAct,
            PlayerHandle    actor,
            RealAction      act,
            HandState       stateAfterAct);
}
