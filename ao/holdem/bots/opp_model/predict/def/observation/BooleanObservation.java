package ao.holdem.bots.opp_model.predict.def.observation;

/**
 *
 */
public class BooleanObservation extends ObservationImpl
{
    //--------------------------------------------------------------------
    public BooleanObservation(boolean observation)
    {
        super( new double[]{ observation ? 1 : 0 } );
    }


    //--------------------------------------------------------------------
    public String toString()
    {
        return Boolean.toString(
                neuralOutput()[0] > 0);
    }
}
