package ao.bucket.index.detail.preflop;

import ao.bucket.index.detail.CanonDetail;
import ao.bucket.index.hole.HoleLookup;
import ao.holdem.model.card.Card;
import ao.holdem.model.card.Community;
import ao.holdem.model.card.Hole;
import ao.holdem.persist.GenericBinding;
import ao.odds.agglom.Odds;
import ao.odds.agglom.impl.PreciseHeadsUpOdds;
import com.sleepycat.bind.tuple.TupleInput;
import com.sleepycat.bind.tuple.TupleOutput;

/**
 * Date: Jan 9, 2009
 * Time: 12:21:17 PM
 */
public class CanonHoleDetail implements CanonDetail
{
    //--------------------------------------------------------------------
    private final Card HOLE_A;
    private final Card HOLE_B;
    private final byte REPRESENTS;
    private final Odds HEADS_UP_ODDS;
    private final int  FIRST_CANON_FLOP;
    private final char CANON_FLOP_COUNT;


    //--------------------------------------------------------------------
    public CanonHoleDetail(
            Hole example,
            byte represents,
            Odds headsUpOdds,
            int  canonFlopsFrom,
            char numCanonFlops)
    {
        HOLE_A           = example.a();
        HOLE_B           = example.b();
        REPRESENTS       = represents;
        HEADS_UP_ODDS    = headsUpOdds;
        FIRST_CANON_FLOP = canonFlopsFrom;
        CANON_FLOP_COUNT = numCanonFlops;
    }


    //--------------------------------------------------------------------
    public Hole example()
    {
        return Hole.valueOf(HOLE_A, HOLE_B);
    }

    public long canonIndex()
    {
        return HoleLookup.lookup(HOLE_A, HOLE_B).canonIndex();
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
    public int firstCanonFlop()
    {
        return FIRST_CANON_FLOP;
    }
    
    public char canonFlopCount()
    {
        return CANON_FLOP_COUNT;
    }


    //--------------------------------------------------------------------
    public static final Binding BINDING = new Binding();
    public static class Binding extends GenericBinding<CanonHoleDetail> {
        public CanonHoleDetail read(TupleInput in) {
            return new CanonHoleDetail(
                    Hole.valueOf(
                            Card.BINDING.entryToObject( in ),
                            Card.BINDING.entryToObject( in )),
                    in.readByte(),
                    Odds.BINDING.read(in),
                    in.readInt(),
                    in.readChar());
        }

        public void write(CanonHoleDetail obj, TupleOutput out) {
            Card.BINDING.objectToEntry( obj.HOLE_A, out );
            Card.BINDING.objectToEntry( obj.HOLE_B, out );
            out.writeByte( obj.REPRESENTS );
            Odds.BINDING.write( obj.HEADS_UP_ODDS, out );
            out.writeInt( obj.FIRST_CANON_FLOP);
            out.writeChar( obj.CANON_FLOP_COUNT);
        }
    }


    //--------------------------------------------------------------------
    @Override public String toString()
    {
        return "CanonHoleDetail{" +
               "HOLE_A=" + HOLE_A +
               ", HOLE_B=" + HOLE_B +
               ", REPRESENTS=" + REPRESENTS +
               ", HEADS_UP_ODDS=" + HEADS_UP_ODDS +
               ", FIRST_CANON_FLOP=" + FIRST_CANON_FLOP +
               ", CANONICAL_COUNT=" + (int) CANON_FLOP_COUNT +
               '}';
    }


    //--------------------------------------------------------------------
    @Override public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CanonHoleDetail that = (CanonHoleDetail) o;

        return FIRST_CANON_FLOP == that.FIRST_CANON_FLOP &&
               CANON_FLOP_COUNT == that.CANON_FLOP_COUNT &&
               REPRESENTS == that.REPRESENTS &&
               HEADS_UP_ODDS.equals(that.HEADS_UP_ODDS) &&
               HOLE_A == that.HOLE_A &&
               HOLE_B == that.HOLE_B;
    }

    @Override public int hashCode()
    {
        int result = HOLE_A.hashCode();
        result = 31 * result + HOLE_B.hashCode();
        result = 31 * result + (int) REPRESENTS;
        result = 31 * result + HEADS_UP_ODDS.hashCode();
        result = 31 * result + FIRST_CANON_FLOP;
        result = 31 * result + (int) CANON_FLOP_COUNT;
        return result;
    }


    //--------------------------------------------------------------------
    public static class Buffer
    {
        public Hole HOLE              = null;
        public byte REPRESENTS        = 0;
        public Odds HEADS_UP_ODDS     = null;
        public int  FIRST_CANON_FLOP = -1;
        public char CANON_FLOP_COUNT = 0;

        public Buffer(Hole hole)
        {
            HOLE          = hole;
            HEADS_UP_ODDS =
                    new PreciseHeadsUpOdds().compute(
                            hole, Community.PREFLOP);
        }

        public CanonHoleDetail toDetail()
        {
            return new CanonHoleDetail(
                    HOLE, REPRESENTS, HEADS_UP_ODDS,
                    FIRST_CANON_FLOP, CANON_FLOP_COUNT);
        }
    }
}
