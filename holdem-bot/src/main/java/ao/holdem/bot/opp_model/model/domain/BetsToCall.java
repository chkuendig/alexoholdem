package ao.holdem.bot.opp_model.model.domain;

/**
 *
 */
public enum BetsToCall
{
    ZERO, ONE, TWO_PLUS;
//    ZERO, ONE, TWO, THREE, FOUR;

    public static BetsToCall fromBets(int bets)
    {
        assert bets >= 0;
        return (bets == 0)
                ? ZERO
                : (bets == 1)
                   ? ONE
                   : TWO_PLUS;
//        return values()[ bets ];
    }
}
