package ao.bucket.index.iso_turn;

import ao.bucket.index.iso_cards.wild.card.WildCard;
import ao.bucket.index.iso_cards.wild.suit.WildSuit;
import static ao.bucket.index.iso_cards.wild.suit.WildSuit.*;
import ao.util.data.Arr;

/**
 * Date: Aug 31, 2008
 * Time: 4:38:25 PM
 */
public enum TurnCase
{
//[ONE, ONE][ONE, TWO, TWO][ONE]
//[ONE, ONE][ONE, TWO, TWO][TWO]
//[ONE, ONE][ONE, TWO, TWO][THREE]
    OO_OTT(ONE, ONE, ONE, TWO, TWO,
           ONE, TWO, THREE),

//[ONE, ONE][TWO, TWO, THREE][TWO]
//[ONE, ONE][TWO, TWO, THREE][ONE]
//[ONE, ONE][TWO, TWO, THREE][THREE]
//[ONE, ONE][TWO, TWO, THREE][FOUR]
    OO_TTR(ONE, ONE, TWO, TWO, THREE,
           ONE, TWO, THREE, FOUR),

//[ONE, ONE][TWO, TWO, TWO][TWO]
//[ONE, ONE][TWO, TWO, TWO][ONE]
//[ONE, ONE][TWO, TWO, TWO][THREE]
    OO_TTT(ONE, ONE, TWO, TWO, TWO,
           ONE, TWO, THREE),

//[ONE, ONE][ONE, ONE, TWO][ONE]
//[ONE, ONE][ONE, ONE, TWO][TWO]
//[ONE, ONE][ONE, ONE, TWO][THREE]
    OO_OOT(ONE, ONE, ONE, ONE, TWO,
           ONE, TWO, THREE),

//[ONE, ONE][ONE, TWO, THREE][ONE]
//[ONE, ONE][ONE, TWO, THREE][TWO]
//[ONE, ONE][ONE, TWO, THREE][THREE]
//[ONE, ONE][ONE, TWO, THREE][FOUR]
    OO_OTR(ONE, ONE, ONE, TWO, THREE,
           ONE, TWO, THREE, FOUR),

//[ONE, ONE][ONE, TWO, THREE][TWO]
    OO_OTR_P(ONE, ONE, ONE, TWO, THREE,
             TWO),

//[ONE, ONE][ONE, WILD, WILD][ONE]
//[ONE, ONE][ONE, WILD, WILD][THREE]
    OO_OWW(ONE, ONE, ONE, WILD, WILD,
           ONE, THREE),

//[ONE, ONE][TWO, THREE, FOUR][ONE]
//[ONE, ONE][TWO, THREE, FOUR][TWO]
//[ONE, ONE][TWO, THREE, FOUR][THREE]
//[ONE, ONE][TWO, THREE, FOUR][FOUR]
    OO_TRF(ONE, ONE, TWO, THREE, FOUR,
           ONE, TWO, THREE, FOUR),

//[ONE, ONE][TWO, THREE, FOUR][THREE]
    OO_TRF_P(ONE, ONE, TWO, THREE, FOUR,
             TWO),

//[ONE, ONE][TWO, WILD, WILD][TWO]
    OO_TWW(ONE, ONE, TWO, WILD, WILD,
           TWO),

//[ONE, ONE][WILD, WILD, WILD][ONE]
    OO_WWW(ONE, ONE, WILD, WILD, WILD,
           ONE),
    
//[ONE, ONE][ONE, ONE, ONE][ONE]
//[ONE, ONE][ONE, ONE, ONE][TWO]
    OO_OOO(ONE, ONE, ONE, ONE, ONE,
           ONE, TWO),

//[ONE, ONE][THREE, WILD, WILD][ONE]
//[ONE, ONE][THREE, WILD, WILD][THREE]
    OO_RWW(ONE, ONE, THREE, WILD, WILD,
           ONE, THREE),

//[ONE, TWO][ONE, ONE, THREE][THREE]
//[ONE, TWO][ONE, ONE, THREE][ONE]
//[ONE, TWO][ONE, ONE, THREE][TWO]
//[ONE, TWO][ONE, ONE, THREE][FOUR]
    OT_OOR(ONE, TWO, ONE, ONE, THREE,
           ONE, TWO, THREE, FOUR),

//[ONE, TWO][ONE, THREE, THREE][THREE]
//[ONE, TWO][ONE, THREE, THREE][ONE]
//[ONE, TWO][ONE, THREE, THREE][TWO]
//[ONE, TWO][ONE, THREE, THREE][FOUR]
    OT_ORR(ONE, TWO, ONE, THREE, THREE,
           ONE, TWO, THREE, FOUR),

//[WILD, WILD][TWO, WILD, WILD][TWO]
//[WILD, WILD][TWO, WILD, WILD][THREE]
    WW_TWW(WILD, WILD, TWO, WILD, WILD,
           TWO, THREE),

//[ONE, TWO][ONE, TWO, THREE][ONE]
//[ONE, TWO][ONE, TWO, THREE][TWO]
//[ONE, TWO][ONE, TWO, THREE][THREE]
//[ONE, TWO][ONE, TWO, THREE][FOUR]
    OT_OTR(ONE, TWO, ONE, TWO, THREE,
           ONE, TWO, THREE, FOUR),

    OT_OTR_P(ONE, TWO, ONE, TWO, THREE,
             ONE),

//[ONE, TWO][ONE, THREE, FOUR][THREE]
//[ONE, TWO][ONE, THREE, FOUR][ONE]
//[ONE, TWO][ONE, THREE, FOUR][TWO]
//[ONE, TWO][ONE, THREE, FOUR][FOUR]
    OT_ORF(ONE, TWO, ONE, THREE, FOUR,
           ONE, TWO, THREE, FOUR),

    OT_ORF_P(ONE, TWO, ONE, THREE, FOUR,
             THREE),

//[ONE, TWO][ONE, WILD, WILD][ONE]
//[ONE, TWO][ONE, WILD, WILD][TWO]
    OT_OWW(ONE, TWO, ONE, WILD, WILD,
           ONE, TWO),

//[WILD, WILD][TWO, TWO, THREE][TWO]
//[WILD, WILD][TWO, TWO, THREE][THREE]
    WW_TTR(WILD, WILD, TWO, TWO, THREE,
           TWO, THREE),

//[ONE, TWO][THREE, THREE, FOUR][ONE]
//[ONE, TWO][THREE, THREE, FOUR][TWO]
//[ONE, TWO][THREE, THREE, FOUR][THREE]
//[ONE, TWO][THREE, THREE, FOUR][FOUR]
    OT_RRF(ONE, TWO, THREE, THREE, FOUR,
           ONE, TWO, THREE, FOUR),

    OT_RRF_P(ONE, TWO, THREE, THREE, FOUR,
             ONE),

//[WILD, WILD][TWO, TWO, TWO][TWO]
//[WILD, WILD][TWO, TWO, TWO][THREE]
    WW_TTT(WILD, WILD, TWO, TWO, TWO,
           TWO, THREE),

//[ONE, TWO][THREE, THREE, THREE][ONE]
//[ONE, TWO][THREE, THREE, THREE][TWO]
//[ONE, TWO][THREE, THREE, THREE][THREE]
//[ONE, TWO][THREE, THREE, THREE][FOUR]
    OT_RRR(ONE, TWO, THREE, THREE, THREE,
           ONE, TWO, THREE, FOUR),

    OT_RRR_P(ONE, TWO, THREE, THREE, THREE,
             ONE),

//[ONE, TWO][ONE, ONE, ONE][ONE]
//[ONE, TWO][ONE, ONE, ONE][TWO]
//[ONE, TWO][ONE, ONE, ONE][THREE]
    OT_OOO(ONE, TWO, ONE, ONE, ONE,
           ONE, TWO, THREE),
    
//[ONE, TWO][ONE, ONE, TWO][ONE]
//[ONE, TWO][ONE, ONE, TWO][TWO]
//[ONE, TWO][ONE, ONE, TWO][THREE]
    OT_OOT(ONE, TWO, ONE, ONE, TWO,
           ONE, TWO, THREE),
    
//[ONE, TWO][ONE, TWO, TWO][ONE]
//[ONE, TWO][ONE, TWO, TWO][TWO]    
//[ONE, TWO][ONE, TWO, TWO][THREE]
    OT_OTT(ONE, TWO, ONE, TWO, TWO,
           ONE, TWO, THREE),
    
//[ONE, TWO][TWO, THREE, FOUR][ONE]
//[ONE, TWO][TWO, THREE, FOUR][TWO]
//[ONE, TWO][TWO, THREE, FOUR][THREE]
//[ONE, TWO][TWO, THREE, FOUR][FOUR]
    OT_TRF(ONE, TWO, TWO, THREE, FOUR,
           ONE, TWO, THREE, FOUR),

    OT_TRF_P(ONE, TWO, TWO, THREE, FOUR,
             THREE),

//[ONE, TWO][TWO, THREE, THREE][ONE]
//[ONE, TWO][TWO, THREE, THREE][TWO]
//[ONE, TWO][TWO, THREE, THREE][THREE]
//[ONE, TWO][TWO, THREE, THREE][FOUR]
    OT_TRR(ONE, TWO, TWO, THREE, THREE,
           ONE, TWO, THREE, FOUR),
    
//[ONE, TWO][TWO, TWO, THREE][ONE]
//[ONE, TWO][TWO, TWO, THREE][TWO]
//[ONE, TWO][TWO, TWO, THREE][THREE]
//[ONE, TWO][TWO, TWO, THREE][FOUR]
    OT_TTR(ONE, TWO, TWO, TWO, THREE,
           ONE, TWO, THREE, FOUR),
    
//[ONE, TWO][TWO, TWO, TWO][ONE]
//[ONE, TWO][TWO, TWO, TWO][THREE]
//[ONE, TWO][TWO, TWO, TWO][TWO]
    OT_TTT(ONE, TWO, TWO, TWO, TWO,
           ONE, TWO, THREE),
    
//[ONE, TWO][TWO, WILD, WILD][ONE]
//[ONE, TWO][TWO, WILD, WILD][TWO]
    OT_TWW(ONE, TWO, TWO, WILD, WILD,
           ONE, TWO)
    ;
    
    public static final TurnCase VALUES[] = values();


    //--------------------------------------------------------------------
    public static TurnCase valueOf(
            WildCard holeCardA, WildCard holeCardB,
            WildCard flopCardA, WildCard flopCardB, WildCard flopCardC,
            WildCard turnCard)
    {
        WildSuit holeA = holeCardA.suit(),
                 holeB = holeCardB.suit(),
                 flopA = flopCardA.suit(),
                 flopB = flopCardB.suit(),
                 flopC = flopCardC.suit();

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
                              : (flopCardB.rank() == flopCardC.rank()
                                 ? OO_OTR_P : OO_OTR))
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
                              ? (flopCardA.rank() == flopCardB.rank()
                                 ? OO_TRF_P : OO_TRF)
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
                              : (flopCardA.rank() == flopCardB.rank()
                                 ? OT_OTR_P : OT_OTR))

                           //[ONE, TWO][ONE, THREE, THREE]
                           //[ONE, TWO][ONE, THREE, FOUR]
                           :  flopB == THREE
                           ? (  flopC == THREE
                              ? OT_ORR
                              : (flopCardB.rank() == flopCardC.rank()
                                 ? OT_ORF_P : OT_ORF))

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
                              : (flopCardB.rank() == flopCardC.rank()
                                 ? OT_TRF_P : OT_TRF))

                           : OT_TWW;
                }
                else // if (flopA == THREE)
                {
                    //[ONE, TWO][THREE, THREE, THREE]
                    //[ONE, TWO][THREE, THREE, FOUR]
                    return flopC == THREE
                           ? (holeCardA.rank() == holeCardB.rank()
                              ? OT_RRR_P : OT_RRR)
                           : (holeCardA.rank() == holeCardB.rank()
                              ? OT_RRF_P : OT_RRF);
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
    private final int      PRETURN_SUIT_MATCHES[];
    

    //--------------------------------------------------------------------
    private TurnCase(
            WildSuit holeA, WildSuit holeB,
            WildSuit flopA, WildSuit flopB, WildSuit flopC,
            WildSuit... turnSuits)
    {
        TURN_SUITS           = turnSuits;
        PRETURN_SUIT_MATCHES = new int[ TURN_SUITS.length ];
        
        for (int i = 0; i < turnSuits.length; i++)
        {
            int matches = 0;
            
            if (turnSuits[i] == holeA) matches++;
            if (turnSuits[i] == holeB) matches++;
            if (turnSuits[i] == flopA) matches++;
            if (turnSuits[i] == flopB) matches++;
            if (turnSuits[i] == flopC) matches++;
            
            PRETURN_SUIT_MATCHES[i] = matches;
        }
    }

    
    //--------------------------------------------------------------------
    public int localOffset(WildSuit forTurn)
    {
        int offset = 0;
        for (int i = 0; i < TURN_SUITS.length; i++)
        {
            if (TURN_SUITS[i] == forTurn)
            {
                return offset;
            }
            offset += size(i);
        }
        return -1;
    }
    
    
    //--------------------------------------------------------------------
    private int size(int ofTurnIndex)
    {
        return 13 - PRETURN_SUIT_MATCHES[ ofTurnIndex ];
    }
    
    public int size()
    {
        int size = 0;
        for (int i = 0; i < TURN_SUITS.length; i++)
        {
            size += size(i);
        }
        return size;
    }
    
    
    //--------------------------------------------------------------------
    public int indexOf(WildSuit turn)
    {
        return Arr.indexOf(TURN_SUITS, turn);
    }
}
