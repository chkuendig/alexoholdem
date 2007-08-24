package ao.decision.domain;

/**
 *
 */
public enum ActivePosition
{
    FIRST, MIDDLE, LAST;

    public static ActivePosition fromPosition(
            int activeOpponents, int activePosition)
    {
        return (activePosition == 1)
                ? FIRST
                : (activePosition == (activeOpponents+1))
                   ? LAST
                   : MIDDLE;
    }
}
