package ao.ai.simple;

import ao.ai.AbstractPlayer;
import ao.holdem.engine.state.State;
import ao.holdem.model.Avatar;
import ao.holdem.model.ChipStack;
import ao.holdem.model.act.Action;
import ao.holdem.model.act.FallbackAction;
import ao.holdem.model.card.sequence.CardSequence;
import ao.util.math.rand.Rand;

import java.util.Map;

//import ao.holdem.engine.analysis.Analysis;


/**
 *
 */
public class RandomBot extends AbstractPlayer
{
    //--------------------------------------------------------------------
    private static final double evenProb [] = {1.0/3, 1.0/3, 1.0/3};
    private static final double callOnly [] = {0, 1.0, 0};
    private static final double callRaise[] = {0, 0.5, 0.5};
    private static final double foldCall [] = {0.5, 0.5, 0};


    //--------------------------------------------------------------------
    public void handEnded(Map<Avatar, ChipStack> deltas) {}


    //--------------------------------------------------------------------
    public Action act(State        state,
                      CardSequence cards/*,
                      Analysis     analysis*/)
    {
        double prob[];
        if (state.canCheck()) {
            // no need to fold
            if (state.canRaise()) {
                prob = callRaise;
            }
            else
            {
                prob = callOnly;
            }
        } else {
            // can fold
            if (state.canRaise()) {
                prob = evenProb;
            } else {
                prob = foldCall;
            }
        }

        double rand = Rand.nextDouble();
        if (rand < prob[0]) {
            return state.reify(FallbackAction.CHECK_OR_FOLD);
        } else {
            rand -= prob[0];
            if (rand < prob[1]) {
                return state.reify(FallbackAction.CHECK_OR_CALL);
            } else {
                return state.reify(FallbackAction.RAISE_OR_CALL);
            }
        }
    }
}
