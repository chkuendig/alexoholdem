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
    OO_OTT(FIRST, FIRST, FIRST, SECOND, SECOND,
           FIRST, SECOND, THIRD),

//[ONE, ONE][TWO, TWO, THREE][TWO]
//[ONE, ONE][TWO, TWO, THREE][ONE]
//[ONE, ONE][TWO, TWO, THREE][THREE]
//[ONE, ONE][TWO, TWO, THREE][FOUR]
    OO_TTR(FIRST, FIRST, SECOND, SECOND, THIRD,
           FIRST, SECOND, THIRD, FOURTH),

//[ONE, ONE][TWO, TWO, TWO][TWO]
//[ONE, ONE][TWO, TWO, TWO][ONE]
//[ONE, ONE][TWO, TWO, TWO][THREE]
    OO_TTT(FIRST, FIRST, SECOND, SECOND, SECOND,
           FIRST, SECOND, THIRD),

//[ONE, ONE][ONE, ONE, TWO][ONE]
//[ONE, ONE][ONE, ONE, TWO][TWO]
//[ONE, ONE][ONE, ONE, TWO][THREE]
    OO_OOT(FIRST, FIRST, FIRST, FIRST, SECOND,
           FIRST, SECOND, THIRD),

//[ONE, ONE][ONE, TWO, THREE][ONE]
//[ONE, ONE][ONE, TWO, THREE][TWO]
//[ONE, ONE][ONE, TWO, THREE][THREE]
//[ONE, ONE][ONE, TWO, THREE][FOUR]
    OO_OTR(FIRST, FIRST, FIRST, SECOND, THIRD,
           FIRST, SECOND, THIRD, FOURTH),

//[ONE, ONE][ONE, TWO, THREE][TWO]
    OO_OTR_P(FIRST, FIRST, FIRST, SECOND, THIRD,
             SECOND),

//[ONE, ONE][ONE, WILD, WILD][ONE]
//[ONE, ONE][ONE, WILD, WILD][THREE]
    OO_OWW(FIRST, FIRST, FIRST, WILD, WILD,
           FIRST, THIRD),

//[ONE, ONE][TWO, THREE, FOUR][ONE]
//[ONE, ONE][TWO, THREE, FOUR][TWO]
//[ONE, ONE][TWO, THREE, FOUR][THREE]
//[ONE, ONE][TWO, THREE, FOUR][FOUR]
    OO_TRF(FIRST, FIRST, SECOND, THIRD, FOURTH,
           FIRST, SECOND, THIRD, FOURTH),

//[ONE, ONE][TWO, THREE, FOUR][THREE]
    OO_TRF_P(FIRST, FIRST, SECOND, THIRD, FOURTH,
             SECOND),

//[ONE, ONE][TWO, WILD, WILD][TWO]
    OO_TWW(FIRST, FIRST, SECOND, WILD, WILD,
           SECOND),

//[ONE, ONE][WILD, WILD, WILD][ONE]
    OO_WWW(FIRST, FIRST, WILD, WILD, WILD,
           FIRST),
    
//[ONE, ONE][ONE, ONE, ONE][ONE]
//[ONE, ONE][ONE, ONE, ONE][TWO]
    OO_OOO(FIRST, FIRST, FIRST, FIRST, FIRST,
           FIRST, SECOND),

//[ONE, ONE][THREE, WILD, WILD][ONE]
//[ONE, ONE][THREE, WILD, WILD][THREE]
    OO_RWW(FIRST, FIRST, THIRD, WILD, WILD,
           FIRST, THIRD),

//[ONE, TWO][ONE, ONE, THREE][THREE]
//[ONE, TWO][ONE, ONE, THREE][ONE]
//[ONE, TWO][ONE, ONE, THREE][TWO]
//[ONE, TWO][ONE, ONE, THREE][FOUR]
    OT_OOR(FIRST, SECOND, FIRST, FIRST, THIRD,
           FIRST, SECOND, THIRD, FOURTH),

//[ONE, TWO][ONE, THREE, THREE][THREE]
//[ONE, TWO][ONE, THREE, THREE][ONE]
//[ONE, TWO][ONE, THREE, THREE][TWO]
//[ONE, TWO][ONE, THREE, THREE][FOUR]
    OT_ORR(FIRST, SECOND, FIRST, THIRD, THIRD,
           FIRST, SECOND, THIRD, FOURTH),

//[WILD, WILD][TWO, WILD, WILD][TWO]
//[WILD, WILD][TWO, WILD, WILD][THREE]
    WW_TWW(WILD, WILD, SECOND, WILD, WILD,
           SECOND, THIRD),

//[ONE, TWO][ONE, TWO, THREE][ONE]
//[ONE, TWO][ONE, TWO, THREE][TWO]
//[ONE, TWO][ONE, TWO, THREE][THREE]
//[ONE, TWO][ONE, TWO, THREE][FOUR]
    OT_OTR(FIRST, SECOND, FIRST, SECOND, THIRD,
           FIRST, SECOND, THIRD, FOURTH),

    OT_OTR_P(FIRST, SECOND, FIRST, SECOND, THIRD,
             FIRST),

//[ONE, TWO][ONE, THREE, FOUR][THREE]
//[ONE, TWO][ONE, THREE, FOUR][ONE]
//[ONE, TWO][ONE, THREE, FOUR][TWO]
//[ONE, TWO][ONE, THREE, FOUR][FOUR]
    OT_ORF(FIRST, SECOND, FIRST, THIRD, FOURTH,
           FIRST, SECOND, THIRD, FOURTH),

    OT_ORF_P(FIRST, SECOND, FIRST, THIRD, FOURTH,
             THIRD),

//[ONE, TWO][ONE, WILD, WILD][ONE]
//[ONE, TWO][ONE, WILD, WILD][TWO]
    OT_OWW(FIRST, SECOND, FIRST, WILD, WILD,
           FIRST, SECOND),

//[WILD, WILD][TWO, TWO, THREE][TWO]
//[WILD, WILD][TWO, TWO, THREE][THREE]
    WW_TTR(WILD, WILD, SECOND, SECOND, THIRD,
           SECOND, THIRD),

//[ONE, TWO][THREE, THREE, FOUR][ONE]
//[ONE, TWO][THREE, THREE, FOUR][TWO]
//[ONE, TWO][THREE, THREE, FOUR][THREE]
//[ONE, TWO][THREE, THREE, FOUR][FOUR]
    OT_RRF(FIRST, SECOND, THIRD, THIRD, FOURTH,
           FIRST, SECOND, THIRD, FOURTH),

    OT_RRF_P(FIRST, SECOND, THIRD, THIRD, FOURTH,
             FIRST),

//[WILD, WILD][TWO, TWO, TWO][TWO]
//[WILD, WILD][TWO, TWO, TWO][THREE]
    WW_TTT(WILD, WILD, SECOND, SECOND, SECOND,
           SECOND, THIRD),

//[ONE, TWO][THREE, THREE, THREE][ONE]
//[ONE, TWO][THREE, THREE, THREE][TWO]
//[ONE, TWO][THREE, THREE, THREE][THREE]
//[ONE, TWO][THREE, THREE, THREE][FOUR]
    OT_RRR(FIRST, SECOND, THIRD, THIRD, THIRD,
           FIRST, SECOND, THIRD, FOURTH),

    OT_RRR_P(FIRST, SECOND, THIRD, THIRD, THIRD,
             FIRST),

//[ONE, TWO][ONE, ONE, ONE][ONE]
//[ONE, TWO][ONE, ONE, ONE][TWO]
//[ONE, TWO][ONE, ONE, ONE][THREE]
    OT_OOO(FIRST, SECOND, FIRST, FIRST, FIRST,
           FIRST, SECOND, THIRD),
    
//[ONE, TWO][ONE, ONE, TWO][ONE]
//[ONE, TWO][ONE, ONE, TWO][TWO]
//[ONE, TWO][ONE, ONE, TWO][THREE]
    OT_OOT(FIRST, SECOND, FIRST, FIRST, SECOND,
           FIRST, SECOND, THIRD),
    
//[ONE, TWO][ONE, TWO, TWO][ONE]
//[ONE, TWO][ONE, TWO, TWO][TWO]    
//[ONE, TWO][ONE, TWO, TWO][THREE]
    OT_OTT(FIRST, SECOND, FIRST, SECOND, SECOND,
           FIRST, SECOND, THIRD),
    
//[ONE, TWO][TWO, THREE, FOUR][ONE]
//[ONE, TWO][TWO, THREE, FOUR][TWO]
//[ONE, TWO][TWO, THREE, FOUR][THREE]
//[ONE, TWO][TWO, THREE, FOUR][FOUR]
    OT_TRF(FIRST, SECOND, SECOND, THIRD, FOURTH,
           FIRST, SECOND, THIRD, FOURTH),

    OT_TRF_P(FIRST, SECOND, SECOND, THIRD, FOURTH,
             THIRD),

//[ONE, TWO][TWO, THREE, THREE][ONE]
//[ONE, TWO][TWO, THREE, THREE][TWO]
//[ONE, TWO][TWO, THREE, THREE][THREE]
//[ONE, TWO][TWO, THREE, THREE][FOUR]
    OT_TRR(FIRST, SECOND, SECOND, THIRD, THIRD,
           FIRST, SECOND, THIRD, FOURTH),
    
//[ONE, TWO][TWO, TWO, THREE][ONE]
//[ONE, TWO][TWO, TWO, THREE][TWO]
//[ONE, TWO][TWO, TWO, THREE][THREE]
//[ONE, TWO][TWO, TWO, THREE][FOUR]
    OT_TTR(FIRST, SECOND, SECOND, SECOND, THIRD,
           FIRST, SECOND, THIRD, FOURTH),
    
//[ONE, TWO][TWO, TWO, TWO][ONE]
//[ONE, TWO][TWO, TWO, TWO][THREE]
//[ONE, TWO][TWO, TWO, TWO][TWO]
    OT_TTT(FIRST, SECOND, SECOND, SECOND, SECOND,
           FIRST, SECOND, THIRD),
    
//[ONE, TWO][TWO, WILD, WILD][ONE]
//[ONE, TWO][TWO, WILD, WILD][TWO]
    OT_TWW(FIRST, SECOND, SECOND, WILD, WILD,
           FIRST, SECOND)
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

        if (holeA == FIRST)
        {
            if (holeB == FIRST)
            {
                if (flopA == FIRST)
                {
                    //[ONE, ONE][ONE, ONE, ONE]
                    //[ONE, ONE][ONE, ONE, TWO]
                    //[ONE, ONE][ONE, TWO, TWO]
                    //[ONE, ONE][ONE, TWO, THREE]
                    //[ONE, ONE][ONE, WILD, WILD]
                    return   flopB == FIRST
                           ? (flopC == FIRST
                              ? OO_OOO
                              : OO_OOT)
                           : flopB == SECOND
                           ? (flopC == SECOND
                              ? OO_OTT
                              : (flopCardB.rank() == flopCardC.rank()
                                 ? OO_OTR_P : OO_OTR))
                           : OO_OWW;
                }
                else if (flopA == SECOND)
                {
                    //[ONE, ONE][TWO, TWO, TWO]
                    //[ONE, ONE][TWO, TWO, THREE]
                    //[ONE, ONE][TWO, THREE, FOUR]
                    //[ONE, ONE][TWO, WILD, WILD]
                    return flopB == SECOND
                           ? (flopC == SECOND
                              ? OO_TTT
                              : OO_TTR)
                           : (flopB == THIRD
                              ? (flopCardA.rank() == flopCardB.rank()
                                 ? OO_TRF_P : OO_TRF)
                              : OO_TWW);
                }
                else
                {
                    //[ONE, ONE][THREE, WILD, WILD]
                    //[ONE, ONE][WILD, WILD, WILD]
                    return flopA == THIRD
                           ? OO_RWW : OO_WWW;
                }
            }
            else //if (holeB == TWO)
            {
                if (flopA == FIRST)
                {
                    return
                           //[ONE, TWO][ONE, ONE, ONE]
                           //[ONE, TWO][ONE, ONE, TWO]
                           //[ONE, TWO][ONE, ONE, THREE]
                              flopB == FIRST
                           ? (  flopC == FIRST
                              ? OT_OOO
                              : flopC == SECOND
                              ? OT_OOT
                              : OT_OOR)

                           //[ONE, TWO][ONE, TWO, TWO]
                           //[ONE, TWO][ONE, TWO, THREE]
                           :  flopB == SECOND
                           ? (  flopC == SECOND
                              ? OT_OTT
                              : (flopCardA.rank() == flopCardB.rank()
                                 ? OT_OTR_P : OT_OTR))

                           //[ONE, TWO][ONE, THREE, THREE]
                           //[ONE, TWO][ONE, THREE, FOUR]
                           :  flopB == THIRD
                           ? (  flopC == THIRD
                              ? OT_ORR
                              : (flopCardB.rank() == flopCardC.rank()
                                 ? OT_ORF_P : OT_ORF))

                           //[ONE, TWO][ONE, WILD, WILD]
                           : OT_OWW;
                }
                else if (flopA == SECOND)
                {
                    //[ONE, TWO][TWO, TWO, TWO]
                    //[ONE, TWO][TWO, TWO, THREE]
                    //[ONE, TWO][TWO, THREE, THREE]
                    //[ONE, TWO][TWO, THREE, FOUR]
                    //[ONE, TWO][TWO, WILD, WILD]
                    return   flopB == SECOND
                           ? (  flopC == SECOND
                              ? OT_TTT
                              : OT_TTR)

                           : flopB == THIRD
                           ? (  flopC == THIRD
                              ? OT_TRR
                              : (flopCardB.rank() == flopCardC.rank()
                                 ? OT_TRF_P : OT_TRF))

                           : OT_TWW;
                }
                else // if (flopA == THREE)
                {
                    //[ONE, TWO][THREE, THREE, THREE]
                    //[ONE, TWO][THREE, THREE, FOUR]
                    return flopC == THIRD
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
            return flopC == SECOND
                   ? WW_TTT
                   : flopC == THIRD
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
