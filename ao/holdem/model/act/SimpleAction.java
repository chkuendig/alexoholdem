package ao.holdem.model.act;

import ao.holdem.model.Money;

/**
 *
 */
public enum SimpleAction
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


    //--------------------------------------------------------------------
    public EasyAction toEasyAction()
    {
        return this == SimpleAction.FOLD
                ? EasyAction.CHECK_OR_FOLD
                : this == SimpleAction.CALL
                  ? EasyAction.CHECK_OR_CALL
                  : EasyAction.RAISE_OR_CALL;
    }
}
