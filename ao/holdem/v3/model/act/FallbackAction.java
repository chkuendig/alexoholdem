package ao.holdem.v3.model.act;

import ao.holdem.model.act.RealAction;

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
        public RealAction toRealAction(
                boolean canCheck, boolean canRaise) {
            return canCheck
                   ? RealAction.CHECK
                   : RealAction.FOLD;
        }},

    CHECK_OR_CALL {
        public RealAction toRealAction(
                boolean canCheck, boolean canRaise) {
            return canCheck
                   ? RealAction.CHECK
                   : RealAction.CALL;
        }},

    RAISE_OR_CALL {
        public RealAction toRealAction(
                boolean canCheck, boolean canRaise) {
            return canRaise
                   ? RealAction.RAISE
                   : RealAction.CALL;
        }};


    //--------------------------------------------------------------------
//    public RealAction toRealAction(HandState state)
//    {
//        return state.toRealAction(this);
//    }

    public abstract RealAction toRealAction(
            boolean canCheck,
            boolean canRaise);
//    {
//        switch (this)
//        {
//            case CHECK_OR_CALL:
//                return canCheck
//                        ? RealAction.CHECK
//                        : RealAction.CALL;
//
//            case CHECK_OR_FOLD:
//                return canCheck
//                        ? RealAction.CHECK
//                        : RealAction.FOLD;
//
//            case RAISE_OR_CALL:
//                return canRaise
//                        ? RealAction.RAISE
//                        : RealAction.CALL;
//        }
//        throw new Error("can't be here");
//    }
}
