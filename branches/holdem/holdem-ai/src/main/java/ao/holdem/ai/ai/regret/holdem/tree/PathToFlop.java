package ao.holdem.ai.ai.regret.holdem.tree;

import ao.holdem.model.act.AbstractAction;

import java.util.Arrays;
import java.util.List;

import static ao.holdem.model.act.AbstractAction.BET_RAISE;
import static ao.holdem.model.act.AbstractAction.CHECK_CALL;

/**
 * All possible preflop sequences that lead to the flop.
 */
public enum PathToFlop
{
    //--------------------------------------------------------------------
    C_C      (CHECK_CALL, CHECK_CALL),

    C_R_C    (CHECK_CALL, BET_RAISE, CHECK_CALL),

    C_R_R_C  (CHECK_CALL, BET_RAISE, BET_RAISE, CHECK_CALL),

    C_R_R_R_C(CHECK_CALL, BET_RAISE, BET_RAISE, BET_RAISE, CHECK_CALL),

    R_C      (BET_RAISE, CHECK_CALL),

    R_R_C    (BET_RAISE, BET_RAISE, CHECK_CALL),

    R_R_R_C  (BET_RAISE, BET_RAISE, BET_RAISE, CHECK_CALL);

    public static final PathToFlop[] VALUES = values();


    //--------------------------------------------------------------------
    public static PathToFlop matching(List<AbstractAction> seq)
    {
        for (PathToFlop path : VALUES)
        {
            if (path.matches( seq )) return path;
        }
        return null;
    }


    //--------------------------------------------------------------------
    private final AbstractAction sequence[];


    //--------------------------------------------------------------------
    private PathToFlop(AbstractAction... seq)
    {
        sequence = seq;
    }


    //--------------------------------------------------------------------
    public boolean matches(List<AbstractAction> seq)
    {
        return Arrays.asList( sequence ).equals( seq );
    }
}
