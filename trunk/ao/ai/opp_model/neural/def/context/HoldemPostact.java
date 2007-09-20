package ao.ai.opp_model.neural.def.context;

import static ao.ai.opp_model.neural.def.NeuralUtils.asDouble;
import ao.holdem.def.state.env.TakenAction;

/**
 *
 */
public class HoldemPostact extends HoldemPreact
{
    //--------------------------------------------------------------------
    public HoldemPostact(GenericContext ctx)
    {
        super(ctx);

        double lastBetsToCallBool =
                asDouble(ctx.lastBetsToCall() > 0);

        double lastActRaiseBool =
                asDouble(ctx.lastAct() == TakenAction.RAISE);

        addNeuralInput(
                lastBetsToCallBool,
                lastActRaiseBool);
    }
}
