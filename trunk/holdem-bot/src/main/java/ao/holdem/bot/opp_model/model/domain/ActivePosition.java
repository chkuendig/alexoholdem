package ao.holdem.bot.opp_model.model.domain;

/**
 *
 */
public enum ActivePosition
{
//    FIRST, MIDDLE, LAST;
    EARLY, MIDDLE, LATE;
//    EARLY, MIDDLE, LATE, LAST;
//    FIRST, MIDDLE, SECOND_LAST, LAST;
//    FIRST, SECOND, THIRD, FOURTH, FIFTH, SIXTH, SEVENTH, EIGHTH, NINETH, TENTH;

    public static ActivePosition fromPosition(
            int activeOpponents, int activePosition)
    {
        double positionPercent =
                activePosition / (activeOpponents + 1.0);
        return positionPercent < 0.33
                ? EARLY
                : positionPercent < 0.66
                  ? MIDDLE : LATE;
//                  : positionPercent < 0.99
//                    ? LATE
//                    : LAST;
        
//        return (activePosition == 1)
//                ? FIRST
////                : (activePosition == (activeOpponents))
////                   ? SECOND_LAST
//                   : (activePosition == (activeOpponents+1))
//                      ? LAST
//                      : MIDDLE;
//        return values()[ activePosition-1 ];
    }
}
