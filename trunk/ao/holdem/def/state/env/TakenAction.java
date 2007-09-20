package ao.holdem.def.state.env;

import ao.holdem.model.Money;

/**
 *
 */
public enum TakenAction
{
    //--------------------------------------------------------------------
    FOLD,
    CALL,
    RAISE;


    //--------------------------------------------------------------------
    public RealAction toRealAction(
            Money   toCall,
            boolean betMadeThisRound)
    {
        switch (this)
        {
            case FOLD:
                return RealAction.FOLD;

            case CALL:
                return toCall.equals( Money.ZERO )
                        ? RealAction.CHECK
                        : RealAction.CALL;

            case RAISE:
                return betMadeThisRound
                        ? RealAction.RAISE
                        : RealAction.BET;
        }
        throw new Error("should never be here");
    }
}
