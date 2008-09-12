package ao.bucket.index.post_flop.turn;

import static ao.bucket.index.post_flop.turn.TurnCase.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * Date: Aug 31, 2008
 * Time: 9:25:14 PM
 */
public enum TurnCaseSet
{
//[OO_OTT]
    OOOTT(OO_OTT),

//[OO_TTR]
    OOTTR(OO_TTR),

//[OO_TTT]
    OOTTT(OO_TTT),

//[OO_OOT]
    OOOOT(OO_OOT),

//[OO_OTR]
    OOOTR(OO_OTR),

//[OO_OTR_P, OO_OWW]
    OOOTR_OOOWW(OO_OTR_P, OO_OWW),

//[OO_RWW, OO_TRF]
    OORWW_OOTRF(OO_TRF_P, OO_RWW),

//[OO_TRF]
    OOTRF(OO_TRF),

//[OO_TWW, OO_WWW]
    OOTWW_OOWWW(OO_TWW, OO_WWW),

//[OO_OOO]
    OOOOO(OO_OOO),

//[OT_OOR]
    OTOOR(OT_OOR),

//[OT_ORR]
    OTORR(OT_ORR),

//[OT_OTR, WW_TWW]
    OTOTR_WWTWW(OT_OTR_P, WW_TWW),

    OTOTR_P(true, OT_OTR_P, OT_OTR),

//[OT_OTR]
    OTOTR(OT_OTR),

//[OT_ORF]
    OTORF(OT_ORF),

//[OT_ORF, OT_OWW]
    OTORF_OTOWW(OT_ORF_P, OT_OWW),

//[OT_RRF, WW_TTR]
    OTRRF_WWTTR(OT_RRF_P, WW_TTR),

//[OT_RRR, WW_TTT]
    OTRRR_WWTTT(OT_RRR_P, WW_TTT),

//[OT_OOO]
    OTOOO(OT_OOO),

//[OT_OOT]
    OTOOT(OT_OOT),

//[OT_TTR]
    OTTTR(OT_TTR),

//[OT_TRR]
    OTTRR(OT_TRR),

//[OT_TRF]
    OTTRF(OT_TRF),

//[OT_TRF, OT_TWW]
    OTTRF_OTTWW(OT_TRF_P, OT_TWW),

//[OT_RRF]
    OTRRF(OT_RRF),

//[OT_RRR]
    OTRRR(OT_RRR),

//[OT_TTT]
    OTTTT(OT_TTT),

//[OT_OTT]
    OTOTT(OT_OTT);

    public static final TurnCaseSet VALUES[] = values();


    //--------------------------------------------------------------------
    public static TurnCaseSet valueOf(Collection<TurnCase> turnCases)
    {
        List<TurnCase> asList = new ArrayList<TurnCase>(turnCases);

        return asList.size() == 1
                ? valueOf(asList.get(0))
                : valueOf(asList.get(0), asList.get(1));
    }
    public static TurnCaseSet valueOf(TurnCase turnCaseA)
    {
        for (TurnCaseSet turnCaseSet : VALUES)
        {
            if (turnCaseSet.TURN_CASES.length == 1 &&
                ( turnCaseSet.FALSE_MATCH == turnCaseA ||
                  turnCaseSet.FALSE_MATCH == null &&
                   turnCaseSet.TURN_CASES[0] == turnCaseA))
            {
                return turnCaseSet;
            }
        }
        return null;
    }

    public static TurnCaseSet valueOf(
            TurnCase turnCaseA, TurnCase turnCaseB)
    {
        TurnCase loTurnCase, hiTurnCase;
        if (turnCaseA.compareTo( turnCaseB ) < 0)
        {
            loTurnCase = turnCaseA;
            hiTurnCase = turnCaseB;
        }
        else
        {
            loTurnCase = turnCaseB;
            hiTurnCase = turnCaseA;
        }

        for (TurnCaseSet turnCaseSet : VALUES)
        {
            if (turnCaseSet.TURN_CASES.length == 2 &&
                turnCaseSet.TURN_CASES[0] == loTurnCase &&
                turnCaseSet.TURN_CASES[1] == hiTurnCase)
            {
                return turnCaseSet;
            }
        }
        return null;
    }


    //--------------------------------------------------------------------
    private final TurnCase FALSE_MATCH;
    private final TurnCase TURN_CASES[];


    //--------------------------------------------------------------------
    private TurnCaseSet(TurnCase... turnCases)
    {
        Arrays.sort(turnCases);
        TURN_CASES  = turnCases;
        FALSE_MATCH = null;
    }
    private TurnCaseSet(boolean  falseCaseMaker,
                        TurnCase falseMatch,
                        TurnCase turnCase)
    {
        FALSE_MATCH = falseMatch;
        TURN_CASES  = new TurnCase[]{turnCase};
    }


    //--------------------------------------------------------------------
    public boolean isFalsed()
    {
        return FALSE_MATCH != null;
    }

    public TurnCase trueCase()
    {
        return TURN_CASES[0];
    }

    //--------------------------------------------------------------------
    public int offset(TurnCase of)
    {
        if (FALSE_MATCH == of) return 0;

        int offset = 0;
        for (TurnCase turnCase : TURN_CASES)
        {
            if (turnCase == of) return offset;
            offset += turnCase.size();
        }
        return 0;
    }

    public int size()
    {
        int size = 0;
        for (TurnCase turnCase : TURN_CASES)
        {
            size += turnCase.size();
        }
        return size;
    }
}
