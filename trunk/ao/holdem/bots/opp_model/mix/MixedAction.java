package ao.holdem.bots.opp_model.mix;

import ao.holdem.def.state.env.TakenAction;
import ao.holdem.bots.opp_model.mix.Classification;

/**
 * Randomized Mixed Action.
 * A probability distribution of actions.
 */
public class MixedAction extends Classification<TakenAction>
{
    //--------------------------------------------------------------------
    public MixedAction(double raiseProbability,
                       double callProbability)
    {
        super( TakenAction.class );

        add( TakenAction.RAISE, raiseProbability );
        add( TakenAction.CALL,  callProbability );
        add( TakenAction.FOLD,  1.0 - callProbability - callProbability );
    }

    public MixedAction(double raiseWeight,
                       double callWeight,
                       double foldWeight)
    {
        super( TakenAction.class );

        add( TakenAction.RAISE, raiseWeight );
        add( TakenAction.CALL,  callWeight  );
        add( TakenAction.FOLD,  foldWeight  );
    }


    //--------------------------------------------------------------------
    public double raiseProbability()
    {
        return probabilityOf( TakenAction.RAISE );
    }

    public double callProbability()
    {
        return probabilityOf( TakenAction.CALL );
    }

    public double foldProability()
    {
        return probabilityOf( TakenAction.FOLD );
    }
}

