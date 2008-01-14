package ao.ai.monte_carlo;

import ao.ai.opp_model.mix.MixedAction;
import ao.holdem.model.act.SimpleAction;
import ao.holdem.model.BettingRound;

/**
 *
 */
public class Surprise
{
    //----------------------------------------------------------------
    private final MixedAction  expected;
    private final SimpleAction actual;
    private final BettingRound round;
    private final boolean      canRaise;


    //----------------------------------------------------------------
    public Surprise(MixedAction  expected,
                    SimpleAction actual,
                    BettingRound round,
                    boolean      canRaise)
    {
        this.expected = expected;
        this.actual   = actual;
        this.round    = round;
        this.canRaise = canRaise;
    }


    //----------------------------------------------------------------
    public MixedAction expected()
    {
        return expected;
    }

    public SimpleAction actual()
    {
        return actual;
    }

    public double surprise()
    {
        double value =
                (actual == SimpleAction.FOLD)
                 ? -1
                 : (actual == SimpleAction.CALL)
                    ? 0 : 1;
        double exp =
                canRaise
                ? expected.probabilityOf(SimpleAction.RAISE) -
                  expected.probabilityOf(SimpleAction.FOLD)
                : -(expected.probabilityOf(SimpleAction.FOLD) /
                    (1.0 - expected.probabilityOf(SimpleAction.RAISE)));
        return value - exp;
    }

    public BettingRound round()
    {
        return round;
    }
}
