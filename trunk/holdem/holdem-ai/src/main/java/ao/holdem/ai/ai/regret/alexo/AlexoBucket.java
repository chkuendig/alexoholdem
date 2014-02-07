package ao.holdem.ai.ai.regret.alexo;

import ao.holdem.bucket.Bucket;
import ao.holdem.engine.agglom.Odds;
import ao.holdem.ai.ai.simple.alexo.card.AlexoCard;
import ao.holdem.ai.ai.simple.alexo.card.AlexoCardSequence;
import ao.holdem.ai.ai.simple.alexo.card.AlexoHand;

import java.util.List;

/**
 *
 */
public class AlexoBucket implements Bucket<AlexoBucket>
{
    //--------------------------------------------------------------------
    //private final int               VALUE;
    private final AlexoCard         HOLE;
    private final AlexoCard         FLOP;
    private final AlexoCard         TURN;

    private final List<AlexoBucket> KIDS;


    //--------------------------------------------------------------------
    public AlexoBucket(AlexoCardSequence cards,
                       boolean           forFirstToAct)
    {
        this(cards.hole(forFirstToAct),
             cards.community().flop(),
             cards.community().turn(),
             null);
    }
    public AlexoBucket(List<AlexoBucket> kids)
    {
        this(null, null, null, kids);
    }
    public AlexoBucket(AlexoCard hole,
                       List<AlexoBucket> kids)
    {
        this(hole, null, null, kids);
    }
    public AlexoBucket(AlexoCard hole,
                       AlexoCard flop,
                       List<AlexoBucket> kids)
    {
        this(hole, flop, null, kids);
    }
    public AlexoBucket(AlexoCard hole,
                       AlexoCard flop,
                       AlexoCard turn)
    {
        this(hole, flop, turn, null);
    }

    private AlexoBucket(AlexoCard         hole,
                        AlexoCard         flop,
                        AlexoCard         turn,
                        List<AlexoBucket> kids)
    {
        HOLE = hole;
        FLOP = flop;
        TURN = turn;
        KIDS = kids;
    }


    //--------------------------------------------------------------------
    public boolean holeEquals(AlexoCard hole)
    {
        return HOLE == hole;
    }

    public boolean flopEquals(AlexoCard flop)
    {
        return FLOP == flop;
    }

    public boolean turnEquals(AlexoCard turn)
    {
        return TURN == turn;
    }


    //--------------------------------------------------------------------
    public AlexoBucket[] nextBuckets()
    {
        return KIDS.toArray(new AlexoBucket[ KIDS.size() ]);
    }


    //--------------------------------------------------------------------
    public AlexoCardSequence sequence(AlexoBucket withLastToAct)
    {
        return new AlexoCardSequence(HOLE, withLastToAct.HOLE,
                                     FLOP, TURN);
    }


    //--------------------------------------------------------------------
    public double against(AlexoBucket otherTerminal)
    {
        int thisValue = AlexoHand.valueOf(HOLE, FLOP, TURN);
        int thatValue = AlexoHand.valueOf(otherTerminal.HOLE,
                                          otherTerminal.FLOP,
                                          otherTerminal.TURN);
        return thisValue < thatValue
                ? new Odds(0, 1, 0).strengthVsRandom()
                : new Odds(1, 0, 0).strengthVsRandom();
    }

    
    //--------------------------------------------------------------------
    public String toString()
    {
        return String.valueOf(HOLE) + " | " +
               String.valueOf(FLOP) + ", "  +
               String.valueOf(TURN);
    }


    //--------------------------------------------------------------------
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AlexoBucket that = (AlexoBucket) o;
        return HOLE == that.HOLE &&
               FLOP == that.FLOP &&
               TURN == that.TURN
//               !(KIDS != null
//                 ? !KIDS.equals(that.KIDS)
//                 : that.KIDS != null) &&
               ;
    }

    public int hashCode()
    {
        int result;
        result = (HOLE != null ? HOLE.hashCode() : 0);
        result = 31 * result + (FLOP != null ? FLOP.hashCode() : 0);
        result = 31 * result + (TURN != null ? TURN.hashCode() : 0);
//        result = 31 * result + (KIDS != null ? KIDS.hashCode() : 0);
        return result;
    }
}
