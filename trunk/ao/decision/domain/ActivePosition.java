package ao.decision.domain;

/**
 *
 */
public enum ActivePosition
{
//    FIRST, MIDDLE, LAST;
    FIRST, SECOND, THIRD, FOURTH, FIFTH, SIXTH, SEVENTH, EIGHTH, NINETH, TENTH;

    public static ActivePosition fromPosition(
            int activeOpponents, int activePosition)
    {
//        return (activePosition == 1)
//                ? FIRST
//                : (activePosition == (activeOpponents+1))
//                   ? LAST
//                   : MIDDLE;
        return values()[ activePosition-1 ];
    }
}
