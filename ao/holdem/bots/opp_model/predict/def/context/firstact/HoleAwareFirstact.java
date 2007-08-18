package ao.holdem.bots.opp_model.predict.def.context.firstact;

import ao.holdem.bots.opp_model.predict.def.context.HoldemPreact;
import ao.holdem.bots.util.ApproximateOddFinder;
import ao.holdem.bots.util.OddFinder;
import ao.holdem.bots.util.Odds;
import ao.holdem.def.model.cards.Community;
import ao.holdem.def.model.cards.Hole;
import ao.holdem.history.Snapshot;

/**
 *
 */
public class HoleAwareFirstact extends HoldemPreact
{
    public HoleAwareFirstact(
            Snapshot curr,
            Hole     hole)
    {
        super(curr);

        OddFinder oddFinder = new ApproximateOddFinder();
        Odds odds = oddFinder.compute(
                        hole, new Community(),
                        curr.activeOpponents().size());

        double winPercent = odds.strengthVsRandom();
        addNeuralInput(winPercent);
    }
}
