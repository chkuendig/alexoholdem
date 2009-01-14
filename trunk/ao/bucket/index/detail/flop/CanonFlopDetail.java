package ao.bucket.index.detail.flop;

import ao.bucket.index.detail.CanonDetail;
import ao.holdem.model.card.Card;
import ao.odds.agglom.Odds;
import com.sleepycat.bind.tuple.TupleInput;
import com.sleepycat.bind.tuple.TupleOutput;

/**
 * Date: Jan 9, 2009
 * Time: 12:33:06 PM
 */
public class CanonFlopDetail implements CanonDetail
{
    //--------------------------------------------------------------------
    public static final CanonFlopDetail SENTINAL =
            CanonFlopDetailBuffer.SENTINAL.toDetail();


    //--------------------------------------------------------------------
    private final int  CANON_INDEX;
    private final Card FLOP_A;
    private final Card FLOP_B;
    private final Card FLOP_C;
    private final byte REPRESENTS;
    private final Odds HEADS_UP_ODDS;
    private final int  FIRST_CANON_TURN;
    private final byte CANON_TURN_COUNT;


    //--------------------------------------------------------------------
    public CanonFlopDetail(
            int  canonIndex,
            Card flopA,
            Card flopB,
            Card flopC,
            byte represents,
            Odds headsUpOdds,
            int  firstCanonTurn,
            byte canonTurnCount)
    {
        CANON_INDEX      = canonIndex;
        FLOP_A           = flopA;
        FLOP_B           = flopB;
        FLOP_C           = flopC;
        REPRESENTS       = represents;
        HEADS_UP_ODDS    = headsUpOdds;
        FIRST_CANON_TURN = firstCanonTurn;
        CANON_TURN_COUNT = canonTurnCount;
    }


    //--------------------------------------------------------------------
    public Card exampleA()
    {
        return FLOP_A;
    }

    public Card exampleB()
    {
        return FLOP_B;
    }

    public Card exampleC()
    {
        return FLOP_C;
    }


    //--------------------------------------------------------------------
    public long canonIndex()
    {
        return CANON_INDEX;
    }


    //--------------------------------------------------------------------
    public byte represents()
    {
        return REPRESENTS;
    }


    //--------------------------------------------------------------------
    public Odds headsUpOdds()
    {
        return HEADS_UP_ODDS;
    }

    public double strengthVsRandom()
    {
        return HEADS_UP_ODDS.strengthVsRandom();
    }


    //--------------------------------------------------------------------
    public int firstCanonTurn()
    {
        return FIRST_CANON_TURN;
    }

    public byte canonTurnCount()
    {
        return CANON_TURN_COUNT;
    }


    //--------------------------------------------------------------------
    public CanonFlopDetail resetCounters()
    {
        return new CanonFlopDetail(
                CANON_INDEX,
                FLOP_A, FLOP_B, FLOP_C,
                (byte) 0, HEADS_UP_ODDS, -1, (byte) 0);
    }


    //--------------------------------------------------------------------
    public static final Binding BINDING = new Binding();

    public static class Binding
    {
        public CanonFlopDetail read(int canonIndex, TupleInput in) {
            return new CanonFlopDetail(
                    canonIndex,
                    Card.BINDING.entryToObject( in ),
                    Card.BINDING.entryToObject( in ),
                    Card.BINDING.entryToObject( in ),
                    in.readByte(),
                    Odds.BINDING.read(in),
                    in.readInt(),
                    in.readByte());
        }

        public void write(CanonFlopDetail obj, TupleOutput out) {
            Card.BINDING.objectToEntry( obj.FLOP_A, out );
            Card.BINDING.objectToEntry( obj.FLOP_B, out );
            Card.BINDING.objectToEntry( obj.FLOP_C, out );
            out.writeByte( obj.REPRESENTS );
            Odds.BINDING.write( obj.HEADS_UP_ODDS, out );
            out.writeInt( obj.FIRST_CANON_TURN);
            out.writeByte( obj.CANON_TURN_COUNT);
        }
    }


    //--------------------------------------------------------------------
    @Override public String toString()
    {
        return "CanonFlopDetail{" +
               "FLOP_A=" + FLOP_A +
               ", FLOP_B=" + FLOP_B +
               ", FLOP_C=" + FLOP_C +
               ", REPRESENTS=" + REPRESENTS +
               ", HEADS_UP_ODDS=" + HEADS_UP_ODDS +
               ", FIRST_CANON_TURN=" + FIRST_CANON_TURN +
               ", CANON_TURN_COUNT=" + CANON_TURN_COUNT +
               '}';
    }


    //--------------------------------------------------------------------
    @Override public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CanonFlopDetail that = (CanonFlopDetail) o;

        return CANON_TURN_COUNT == that.CANON_TURN_COUNT &&
               FIRST_CANON_TURN == that.FIRST_CANON_TURN &&
               REPRESENTS == that.REPRESENTS &&
               FLOP_A == that.FLOP_A &&
               FLOP_B == that.FLOP_B &&
               FLOP_C == that.FLOP_C &&
               HEADS_UP_ODDS.equals(that.HEADS_UP_ODDS);
    }

    @Override public int hashCode()
    {
        int result = FLOP_A.hashCode();
        result = 31 * result + FLOP_B.hashCode();
        result = 31 * result + FLOP_C.hashCode();
        result = 31 * result + (int) REPRESENTS;
        result = 31 * result + HEADS_UP_ODDS.hashCode();
        result = 31 * result + FIRST_CANON_TURN;
        result = 31 * result + (int) CANON_TURN_COUNT;
        return result;
    }
}
