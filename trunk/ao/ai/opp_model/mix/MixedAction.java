package ao.ai.opp_model.mix;

import ao.ai.supervised.decision.classification.RealHistogram;
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

    public static MixedAction fromHistogram(
            RealHistogram<SimpleAction> hist)
    {
        double fold  = hist.probabilityOf(SimpleAction.FOLD);
        double call  = hist.probabilityOf(SimpleAction.CALL);
        double raise = hist.probabilityOf(SimpleAction.RAISE);
        double total = fold + call + raise;
        if (total == 0) return new MixedAction(1, 1, 1);

        int    sample     = hist.sampleSize();
        double avg        = total / 3.0;
        double error      = Math.sqrt(sample + 1) - 1;
        double errPercent = error / sample;

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
    public SimpleAction weightedRandom()
    {
        double rand = Rand.nextDouble();

        return (rand <= foldProb)
                ? SimpleAction.FOLD
                : (rand <= (foldProb + callProb))
                   ? SimpleAction.CALL
                   : SimpleAction.RAISE;
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

