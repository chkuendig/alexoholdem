package ao.ai.simple;

import ao.ai.AbstractPlayer;
import ao.holdem.engine.analysis.Analysis;
import ao.holdem.engine.state.State;
import ao.holdem.model.Avatar;
import ao.holdem.model.Chips;
import ao.holdem.model.act.Action;
import ao.holdem.model.act.FallbackAction;
import ao.holdem.model.card.Community;
import ao.holdem.model.card.Hole;
import ao.util.rand.Rand;

import java.util.Map;


/**
 *
 */
public class RandomBot extends AbstractPlayer
{
    //--------------------------------------------------------------------
    public void handEnded(Map<Avatar, Chips> deltas) {}


    //--------------------------------------------------------------------
    public Action act(State     state,
                      Hole      hole,
                      Community community,
                      Analysis  analysis)
    {
        return state.reify(
                Rand.fromArray( FallbackAction.VALUES ));
    }
}
