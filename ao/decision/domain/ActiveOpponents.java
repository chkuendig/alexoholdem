package ao.decision.domain;

/**
 *
 */
public enum ActiveOpponents
{
    ONE, TWO_OR_THREE, FOUR_OR_MORE;

    public static ActiveOpponents fromActiveOpps(int activeOpps)
    {
        return (activeOpps == 1)
                ? ONE
                : (activeOpps < 4)
                   ? TWO_OR_THREE
                   : FOUR_OR_MORE;
    }
}
