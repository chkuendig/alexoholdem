package ao.holdem.bots.opp_model;

import ao.holdem.def.state.env.TakenAction;
import ao.util.rand.Rand;

/**
 * Randomized Mixed Action.
 * A probability distribution of actions.
 */
public class MixedAction
{
    //--------------------------------------------------------------------
    private final double raiseProb;
    private final double callProb;


    //--------------------------------------------------------------------
    public MixedAction(double raiseProbability,
                       double callProbability)
    {
        assert raiseProbability >= 0 &&
               callProbability  >= 0;

        raiseProb = raiseProbability;
        callProb  = callProbability;
    }

    public MixedAction(double raiseWeight,
                       double callWeight,
                       double foldWeight)
    {
        assert raiseWeight >= 0 &&
               callWeight  >= 0 &&
               foldWeight  >= 0;

        double total = raiseWeight + callWeight + foldWeight;

        raiseProb = raiseWeight / total;
        callProb  = callWeight / total;
    }


    //--------------------------------------------------------------------
    public double raiseProbability()
    {
        return raiseProb;
    }

    public double callProbability()
    {
        return callProb;
    }

    public double foldProability()
    {
        return 1.0 - raiseProb - callProb;
    }


    //--------------------------------------------------------------------
    public TakenAction nextRandom()
    {
        double rand = Rand.nextDouble();
        return rand < raiseProb
                ? TakenAction.RAISE
                : rand - raiseProb < callProb
                   ? TakenAction.CALL
                   : TakenAction.FOLD;
    }


    //--------------------------------------------------------------------
    @Override
    public String toString()
    {
        return "[" + Math.round(raiseProb        * 100) + ", " +
                     Math.round(callProb         * 100) + ", " +
                     Math.round(foldProability() * 100) + "]";
    }
}

