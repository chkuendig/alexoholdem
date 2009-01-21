package ao.bucket.index.detail.turn;

import ao.bucket.index.turn.Turn;
import ao.holdem.model.card.Card;
import ao.odds.agglom.Odds;

/**
 * Date: Jan 21, 2009
 * Time: 2:39:13 AM
 */
public class TurnDetailBuffer
{
    //--------------------------------------------------------------------
    public static final TurnDetailBuffer
            SENTINAL = new TurnDetailBuffer();


    //--------------------------------------------------------------------
    private int   canonIndex      = -1;
    private Card  example         = null;
    private byte  represents      = 0;
    private float strength        = Float.NaN;
    private int   firstCanonRiver = 0;
    private byte  canonRiverCount = 0;


    //--------------------------------------------------------------------
    private TurnDetailBuffer()
    {
        example = Card.TWO_OF_CLUBS;
    }

    public TurnDetailBuffer(Turn turn, Odds odds)
    {
        canonIndex  = turn.canonIndex();
        example     = turn.community().turn();
        strength    = (float) odds.strengthVsRandom();
    }


    //--------------------------------------------------------------------
    public void setRiverInfo(long firstRiver, byte riverCount)
    {
        firstCanonRiver = (int) firstRiver;
        canonRiverCount = riverCount;
    }


    //--------------------------------------------------------------------
    public void incrementTurnRepresentation()
    {
        represents++;
    }


    //--------------------------------------------------------------------
    public CanonTurnDetail toDetail()
    {
        return new CanonTurnDetail(
                canonIndex,
                example,
                represents,
                strength,
                firstCanonRiver,
                canonRiverCount);
    }
}
