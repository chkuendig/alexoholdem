package ao.ai.monte_carlo;

import ao.ai.opp_model.decision.classification.RealHistogram;
import ao.ai.opp_model.mix.MixedAction;
import ao.holdem.model.BettingRound;
import ao.holdem.model.act.SimpleAction;
import ao.state.HandState;

/**
 *
 */
public class Choice
{
    //----------------------------------------------------------------
    private final MixedAction  expected;
    private final SimpleAction actual;
    private final HandState    state;


    //----------------------------------------------------------------
    public Choice(RealHistogram<SimpleAction> expected,
                  SimpleAction                actual,
                  HandState                   state)
    {
        this.expected = MixedAction.fromHistogram( expected );
        this.actual   = actual;
        this.state    = state;
    }


    //----------------------------------------------------------------
    public HandState state()
    {
        return state;
    }

//    public MixedAction expected()
//    {
//        return expected;
//    }

//    public SimpleAction actual()
//    {
//        return actual;
//    }

    public double surprise()
    {
        double value =
                (actual == SimpleAction.FOLD)
                 ? -1
                 : (actual == SimpleAction.CALL)
                    ? 0 : 1;
        double exp =
                state.nextToActCanRaise()
                ? expected.probabilityOf(SimpleAction.RAISE) -
                  expected.probabilityOf(SimpleAction.FOLD)
                : -(expected.probabilityOf(SimpleAction.FOLD) /
                    (1.0 - expected.probabilityOf(SimpleAction.RAISE)));
        return value - exp;
    }

    public BettingRound round()
    {
        return state.round();
    }
}
