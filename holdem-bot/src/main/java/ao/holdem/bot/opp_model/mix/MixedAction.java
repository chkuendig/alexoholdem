package ao.holdem.bot.opp_model.mix;

import ao.ai.classify.decision.impl.classification.RealHistogram;
import ao.holdem.model.act.AbstractAction;
import ao.util.math.rand.Rand;

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
            RealHistogram<AbstractAction> hist)
    {
        double fold  = hist.probabilityOf(AbstractAction.QUIT_FOLD);
        double call  = hist.probabilityOf(AbstractAction.CHECK_CALL);
        double raise = hist.probabilityOf(AbstractAction.BET_RAISE);
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
    public AbstractAction weightedRandom()
    {
        double rand = Rand.nextDouble();

        return (rand <= foldProb)
                ? AbstractAction.QUIT_FOLD
                : (rand <= (foldProb + callProb))
                   ? AbstractAction.CHECK_CALL
                   : AbstractAction.BET_RAISE;
    }


    //--------------------------------------------------------------------
    public double probabilityOf(AbstractAction action)
    {
        return action == AbstractAction.QUIT_FOLD
               ? foldProb
               : action == AbstractAction.CHECK_CALL
                 ? callProb
                 : action == AbstractAction.BET_RAISE
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

