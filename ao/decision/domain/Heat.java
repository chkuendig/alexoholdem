package ao.decision.domain;

/**
 * how good the community cards are.
 */
public enum Heat//1011
{
    //--------------------------------------------------------------------
//    LESS_THAN_21, LESS_THAN_27, LESS_THAN_41, HOT;
    COOL, WARM, HOT;


    //--------------------------------------------------------------------
    public static Heat fromHeat(double heat)
    {
//        return (heat <= 0.21)
//                ? LESS_THAN_21
//                : heat <= 0.27
//                  ? LESS_THAN_27
//                  : heat <= 0.41
//                    ? LESS_THAN_41
//                    : HOT;
        return (heat <= 0.23)
                ? COOL
                : heat <= 0.34
                  ? WARM : HOT;
    }
}
