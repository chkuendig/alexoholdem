package ao.bucket.index.detail.flop;

import ao.bucket.index.flop.Flop;
import ao.holdem.model.card.Card;
import ao.odds.agglom.Odds;
import ao.odds.agglom.impl.PreciseHeadsUpOdds;

/**
 * Date: Jan 13, 2009
 * Time: 11:48:01 AM
 */
public class CanonFlopDetailBuffer
{
    //--------------------------------------------------------------------
    public static final CanonFlopDetailBuffer
            SENTINAL = new CanonFlopDetailBuffer();


    //--------------------------------------------------------------------
    private int  canonIndex     = -1;
    private Card flopA          = null;
    private Card flopB          = null;
    private Card flopC          = null;
    private byte represents     = 0;
    private Odds headsUpOdds    = null;
    private int  firstCanonTurn = -1;
    private byte canonTurnCount = 0;


    //--------------------------------------------------------------------
    private CanonFlopDetailBuffer()
    {
        flopA = Card.TWO_OF_CLUBS;
        flopB = Card.TWO_OF_CLUBS;
        flopC = Card.TWO_OF_CLUBS;
        headsUpOdds = new Odds();
    }

    public CanonFlopDetailBuffer(Flop flop, Odds odds)
    {
        canonIndex  = flop.canonIndex();

        flopA       = flop.community().flopA();
        flopB       = flop.community().flopB();
        flopC       = flop.community().flopC();

        headsUpOdds =
            odds != null ? odds :
            new PreciseHeadsUpOdds().compute(
                    flop.hole(), flop.community());
    }


    //--------------------------------------------------------------------
    public void addCanonTurn(int canonTurn)
    {
        if (firstCanonTurn == -1)
        {
            firstCanonTurn = canonTurn;
        }
        else if (firstCanonTurn > canonTurn)
        {
            firstCanonTurn = canonTurn;
        }

        canonTurnCount++;
    }

    public void setTurnInfo(int firstTurn, byte turnCount)
    {
        firstCanonTurn = firstTurn;
        canonTurnCount = turnCount;
    }


    //--------------------------------------------------------------------
    public void incrementFlopRepresentation()
    {
        represents++;
    }


    //--------------------------------------------------------------------
    public CanonFlopDetail toDetail()
    {
        return new CanonFlopDetail(
                canonIndex,
                flopA, flopB, flopC,
                represents, headsUpOdds,
                firstCanonTurn, canonTurnCount);
    }
}
