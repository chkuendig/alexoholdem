package ao.holdem.def.state.action;

/**
 * In Holdem, there are four possible actions:
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
 */
public enum Action
{
    CHECK_OR_FOLD,
    CHECK_OR_CALL,
    RAISE_OR_CALL
}
