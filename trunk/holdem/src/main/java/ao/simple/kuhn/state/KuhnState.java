package ao.simple.kuhn.state;

import ao.simple.kuhn.KuhnAction;

/**
 * 
 */
public enum KuhnState
{
    //--------------------------------------------------------------------
    //or bet, forcing the first player to make a final
    // decision of either bet, for a showdown, or pass, for a fold.
    AFTER_RAISE(new StateFlow(null, KuhnOutcome.PLAYER_TWO_WINS),
                new StateFlow(null, KuhnOutcome.DOUBLE_SHOWDOWN),
                true),

    //If the first player bets the second player may
    // either bet, causing a showdown, or pass, to fold.
    AFTER_BET(new StateFlow(null, KuhnOutcome.PLAYER_ONE_WINS),
              new StateFlow(null, KuhnOutcome.DOUBLE_SHOWDOWN),
              false),

    //If the first player passes, the second player can
    // also pass, causing a showdown,
    AFTER_PASS(new StateFlow(null, KuhnOutcome.SHOWDOWN),
               new StateFlow(AFTER_RAISE, null),
               false),

    // The first player can either bet or pass.
    FIRST_ACTION(new StateFlow(AFTER_PASS, null),
                 new StateFlow(AFTER_BET,  null),
                 true);

    public static final KuhnState VALUES[] = values();


    //--------------------------------------------------------------------
    private final StateFlow ON_PASS;
    private final StateFlow ON_BET_RAISE;
    private final boolean   FIRST_TO_ACT;


    //--------------------------------------------------------------------
    private KuhnState(StateFlow onPass,
                      StateFlow onBetRaise,
                      boolean   firstToAct)
    {
        ON_PASS      = onPass;
        ON_BET_RAISE = onBetRaise;
        FIRST_TO_ACT = firstToAct;
    }


    //--------------------------------------------------------------------
    public StateFlow advance(KuhnAction action)
    {
        return action == KuhnAction.PASS
                ? ON_PASS
                : ON_BET_RAISE;
    }


    //--------------------------------------------------------------------
    // true if the next player to act is the first to act
    public boolean firstToAct()
    {
        return FIRST_TO_ACT;
    }
}
