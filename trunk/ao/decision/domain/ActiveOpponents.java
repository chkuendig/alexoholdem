package ao.decision.domain;

/**
 *
 */
public enum ActiveOpponents
{
//    ONE, TWO_OR_THREE, FOUR_OR_MORE;
    ONE, TWO, THREE, FOUR, FIVE, SIX, SEVEN, EIGHT, NINE;

    public static ActiveOpponents fromActiveOpps(int activeOpps)
    {
//        return (activeOpps == 1)
//                ? ONE
//                : (activeOpps < 4)
//                   ? TWO_OR_THREE
//                   : FOUR_OR_MORE;
        return values()[ activeOpps - 1 ];
    }
}
