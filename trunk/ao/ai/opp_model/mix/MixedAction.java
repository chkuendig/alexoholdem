package ao.ai.opp_model.mix;

import ao.holdem.model.act.SimpleAction;
import ao.util.rand.Rand;
import ao.ai.opp_model.decision.data.Histogram;

/**
 * Randomized Mixed Action.
 * A probability distribution of actions.
 */
public class MixedAction extends Classification<SimpleAction>
{
    //--------------------------------------------------------------------
    public static MixedAction randomInstance()
    {
        return new MixedAction(Rand.nextDouble(),
                               Rand.nextDouble(),
                               Rand.nextDouble());
    }

    public static MixedAction fromHistogram(Histogram<SimpleAction> hist)
    {
        int fold  = hist.countOf(SimpleAction.FOLD);
        int call  = hist.countOf(SimpleAction.CALL);
        int raise = hist.countOf(SimpleAction.RAISE);
        int total = fold + call + raise;

        double avg        = total / 3.0;
        double error      = Math.sqrt(total + 1) - 1;
        double errPercent = error / total;

        double adjustedFold  = (fold  - avg)*(1 - errPercent) + avg;
        double adjustedCall  = (call  - avg)*(1 - errPercent) + avg;
        double adjustedRaise = (raise - avg)*(1 - errPercent) + avg;

        return new MixedAction(adjustedFold, adjustedCall, adjustedRaise);
    }
    

    //--------------------------------------------------------------------
    public MixedAction()
    {
        super( SimpleAction.class );
    }

    public MixedAction(double foldProbability,
                       double callProbability)
    {
        this();
        add( SimpleAction.FOLD,  foldProbability );
        add( SimpleAction.CALL,  callProbability );
        add( SimpleAction.RAISE, 1.0 - callProbability - callProbability );
    }

    public MixedAction(double foldWeight,
                       double callWeight,
                       double raiseWeight)
    {
        this();
        add( SimpleAction.FOLD,  foldWeight  );
        add( SimpleAction.CALL,  callWeight  );
        add( SimpleAction.RAISE, raiseWeight );
    }

    public MixedAction(double triplet[])
    {
        this( triplet[0], triplet[1], triplet[2] );
    }

    public MixedAction(SimpleAction act)
    {
        this();
        add(act, 1.0);
    }


    //--------------------------------------------------------------------
    public double foldProability()
    {
        return probabilityOf( SimpleAction.FOLD );
    }

    public double raiseProbability()
    {
        return probabilityOf( SimpleAction.RAISE );
    }

    public double callProbability()
    {
        return probabilityOf( SimpleAction.CALL );
    }


    //--------------------------------------------------------------------
    public String toString()
    {
        return weights()[0] + "\t" +
               weights()[1] + "\t" +
               weights()[2];
    }
}

