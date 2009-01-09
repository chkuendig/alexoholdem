package ao.holdem.model.act;


/**
 * In limit Holdem, there are four possible actions:
 *  fold, check, call, raise.
 *
 * A player may choose between these, at each turn
 *  there is a choice between more than one action.
 *
 * In general, the choices that a player has fall into
 *  three categories:
 *  1) fold, check, raise
 *       if there was no prior raise
 *  2) fold, call, raise
 *       if there was a prior raise
 *  3) fold, call
 *       if there were four prior bets, so another
 *       bet is not an option.
 *
 *  Note that at any point, a player may choose to fold.
 *  However, in case (1) it makes no sense to fold because
 *      a player can just check instead.
 *  Also note that the combination of fold, check, but
 *      without the possibility of raising can never happen.
 *
 * At any point in the game, all of the following actions
 *  are valid moves.
 *
 * See AbstractAction.
 */
public enum FallbackAction
{
    //--------------------------------------------------------------------
    CHECK_OR_FOLD {
        public Action fallback(
                boolean canCheck, boolean canRaise) {
            return canCheck
                   ? Action.CHECK
                   : Action.FOLD;
        }},

    CHECK_OR_CALL {
        public Action fallback(
                boolean canCheck, boolean canRaise) {
            return canCheck
                   ? Action.CHECK
                   : Action.CALL;
        }},

    RAISE_OR_CALL {
        public Action fallback(
                boolean canCheck, boolean canRaise) {
            return canRaise
                   ? Action.RAISE
                   : Action.CALL;
        }};

    public static final FallbackAction VALUES[] = values();


    //--------------------------------------------------------------------
//    public RealAction fallback(HandState state)
//    {
//        return state.fallback(this);
//    }

    public abstract Action fallback(
            boolean canCheck,
            boolean canRaise);
}
