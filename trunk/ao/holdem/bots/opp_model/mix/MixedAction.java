package ao.holdem.bots.opp_model.mix;

import ao.holdem.def.state.env.TakenAction;

/**
 * Randomized Mixed Action.
 * A probability distribution of actions.
 */
public class MixedAction extends Classification<TakenAction>
{
    //--------------------------------------------------------------------
    public MixedAction()
    {
        super( TakenAction.class );
    }

    public MixedAction(double foldProbability,
                       double callProbability)
    {
        this();
        add( TakenAction.FOLD,  foldProbability );
        add( TakenAction.CALL,  callProbability );
        add( TakenAction.RAISE, 1.0 - callProbability - callProbability );
    }

    public MixedAction(double foldWeight,
                       double callWeight,
                       double raiseWeight)
    {
        this();
        add( TakenAction.FOLD,  foldWeight  );
        add( TakenAction.CALL,  callWeight  );
        add( TakenAction.RAISE, raiseWeight );
    }

    public MixedAction(double triplet[])
    {
        this( triplet[0], triplet[1], triplet[2] );
    }

    public MixedAction(TakenAction act)
    {
        this();
        add(act, 1.0);
    }


    //--------------------------------------------------------------------
    public double foldProability()
    {
        return probabilityOf( TakenAction.FOLD );
    }

    public double raiseProbability()
    {
        return probabilityOf( TakenAction.RAISE );
    }

    public double callProbability()
    {
        return probabilityOf( TakenAction.CALL );
    }


    //--------------------------------------------------------------------
    public String toString()
    {
        return weights()[0] + "\t" +
               weights()[1] + "\t" +
               weights()[2];
    }
}

