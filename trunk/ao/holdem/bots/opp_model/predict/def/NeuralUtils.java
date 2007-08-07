package ao.holdem.bots.opp_model.predict.def;

/**
 *
 */
public class NeuralUtils
{
    //--------------------------------------------------------------------
    private NeuralUtils() {}


    //--------------------------------------------------------------------
    public static double asDouble(boolean bool)
    {
        return bool ? 1.0 : 0.0;
    }
}
