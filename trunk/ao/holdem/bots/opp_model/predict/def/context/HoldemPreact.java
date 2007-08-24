package ao.holdem.bots.opp_model.predict.def.context;

import static ao.holdem.bots.opp_model.predict.def.NeuralUtils.asDouble;

/**
 *
 */
public class HoldemPreact extends AbstractContext
{
    //--------------------------------------------------------------------
    public HoldemPreact(GenericContext ctx)
    {
        double immedatePotOdds = ctx.immedatePotOdds();

        double betRatio = ctx.betRatio();
        double potRatio = ctx.potRatio();
        double committedThisRoundBool =
                asDouble(ctx.committedThisRound());

        double zeroBetsToCallBool =
                asDouble(ctx.betsToCall() == 0);
        double oneBetToCallBool =
                asDouble(ctx.betsToCall() == 1);
        double manyBetsToCallBool =
                asDouble(ctx.betsToCall() >= 2);

        double numOppsFraction = ctx.numOpps() / 9.0;
        double numActiveOppsFraction = ctx.numActiveOpps() / 9.0;
        double numUnactedOppsFraction = ctx.numUnactedThisRound() / 10.0;

        double position = ctx.position() / 10.0;
        double activePosition = ctx.activePosition() / 10.0;

        addNeuralInput(
                immedatePotOdds,
                betRatio,
                potRatio,
                committedThisRoundBool,
                zeroBetsToCallBool,
                oneBetToCallBool,
                manyBetsToCallBool,
                numOppsFraction,
                numActiveOppsFraction,
                numUnactedOppsFraction,
                position,
                activePosition);
    }
}
