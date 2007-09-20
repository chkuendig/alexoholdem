package ao.ai.opp_model.neural.def.observation;

import ao.ai.opp_model.mix.MixedAction;
import ao.holdem.model.act.SimpleAction;


/**
 *
 */
public class HoldemObservation extends ObservationImpl
{
    //--------------------------------------------------------------------
    public HoldemObservation(SimpleAction act)
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
