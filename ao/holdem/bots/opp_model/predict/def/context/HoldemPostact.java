package ao.holdem.bots.opp_model.predict.def.context;

import ao.holdem.def.state.env.TakenAction;
import ao.holdem.history.Snapshot;

/**
 *
 */
public class HoldemPostact extends HoldemPreact
{
    //--------------------------------------------------------------------
    public HoldemPostact(
            Snapshot    prev,
            TakenAction prevAct,
            Snapshot    curr)
    {
        super(curr);

        double lastBetsToCallBool =
                (prev.toCall().smallBlinds() > 0)
                ? 1.0 : 0.0;

        double lastActRaiseBool =
                (prevAct == TakenAction.RAISE)
                ? 1.0 : 0.0;

        addNeuralInput(
                lastBetsToCallBool,
                lastActRaiseBool);
    }
}
