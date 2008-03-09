package ao.holdem.v3.model.act.descrete;

import ao.holdem.model.Money;

/**
 * 
 */
public enum AbstractAction
{
    //--------------------------------------------------------------------
    QUIT_FOLD(FallbackAction.CHECK_OR_FOLD){
        public Action toAction(
                Money toCall, boolean betMadeThisRound) {
            return Action.FOLD;
        }},
    CHECK_CALL(FallbackAction.CHECK_OR_CALL){
        public Action toAction(
                Money toCall, boolean betMadeThisRound) {
            return toCall.equals( Money.ZERO )
                   ? Action.CHECK
                   : Action.CALL;
        }},
    BET_RAISE(FallbackAction.RAISE_OR_CALL){
        public Action toAction(
                Money toCall, boolean betMadeThisRound) {
            return betMadeThisRound
                   ? Action.RAISE
                   : Action.BET;
        }};


    //--------------------------------------------------------------------
    private final FallbackAction FALLACK_ACTION;


    //--------------------------------------------------------------------
    private AbstractAction(FallbackAction fallbackAction)
    {
        FALLACK_ACTION = fallbackAction;
    }


    //--------------------------------------------------------------------
//    public Action toRealAction(HandState state)
//    {
//        return toRealAction(state.toCall(),
//                            state.remainingBetsInRound() < 4);
//    }

    public abstract Action toAction(
            Money toCall,
            boolean betMadeThisRound);
//    {
//        switch (this)
//        {
//            case FOLD:
//                return RealAction.FOLD;
//
//            case CALL:
//                return toCall.equals( Money.ZERO )
//                        ? RealAction.CHECK
//                        : RealAction.CALL;
//
//            case RAISE:
//                return betMadeThisRound
//                        ? RealAction.RAISE
//                        : RealAction.BET;
//        }
//        throw new Error("should never be here");
//    }


    //--------------------------------------------------------------------
    public FallbackAction toFallbackAction()
    {
        return FALLACK_ACTION;
    }
}
