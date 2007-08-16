package ao.holdem.bots.opp_model.predict.def.observation;

import ao.holdem.bots.opp_model.mix.MixedAction;
import ao.holdem.def.state.env.TakenAction;


/**
 *
 */
public class HoldemObservation extends ObservationImpl
{
    //--------------------------------------------------------------------
    public HoldemObservation(TakenAction act)
    {
        super( new MixedAction(act).weights() );
    }

    public HoldemObservation(MixedAction act)
    {
        super( act.weights() );
    }


    //--------------------------------------------------------------------
    public String toString()
    {
        return neuralOutput()[0] + "\t" +
               neuralOutput()[1] + "\t" +
               neuralOutput()[2];
    }
}
