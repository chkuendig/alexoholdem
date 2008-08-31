package ao.bucket.index.iso_turn;

import ao.bucket.index.iso_cards.wild.suit.WildSuit;
import static ao.bucket.index.iso_cards.wild.suit.WildSuit.*;
import ao.util.data.Arr;

/**
 * Date: Aug 31, 2008
 * Time: 4:38:25 PM
 */
public enum TurnSubCase
{
//[ONE, ONE][ONE, TWO, TWO][ONE]
//[ONE, ONE][ONE, TWO, TWO][TWO]
//[ONE, ONE][ONE, TWO, TWO][THREE]
    OO_OTT(ONE, TWO, THREE),

//[ONE, ONE][TWO, TWO, THREE][TWO]
//[ONE, ONE][TWO, TWO, THREE][ONE]
//[ONE, ONE][TWO, TWO, THREE][THREE]
//[ONE, ONE][TWO, TWO, THREE][FOUR]
    OO_TTR(ONE, TWO, THREE, FOUR),

//[ONE, ONE][TWO, TWO, TWO][TWO]
//[ONE, ONE][TWO, TWO, TWO][ONE]
//[ONE, ONE][TWO, TWO, TWO][THREE]
    OO_TTT(ONE, TWO, THREE),

//[ONE, ONE][ONE, ONE, TWO][TWO]
//[ONE, ONE][ONE, ONE, TWO][ONE]
//[ONE, ONE][ONE, ONE, TWO][THREE]
    OO_OOT(ONE, TWO, THREE),

//[ONE, ONE][ONE, TWO, THREE][TWO]
//[ONE, ONE][ONE, TWO, THREE][ONE]
//[ONE, ONE][ONE, TWO, THREE][THREE]
//[ONE, ONE][ONE, TWO, THREE][FOUR]
    OO_OTR(ONE, TWO, THREE, FOUR),

//[ONE, ONE][ONE, WILD, WILD][ONE]
//[ONE, ONE][ONE, WILD, WILD][THREE]
    OO_OWW(ONE, THREE),

//[ONE, ONE][TWO, THREE, FOUR][TWO]
//[ONE, ONE][TWO, THREE, FOUR][ONE]
//[ONE, ONE][TWO, THREE, FOUR][FOUR]
//[ONE, ONE][TWO, THREE, FOUR][THREE]
    OO_TRF(ONE, TWO, THREE, FOUR),

//[ONE, ONE][TWO, WILD, WILD][TWO]
    OO_TWW(TWO),

//[ONE, ONE][WILD, WILD, WILD][ONE]
    OO_WWW(ONE),

//[ONE, ONE][ONE, ONE, ONE][TWO]
//[ONE, ONE][ONE, ONE, ONE][ONE]
    OO_OOO(ONE, TWO),

//[ONE, ONE][THREE, WILD, WILD][THREE]
//[ONE, ONE][THREE, WILD, WILD][ONE]
    OO_RWW(ONE, THREE),

//[ONE, TWO][ONE, ONE, THREE][THREE]
//[ONE, TWO][ONE, ONE, THREE][ONE]
//[ONE, TWO][ONE, ONE, THREE][TWO]
//[ONE, TWO][ONE, ONE, THREE][FOUR]
    OT_OOR(ONE, TWO, THREE, FOUR),

//[ONE, TWO][ONE, THREE, THREE][THREE]
//[ONE, TWO][ONE, THREE, THREE][ONE]
//[ONE, TWO][ONE, THREE, THREE][TWO]
//[ONE, TWO][ONE, THREE, THREE][FOUR]
    OT_ORR(ONE, TWO, THREE, FOUR),

//[WILD, WILD][TWO, WILD, WILD][TWO]
//[WILD, WILD][TWO, WILD, WILD][THREE]
    WW_TWW(TWO, THREE),

//[ONE, TWO][ONE, TWO, THREE][ONE]
//[ONE, TWO][ONE, TWO, THREE][THREE]
//[ONE, TWO][ONE, TWO, THREE][TWO]
//[ONE, TWO][ONE, TWO, THREE][FOUR]
    OT_OTR(ONE, TWO, THREE, FOUR),

//[ONE, TWO][ONE, THREE, FOUR][THREE]
//[ONE, TWO][ONE, THREE, FOUR][ONE]
//[ONE, TWO][ONE, THREE, FOUR][TWO]
//[ONE, TWO][ONE, THREE, FOUR][FOUR]
    OT_ORF(ONE, TWO, THREE, FOUR),

//[ONE, TWO][ONE, WILD, WILD][ONE]
//[ONE, TWO][ONE, WILD, WILD][TWO]
    OT_OWW(ONE, TWO),

//[WILD, WILD][TWO, TWO, THREE][TWO]
//[WILD, WILD][TWO, TWO, THREE][THREE]
    WW_TTR(TWO, THREE),

//[ONE, TWO][THREE, THREE, FOUR][ONE]
//[ONE, TWO][THREE, THREE, FOUR][TWO]
//[ONE, TWO][THREE, THREE, FOUR][THREE]
//[ONE, TWO][THREE, THREE, FOUR][FOUR]
    OT_RRF(ONE, TWO, THREE, FOUR),

//[WILD, WILD][TWO, TWO, TWO][TWO]
//[WILD, WILD][TWO, TWO, TWO][THREE]
    WW_TTT(TWO, THREE),

//[ONE, TWO][THREE, THREE, THREE][ONE]
//[ONE, TWO][THREE, THREE, THREE][TWO]
//[ONE, TWO][THREE, THREE, THREE][THREE]
//[ONE, TWO][THREE, THREE, THREE][FOUR]
    OT_RRR(ONE, TWO, THREE, FOUR),

//[ONE, TWO][ONE, ONE, ONE][ONE]
//[ONE, TWO][ONE, ONE, ONE][TWO]
//[ONE, TWO][ONE, ONE, ONE][THREE]
    OT_OOO(ONE, TWO, THREE),
    
//[ONE, TWO][ONE, ONE, TWO][ONE]
//[ONE, TWO][ONE, ONE, TWO][TWO]
//[ONE, TWO][ONE, ONE, TWO][THREE]
    OT_OOT(ONE, TWO, THREE),
    
//[ONE, TWO][ONE, TWO, TWO][ONE]
//[ONE, TWO][ONE, TWO, TWO][TWO]    
//[ONE, TWO][ONE, TWO, TWO][THREE]
    OT_OTT(ONE, TWO, THREE),
    
//[ONE, TWO][TWO, THREE, FOUR][ONE]
//[ONE, TWO][TWO, THREE, FOUR][TWO]
//[ONE, TWO][TWO, THREE, FOUR][THREE]
//[ONE, TWO][TWO, THREE, FOUR][FOUR]
    OT_TRF(ONE, TWO, THREE, FOUR),


//[ONE, TWO][TWO, THREE, THREE][ONE]
//[ONE, TWO][TWO, THREE, THREE][TWO]
//[ONE, TWO][TWO, THREE, THREE][THREE]
//[ONE, TWO][TWO, THREE, THREE][FOUR]
    OT_TRR(ONE, TWO, THREE, FOUR),
    
//[ONE, TWO][TWO, TWO, THREE][ONE]
//[ONE, TWO][TWO, TWO, THREE][TWO]
//[ONE, TWO][TWO, TWO, THREE][THREE]
//[ONE, TWO][TWO, TWO, THREE][FOUR]
    OT_TTR(ONE, TWO, THREE, FOUR),
    
//[ONE, TWO][TWO, TWO, TWO][ONE]
//[ONE, TWO][TWO, TWO, TWO][THREE]
//[ONE, TWO][TWO, TWO, TWO][TWO]
    OT_TTT(ONE, TWO, THREE),
    
//[ONE, TWO][TWO, WILD, WILD][ONE]
//[ONE, TWO][TWO, WILD, WILD][TWO]
    OT_TWW(ONE, TWO)
    ;
    
    public static final TurnSubCase VALUES[] = values();
    

    //--------------------------------------------------------------------
    public static TurnSubCase valueOf(
            WildSuit holeA, WildSuit holeB,
            WildSuit flopA, WildSuit flopB, WildSuit flopC)
    {
        if (holeA == ONE)
        {
            if (holeB == ONE)
            {
                if (flopA == ONE)
                {
                    //[ONE, ONE][ONE, ONE, ONE]
                    //[ONE, ONE][ONE, ONE, TWO]
                    //[ONE, ONE][ONE, TWO, TWO]
                    //[ONE, ONE][ONE, TWO, THREE]
                    //[ONE, ONE][ONE, WILD, WILD]
                    return   flopB == ONE
                           ? (flopC == ONE
                              ? OO_OOO
                              : OO_OOT)
                           : flopB == TWO
                           ? (flopC == TWO
                              ? OO_OTT
                              : OO_OTR)
                           : OO_OWW;
                }
                else if (flopA == TWO)
                {
                    //[ONE, ONE][TWO, TWO, TWO]
                    //[ONE, ONE][TWO, TWO, THREE]
                    //[ONE, ONE][TWO, THREE, FOUR]
                    //[ONE, ONE][TWO, WILD, WILD]
                    return flopB == TWO
                           ? (flopC == TWO
                              ? OO_TTT
                              : OO_TTR)
                           : (flopB == THREE
                              ? OO_TRF
                              : OO_TWW);
                }
                else
                {
                    //[ONE, ONE][THREE, WILD, WILD]
                    //[ONE, ONE][WILD, WILD, WILD]
                    return flopA == THREE
                           ? OO_RWW : OO_WWW;
                }
            }
            else //if (holeB == TWO)
            {
                if (flopA == ONE)
                {
                    return
                           //[ONE, TWO][ONE, ONE, ONE]
                           //[ONE, TWO][ONE, ONE, TWO]
                           //[ONE, TWO][ONE, ONE, THREE]
                              flopB == ONE
                           ? (  flopC == ONE
                              ? OT_OOO
                              : flopC == TWO
                              ? OT_OOT
                              : OT_OOR)

                           //[ONE, TWO][ONE, TWO, TWO]
                           //[ONE, TWO][ONE, TWO, THREE]
                           :  flopB == TWO
                           ? (  flopC == TWO
                              ? OT_OTT
                              : OT_OTR)

                           //[ONE, TWO][ONE, THREE, THREE]
                           //[ONE, TWO][ONE, THREE, FOUR]
                           :  flopB == THREE
                           ? (  flopC == THREE
                              ? OT_ORR
                              : OT_ORF)

                           //[ONE, TWO][ONE, WILD, WILD]
                           : OT_OWW;
                }
                else if (flopA == TWO)
                {
                    //[ONE, TWO][TWO, TWO, TWO]
                    //[ONE, TWO][TWO, TWO, THREE]
                    //[ONE, TWO][TWO, THREE, THREE]
                    //[ONE, TWO][TWO, THREE, FOUR]
                    //[ONE, TWO][TWO, WILD, WILD]
                    return   flopB == TWO
                           ? (  flopC == TWO
                              ? OT_TTT
                              : OT_TTR)

                           : flopB == THREE
                           ? (  flopC == THREE
                              ? OT_TRR
                              : OT_TRF)

                           : OT_TWW;
                }
                else // if (flopA == THREE)
                {
                    //[ONE, TWO][THREE, THREE, THREE]
                    //[ONE, TWO][THREE, THREE, FOUR]
                    return flopC == THREE
                           ? OT_RRR
                           : OT_RRF;
                }
            }
        }
        else
        {
            //[WILD, WILD][TWO, TWO, THREE]
            //[WILD, WILD][TWO, TWO, TWO]
            //[WILD, WILD][TWO, WILD, WILD]
            return flopC == TWO
                   ? WW_TTT
                   : flopC == THREE
                     ? WW_TTR
                     : WW_TWW;
        }
    }


    //--------------------------------------------------------------------
    private final WildSuit TURN_SUITS[];


    //--------------------------------------------------------------------
    private TurnSubCase(WildSuit... turnSuits)
    {
        TURN_SUITS = turnSuits;
    }


    //--------------------------------------------------------------------
    public int indexOf(WildSuit turn)
    {
        return Arr.indexOf(TURN_SUITS, turn);
    }
}
