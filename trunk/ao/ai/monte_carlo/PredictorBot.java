package ao.ai.monte_carlo;

import ao.ai.AbstractPlayer;
import ao.holdem.model.act.EasyAction;
import ao.holdem.model.card.Hole;
import ao.holdem.model.Money;
import ao.state.StateManager;
import ao.state.HandState;
import ao.persist.PlayerHandle;

import java.util.Map;

/**
 *
 */
public class PredictorBot extends AbstractPlayer
{
    //--------------------------------------------------------------------
    protected EasyAction act(
            StateManager env,
            HandState    state,
            Hole         hole)
    {



        return null;
    }

    
    //--------------------------------------------------------------------
    public void handEnded(Map<PlayerHandle, Money> deltas)
    {

    }
}
