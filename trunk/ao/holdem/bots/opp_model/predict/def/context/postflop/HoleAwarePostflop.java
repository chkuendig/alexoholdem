package ao.holdem.bots.opp_model.predict.def.context.postflop;

import ao.holdem.bots.util.ApproximateOddFinder;
import ao.holdem.bots.util.OddFinder;
import ao.holdem.bots.util.Odds;
import ao.holdem.def.model.cards.Community;
import ao.holdem.def.model.cards.Hole;
import ao.holdem.def.state.env.TakenAction;
import ao.holdem.history.Snapshot;

/**
 *
 */
public class HoleAwarePostflop extends HoldemPostflop
{
    //--------------------------------------------------------------------
    public HoleAwarePostflop(
            Snapshot    prev,
            TakenAction prevAct,
            Snapshot    curr,
            Community   community,
            Hole        hole)
    {
        super(prev, prevAct, curr);

        OddFinder oddFinder = new ApproximateOddFinder();
        Odds odds = oddFinder.compute(
                        hole, community,
                        curr.activeOpponents().size());

        double winPercent = odds.strengthVsRandom();
        addNeuralInput(winPercent);
    }
}
