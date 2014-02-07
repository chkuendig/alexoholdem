package ao.holdem.model.canon.turn;

import ao.holdem.model.canon.card.CanonSuit;
import static ao.holdem.model.canon.card.CanonSuit.*;

import java.util.*;

/**
 * Date: Sep 15, 2008
 * Time: 4:09:49 PM
 */
/*package-private*/ enum TurnCase
{
    //--------------------------------------------------------------------
    F   (FIRST),
    FS  (FIRST, SECOND),
    FST (FIRST, SECOND, THIRD),
    FSR (FIRST, SECOND, FOURTH),
    FSTR(FIRST, SECOND, THIRD, FOURTH),
    FT  (FIRST, THIRD),
    FTR (FIRST, THIRD, FOURTH),
    FR  (FIRST, FOURTH),

    S  (SECOND),
    ST (SECOND, THIRD),
    STF(SECOND, THIRD, FOURTH),
    SF (SECOND, FOURTH),

    T (THIRD),
    TR(THIRD, FOURTH),

    R(FOURTH),
    ;

    public static TurnCase VALUES[] = values();


    //--------------------------------------------------------------------
    public static TurnCase valueOf(Set<CanonSuit> cases)
    {
        for (TurnCase riverCaseSet : VALUES)
        {
            if (riverCaseSet.SUIT_CASES.equals( cases ))
            {
                return riverCaseSet;
            }
        }
        System.err.println("unknown set: " + cases);
        return null;
    }


    //--------------------------------------------------------------------
    private final EnumSet<CanonSuit> SUIT_CASES;
    private final int                INDEXES[];


    //--------------------------------------------------------------------
    private TurnCase(CanonSuit... suitCases)
    {
        SUIT_CASES = EnumSet.copyOf(Arrays.asList(suitCases));
        INDEXES     = computeIndex();
    }


    //--------------------------------------------------------------------
    public int size()
    {
        return SUIT_CASES.size();
    }

    public int index(CanonSuit of)
    {
        return INDEXES[ of.ordinal() ];
    }
    private int[] computeIndex()
    {
        List<CanonSuit> asList =
                new ArrayList<CanonSuit>(SUIT_CASES);
        int indexes[] = new int[ CanonSuit.VALUES.length ];
        for (CanonSuit suitCase : CanonSuit.VALUES)
        {
            indexes[ suitCase.ordinal() ] =
                    asList.indexOf( suitCase );
        }
        return indexes;
    }
}
