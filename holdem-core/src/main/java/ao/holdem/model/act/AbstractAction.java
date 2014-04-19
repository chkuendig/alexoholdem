package ao.holdem.model.act;

import ao.holdem.model.ChipStack;


/**
 * Note that the actions are in a specific order: fold, call, raise.
 * The reason for this is that sometimes raising is not available,
 *  so if available actions are accessed by array index, no extra
 *  work is required.
 */
public enum AbstractAction
{
    //--------------------------------------------------------------------
    QUIT_FOLD(FallbackAction.CHECK_OR_FOLD){
        public Action toAction(
                ChipStack toCall, boolean betMadeThisRound) {
            return Action.FOLD;
        }},

    CHECK_CALL(FallbackAction.CHECK_OR_CALL){
        public Action toAction(
                ChipStack toCall, boolean betMadeThisRound) {
            return toCall.equals( ChipStack.ZERO )
                   ? Action.CHECK
                   : Action.CALL;
        }},

    BET_RAISE(FallbackAction.RAISE_OR_CALL){
        public Action toAction(
                ChipStack toCall, boolean betMadeThisRound) {
            return betMadeThisRound
                   ? Action.RAISE
                   : Action.BET;
        }};


    public static final AbstractAction[] VALUES = values();


    //--------------------------------------------------------------------
    private final FallbackAction FALLACK_ACTION;


    //--------------------------------------------------------------------
    private AbstractAction(FallbackAction fallbackAction)
    {
        FALLACK_ACTION = fallbackAction;
    }


    //--------------------------------------------------------------------
    public abstract Action toAction(
            ChipStack toCall,
            boolean betMadeThisRound);


    //--------------------------------------------------------------------
    public FallbackAction toFallbackAction()
    {
        return FALLACK_ACTION;
    }
}
