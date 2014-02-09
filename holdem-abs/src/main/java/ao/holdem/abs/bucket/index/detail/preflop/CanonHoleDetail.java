package ao.holdem.abs.bucket.index.detail.preflop;

import ao.holdem.canon.hole.CanonHole;
import ao.holdem.abs.bucket.index.detail.CanonDetail;
import ao.holdem.abs.bucket.index.detail.example.ExampleLookup;
import ao.holdem.abs.bucket.index.detail.range.CanonRange;
import ao.holdem.model.card.Hole;
import ao.holdem.persist.GenericBinding;
import com.sleepycat.bind.tuple.TupleInput;
import com.sleepycat.bind.tuple.TupleOutput;

/**
 * Date: Jan 9, 2009
 * Time: 12:21:17 PM
 */
public class CanonHoleDetail implements CanonDetail
{
    //--------------------------------------------------------------------
//    private final Card HOLE_A;
//    private final Card HOLE_B;
    private final char CANON_INDEX;
    private final byte REPRESENTS;
//    private final Odds HEADS_UP_ODDS;
    private final int  FIRST_CANON_FLOP;
    private final char CANON_FLOP_COUNT;


    //--------------------------------------------------------------------
    public CanonHoleDetail(
            char canonIndex,
            byte represents,
//            Odds headsUpOdds,
            int  canonFlopsFrom,
            char numCanonFlops)
    {
        CANON_INDEX      = canonIndex;
        REPRESENTS       = represents;
//        HEADS_UP_ODDS    = headsUpOdds;
        FIRST_CANON_FLOP = canonFlopsFrom;
        CANON_FLOP_COUNT = numCanonFlops;
    }


    //--------------------------------------------------------------------
    public Hole example()
    {
        return ExampleLookup.hole( CANON_INDEX );
    }

    public CanonHole canon()
    {
        return CanonHole.create(example());
    }

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
//    public Odds headsUpOdds()
//    {
//        return HoleOdds;
//    }

    public double strength()
    {
        return HoleOdds.lookup(CANON_INDEX).strengthVsRandom();
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

    public CanonRange flops()
    {
        return CanonRange.newFromCount(
                firstCanonFlop(),
                canonFlopCount());
    }


    //--------------------------------------------------------------------
    public static final Binding BINDING = new Binding();

    public static class Binding extends GenericBinding<CanonHoleDetail> {
        public CanonHoleDetail read(TupleInput in) {
            return new CanonHoleDetail(
                    in.readChar(),
                    in.readByte(),
//                    Odds.BINDING.read(in),
                    in.readInt(),
                    in.readChar());
        }

        public void write(CanonHoleDetail obj, TupleOutput out) {
//            Card.BINDING.objectToEntry( obj.HOLE_A, out );
//            Card.BINDING.objectToEntry( obj.HOLE_B, out );
            out.writeChar( obj.CANON_INDEX );
            out.writeByte( obj.REPRESENTS );
//            Odds.BINDING.write( obj.HEADS_UP_ODDS, out );
            out.writeInt( obj.FIRST_CANON_FLOP);
            out.writeChar( obj.CANON_FLOP_COUNT);
        }
    }


    //--------------------------------------------------------------------
    @Override public String toString()
    {
        return "CanonHoleDetail{" +
               "INDEX=" + (int)CANON_INDEX +
               ", REPRESENTS=" + REPRESENTS +
//               ", HEADS_UP_ODDS=" + HEADS_UP_ODDS +
               ", FIRST_CANON_FLOP=" + FIRST_CANON_FLOP +
               ", CANONS=" + (int) CANON_FLOP_COUNT +
               '}';
    }


    //--------------------------------------------------------------------
    @Override public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CanonHoleDetail that = (CanonHoleDetail) o;
        return CANON_INDEX == that.CANON_INDEX;
    }

    @Override public int hashCode()
    {
        return CANON_INDEX;
    }


    //--------------------------------------------------------------------
    public static class Buffer
    {
        public Hole HOLE              = null;
        public byte REPRESENTS        = 0;
//        public Odds HEADS_UP_ODDS     = null;
        public int  FIRST_CANON_FLOP = -1;
        public char CANON_FLOP_COUNT = 0;

        public Buffer(Hole hole)
        {
            HOLE          = hole;
//            HEADS_UP_ODDS =
//                    new PreciseHeadsUpOdds().compute(
//                            hole, Community.PREFLOP);
        }

        public CanonHoleDetail toDetail()
        {
            return new CanonHoleDetail(
                    CanonHole.create(HOLE).canonIndex(),
                    REPRESENTS,
//                    HEADS_UP_ODDS,
                    FIRST_CANON_FLOP, CANON_FLOP_COUNT);
        }
    }
}
