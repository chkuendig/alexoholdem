package ao.ai.opp_model.mix;

import ao.ai.opp_model.decision.classification.Histogram;
import ao.holdem.model.act.SimpleAction;
import ao.util.rand.Rand;

/**
 * Randomized Mixed Action.
 * A probability distribution of actions.
 */
public class MixedAction
{
    //--------------------------------------------------------------------
    public static MixedAction randomInstance()
    {
        return new MixedAction(Rand.nextDouble(),
                               Rand.nextDouble(),
                               Rand.nextDouble());
    }

    public static MixedAction fromHistogram(Histogram hist)
    {
        int fold  = hist.countOfState(SimpleAction.FOLD);
        int call  = hist.countOfState(SimpleAction.CALL);
        int raise = hist.countOfState(SimpleAction.RAISE);
        int total = fold + call + raise;
        if (total == 0) return new MixedAction(1, 1, 1);

        double avg        = total / 3.0;
        double error      = Math.sqrt(total + 1) - 1;
        double errPercent = error / total;

        double adjustedFold  = (fold  - avg)*(1 - errPercent) + avg;
        double adjustedCall  = (call  - avg)*(1 - errPercent) + avg;
        double adjustedRaise = (raise - avg)*(1 - errPercent) + avg;

        return new MixedAction(adjustedFold, adjustedCall, adjustedRaise);
    }


    //--------------------------------------------------------------------
    private final double foldProb;
    private final double callProb;
    private final double raiseProb;


    //--------------------------------------------------------------------
    public MixedAction(double foldWeight,
                       double callWeight,
                       double raiseWeight)
    {
        double total = foldWeight + callWeight + raiseWeight;

        foldProb  = foldWeight / total;
        callProb  = callWeight / total;
        raiseProb = raiseWeight / total;
    }


    //--------------------------------------------------------------------
    public double foldProability()
    {
        return foldProb;
    }

    public double callProbability()
    {
        return callProb;
    }

    public double raiseProbability()
    {
        return raiseProb;
    }


    //--------------------------------------------------------------------
    public double probabilityOf(SimpleAction action)
    {
        return action == SimpleAction.FOLD
               ? foldProb
               : action == SimpleAction.CALL
                 ? callProb
                 : action == SimpleAction.RAISE
                   ? raiseProb : 0;
    }

    //--------------------------------------------------------------------
    public String toString()
    {
        return foldProb + "\t" +
               callProb + "\t" +
               raiseProb;
    }
}

