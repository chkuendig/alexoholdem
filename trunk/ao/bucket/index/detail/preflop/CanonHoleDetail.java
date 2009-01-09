package ao.bucket.index.detail.preflop;

import ao.holdem.model.card.Card;
import ao.holdem.model.card.Hole;
import ao.holdem.persist.GenericBinding;
import ao.odds.agglom.Odds;
import com.sleepycat.bind.tuple.TupleInput;
import com.sleepycat.bind.tuple.TupleOutput;

/**
 * Date: Jan 9, 2009
 * Time: 12:21:17 PM
 */
public class CanonHoleDetail
{
    //--------------------------------------------------------------------
    private final Card HOLE_A;
    private final Card HOLE_B;
    private final byte REPRESENTS;
    private final Odds HEADS_UP_ODDS;
    private final int  CANON_FLOPS_FROM;
    private final char NUM_CANON_FLOPS;


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
        CANON_FLOPS_FROM = canonFlopsFrom;
        NUM_CANON_FLOPS  = numCanonFlops;
    }


    //--------------------------------------------------------------------
    public Hole example()
    {
        return Hole.valueOf(HOLE_A, HOLE_B);
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


    //--------------------------------------------------------------------
    public int canonFlopsFrom()
    {
        return CANON_FLOPS_FROM;
    }
    
    public char numCanonFlops()
    {
        return NUM_CANON_FLOPS;
    }


    //--------------------------------------------------------------------
//    public static final int     SIZE;
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
            out.writeInt( obj.CANON_FLOPS_FROM );
            out.writeChar( obj.NUM_CANON_FLOPS );
        }
    }

//    static {
//        CanonHoleDetail canonHoleDetail =
//                new CanonHoleDetail(
//                    Hole.valueOf(Card.ACE_OF_CLUBS,
//                                 Card.ACE_OF_DIAMONDS),
//                    (byte) 0, new Odds(), 0, (byte) 0);
//
//        TupleOutput out = new TupleOutput();
//        BINDING.write(canonHoleDetail, out);
//        SIZE = out.getBufferLength();
//    }


    //--------------------------------------------------------------------
    @Override public String toString()
    {
        return "CanonHoleDetail{" +
               "HOLE_A=" + HOLE_A +
               ", HOLE_B=" + HOLE_B +
               ", REPRESENTS=" + REPRESENTS +
               ", HEADS_UP_ODDS=" + HEADS_UP_ODDS +
               ", CANON_FLOPS_FROM=" + CANON_FLOPS_FROM +
               ", NUM_CANON_FLOPS=" + (int)NUM_CANON_FLOPS +
               '}';
    }


    //--------------------------------------------------------------------
    @Override public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CanonHoleDetail that = (CanonHoleDetail) o;

        return CANON_FLOPS_FROM == that.CANON_FLOPS_FROM &&
               NUM_CANON_FLOPS == that.NUM_CANON_FLOPS &&
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
        result = 31 * result + CANON_FLOPS_FROM;
        result = 31 * result + (int) NUM_CANON_FLOPS;
        return result;
    }


    //--------------------------------------------------------------------
    public static class Buffer
    {
        public Hole HOLE              = null;
        public byte REPRESENTS        = 0;
        public Odds HEADS_UP_ODDS     = null;
        public int  CANON_FLOPS_FROM  = -1;
        public char NUM_CANON_FLOPS   = 0;

        public CanonHoleDetail toDetail()
        {
            return new CanonHoleDetail(
                    HOLE, REPRESENTS, HEADS_UP_ODDS,
                    CANON_FLOPS_FROM, NUM_CANON_FLOPS); 
        }
    }
}
