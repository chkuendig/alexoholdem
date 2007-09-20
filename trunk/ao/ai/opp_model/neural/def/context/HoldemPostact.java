package ao.ai.opp_model.neural.def.context;

import static ao.ai.opp_model.neural.def.NeuralUtils.asDouble;
import ao.holdem.model.act.SimpleAction;

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
                asDouble(ctx.lastAct() == SimpleAction.RAISE);

        addNeuralInput(
                lastBetsToCallBool,
                lastActRaiseBool);
    }
}
