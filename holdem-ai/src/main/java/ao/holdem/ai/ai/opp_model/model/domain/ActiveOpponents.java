package ao.ai.opp_model.model.domain;

/**
 *
 */
public enum ActiveOpponents
{
//    ONE, TWO_OR_THREE, FOUR_OR_MORE;
    ONE_OR_TWO, THREE_TO_FIVE, SIX_OR_MORE;
//    ONE, TWO, THREE, FOUR, FIVE, SIX, SEVEN, EIGHT, NINE;

    public static ActiveOpponents fromActiveOpps(int activeOpps)
    {
//        return (activeOpps == 1)
//                ? ONE
//                : (activeOpps < 4)
//                   ? TWO_OR_THREE
//                   : FOUR_OR_MORE;
//        return values()[ activeOpps - 1 ];

        return (activeOpps <= 2)
                ? ONE_OR_TWO
                : (activeOpps <= 5)
                   ? THREE_TO_FIVE
                   : SIX_OR_MORE;
    }
}
