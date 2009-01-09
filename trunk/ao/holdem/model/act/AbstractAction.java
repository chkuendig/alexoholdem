package ao.holdem.model.act;

import ao.holdem.model.Chips;


/**
 * 
 */
public enum AbstractAction
{
    //--------------------------------------------------------------------
    QUIT_FOLD(FallbackAction.CHECK_OR_FOLD){
        public Action toAction(
                Chips toCall, boolean betMadeThisRound) {
            return Action.FOLD;
        }},
    CHECK_CALL(FallbackAction.CHECK_OR_CALL){
        public Action toAction(
                Chips toCall, boolean betMadeThisRound) {
            return toCall.equals( Chips.ZERO )
                   ? Action.CHECK
                   : Action.CALL;
        }},
    BET_RAISE(FallbackAction.RAISE_OR_CALL){
        public Action toAction(
                Chips toCall, boolean betMadeThisRound) {
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
//    public Action fallback(HandState state)
//    {
//        return fallback(state.toCall(),
//                            state.remainingBetsInRound() < 4);
//    }

    public abstract Action toAction(
            Chips toCall,
            boolean betMadeThisRound);


    //--------------------------------------------------------------------
    public FallbackAction toFallbackAction()
    {
        return FALLACK_ACTION;
    }
}
