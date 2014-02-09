package ao.holdem.abs.odds.agglom;

import ao.holdem.persist.GenericBinding;
import com.sleepycat.bind.tuple.TupleInput;
import com.sleepycat.bind.tuple.TupleOutput;

/**
 * odds from the point of view of one player.
 */
public class Odds
{
    //--------------------------------------------------------------------
    private static final Odds PURE_WIN   = new Odds(1, 0, 0);
    private static final Odds PURE_LOSS  = new Odds(0, 1, 0);
    private static final Odds PURE_SPLIT = new Odds(0, 0, 1);

    public static Odds valueOf(short thisValue, short thatValue)
    {
        return   thisValue > thatValue
               ? PURE_WIN
               : thisValue < thatValue
               ? PURE_LOSS
               : PURE_SPLIT;
    }


    //--------------------------------------------------------------------
    private final long WIN;
    private final long LOSE;
    private final long SPLIT;


    //--------------------------------------------------------------------
    public Odds()
    {
        this(0, 0, 0);
    }

    public Odds(long winOdds, long loseOdds, long splitOdds)
    {
        WIN   = winOdds;
        LOSE  = loseOdds;
        SPLIT = splitOdds;
    }

    
    //--------------------------------------------------------------------
    public Odds plus(Odds addend)
    {
//        return (nonLossPercent() < between.nonLossPercent())
//                ? this
//                : between;
            return new Odds(WIN   + addend.WIN,
                            LOSE  + addend.LOSE,
                            SPLIT + addend.SPLIT);
    }


    //--------------------------------------------------------------------
    public long winOdds()
    {
        return WIN;
    }
//    public double winPercent()
//    {
//        return ((double) WIN) / (WIN + LOSE + SPLIT);
//    }


    //--------------------------------------------------------------------
    public long loseOdds()
    {
        return LOSE;
    }
//    public double losePercent()
//    {
//        return ((double) LOSE) / (WIN + LOSE + SPLIT);
//    }


    //--------------------------------------------------------------------
    public long splitOdds()
    {
        return SPLIT;
    }
//    public double splitPercent()
//    {
//        return ((double) SPLIT) / (WIN + LOSE + SPLIT);
//    }


    //--------------------------------------------------------------------
    public double strengthVsRandom()
    {
        // it is extremely rare that more than 2 ppl split.
        return strengthVsRandom(2);
    }
    private double strengthVsRandom(int numberOfPlayers)
    {
        return (WIN + (double)SPLIT/numberOfPlayers)
               / (WIN + LOSE + SPLIT);
    }


    //--------------------------------------------------------------------
    public static final Binding BINDING = new Binding();

    public static class Binding extends GenericBinding<Odds> {
        public Odds read(TupleInput tupleInput) {
            return new Odds(tupleInput.readLong(),
                            tupleInput.readLong(),
                            tupleInput.readLong());
        }

        public void write(Odds o, TupleOutput tupleOutput) {
            tupleOutput.writeLong( o.WIN   );
            tupleOutput.writeLong( o.LOSE  );
            tupleOutput.writeLong( o.SPLIT );
        }
    }


    //--------------------------------------------------------------------
    @Override public String toString()
    {
//        return "[win: "    + WIN   +
//                " (" + Math.round(nonLossPercent()  * 100) + ")" +
//               ", lose: "  + LOSE  +
//                " (" + Math.round(losePercent() * 100) + ")" +
//               ", split: " + SPLIT +
//               " (" + Math.round(splitPercent() * 100) + ")" + "]";
//        return "new Odds(" + WIN + ", " + LOSE + ", " + SPLIT + ")";
        return WIN + "\t" + LOSE + "\t" + SPLIT;
    }


    //--------------------------------------------------------------------
    @Override public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Odds odds = (Odds) o;
        return WIN   == odds.WIN  &&
               LOSE  == odds.LOSE &&
               SPLIT == odds.SPLIT;

    }

    @Override public int hashCode()
    {
        int result = (int) (WIN ^ (WIN >>> 32));
        result = 31 * result + (int) (LOSE ^ (LOSE >>> 32));
        result = 31 * result + (int) (SPLIT ^ (SPLIT >>> 32));
        return result;
    }
}
