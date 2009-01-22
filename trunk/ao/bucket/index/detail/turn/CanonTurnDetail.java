package ao.bucket.index.detail.turn;

import ao.bucket.index.detail.CanonDetail;
import ao.holdem.model.card.Card;
import ao.util.math.Calc;

/**
 * Date: Jan 9, 2009
 * Time: 12:38:16 PM
 */
public class CanonTurnDetail implements CanonDetail
{
    //--------------------------------------------------------------------
    private final int   CANON_INDEX;
    private final Card  EXAMPLE;
    private final byte  REPRESENTS;
    private final float STRENGTH;
    private final int   FIRST_CANON_RIVER;
    private final byte  CANON_RIVER_COUNT;


    //--------------------------------------------------------------------
    public CanonTurnDetail(
            int   canonIndex,
            Card  example,
            byte  represents,
            float strength,
            int   firstCanonRiver,
            byte  canonRiverCount)
    {
        CANON_INDEX       = canonIndex;
        EXAMPLE           = example;
        REPRESENTS        = represents;
        STRENGTH          = strength;
        FIRST_CANON_RIVER = firstCanonRiver;
        CANON_RIVER_COUNT = canonRiverCount;
    }


    //--------------------------------------------------------------------
    public long canonIndex()
    {
        return CANON_INDEX;
    }


    //--------------------------------------------------------------------
    public Card example()
    {
        return EXAMPLE;
    }


    //--------------------------------------------------------------------
    public byte represents()
    {
        return REPRESENTS;
    }


    //--------------------------------------------------------------------
    public double strengthVsRandom()
    {
        return STRENGTH;
    }


    //--------------------------------------------------------------------
    public long firstCanonRiver()
    {
        return Calc.unsigned( FIRST_CANON_RIVER );
    }

    public byte canonRiverCount()
    {
        return CANON_RIVER_COUNT;
    }


//    //--------------------------------------------------------------------
//    public static final Binding BINDING = new Binding();
//    public static class Binding
//    {
//        public CanonTurnDetail read(int canonIndex, TupleInput in) {
//            return new CanonTurnDetail(
//                    canonIndex,
//                    Card.BINDING.entryToObject( in ),
//                    in.readByte(),
//                    in.readFloat(),
//                    in.readInt(),
//                    in.readByte());
//        }
//
//        public void write(CanonTurnDetail obj, TupleOutput out) {
//            Card.BINDING.objectToEntry( obj.EXAMPLE, out );
//            out.writeByte ( obj.REPRESENTS       );
//            out.writeFloat( obj.STRENGTH         );
//            out.writeInt  ( obj.FIRST_CANON_RIVER);
//            out.writeByte ( obj.CANON_RIVER_COUNT);
//        }
//    }


    //--------------------------------------------------------------------
    @Override public String toString()
    {
        return "CanonTurnDetail{" +
               "CANON_INDEX=" + CANON_INDEX +
               ", EXAMPLE=" + EXAMPLE +
               ", REPRESENTS=" + REPRESENTS +
               ", STRENGTH=" + STRENGTH +
               ", FIRST_CANON_RIVER=" + firstCanonRiver() +
               ", CANON_RIVER_COUNT=" + CANON_RIVER_COUNT +
               '}';
    }

    @Override public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CanonTurnDetail that = (CanonTurnDetail) o;
        return CANON_INDEX == that.CANON_INDEX &&
               CANON_RIVER_COUNT == that.CANON_RIVER_COUNT &&
               FIRST_CANON_RIVER == that.FIRST_CANON_RIVER &&
               REPRESENTS == that.REPRESENTS &&
               Float.compare(that.STRENGTH, STRENGTH) == 0 &&
               EXAMPLE == that.EXAMPLE;
    }

    @Override public int hashCode()
    {
        int result = CANON_INDEX;
        result = 31 * result + EXAMPLE.hashCode();
        result = 31 * result + (int) REPRESENTS;
        result = 31 * result + (STRENGTH != +0.0f
                                ? Float.floatToIntBits(STRENGTH) : 0);
        result = 31 * result + FIRST_CANON_RIVER;
        result = 31 * result + (int) CANON_RIVER_COUNT;
        return result;
    }
}
