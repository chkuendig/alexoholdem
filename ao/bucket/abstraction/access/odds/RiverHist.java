package ao.bucket.abstraction.access.odds;

import ao.holdem.persist.GenericBinding;
import ao.odds.agglom.hist.RiverStrengths;
import ao.odds.agglom.hist.StrengthHist;
import ao.odds.eval.eval5.Eval5;
import ao.util.data.Arr;
import com.sleepycat.bind.tuple.TupleInput;
import com.sleepycat.bind.tuple.TupleOutput;

import java.util.Arrays;

/**
 * Date: Jan 30, 2009
 * Time: 2:21:11 PM
 *
 * Histogram of 7 card hand strengths at river.
 *
 */
public class RiverHist
{
    //--------------------------------------------------------------------
    private final int    HIST[];
    private       double mean = Double.NaN;


    //--------------------------------------------------------------------
    public RiverHist()
    {
        this( new int[RiverStrengths.COUNT] );
    }
    private RiverHist(int hist[])
    {
        HIST = hist;
    }

    public RiverHist(StrengthHist hist)
    {
        this();

        for (short i = 0, r = 0; i < Eval5.VALUE_COUNT; i++)
        {
            short riverI = RiverStrengths.lookup(i);
            if (riverI != -1)
            {
                HIST[ riverI ] = hist.get(i);
            }
        }
    }


    //--------------------------------------------------------------------
    public void count(short eval5Value)
    {
        HIST[ RiverStrengths.lookup(eval5Value) ]++;
        mean = Double.NaN;
    }


    //--------------------------------------------------------------------
    public int maxCount()
    {
        int maxCount = 0;
        for (int count : HIST)
        {
            if (maxCount < count)
            {
                maxCount = count;
            }
        }
        return maxCount;
    }

    public long totalCount()
    {
        long total = 0;
        for (int count : HIST) total += count;
        return total;
    }


    //--------------------------------------------------------------------
    public double mean()
    {
        if (Double.isNaN(mean))
        {
            mean = calculateMean();
        }
        return mean;
    }
    private double calculateMean()
    {
        long sum   = 0;
        long count = 0;

        for (int i = 0; i < HIST.length; i++)
        {
            long histCount = HIST[i];

            sum   += histCount * (i + 1);
            count += histCount;
        }

        return ((double) sum / count) /
                    (RiverStrengths.COUNT + 1);
    }


    //--------------------------------------------------------------------
    public double nonLossProb(StrengthHist that)
    {
        return -1;
    }


    //--------------------------------------------------------------------
    public int compareTo(StrengthHist o)
    {
        return Double.compare(mean(), o.mean());
    }


    //--------------------------------------------------------------------
    @Override public String toString()
    {
        return Arr.join(HIST, "\t");
    }

    @Override public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RiverHist strengthHist = (RiverHist) o;
        return Arrays.equals(HIST, strengthHist.HIST);
    }

    @Override public int hashCode()
    {
        return Arrays.hashCode(HIST);
    }


    //--------------------------------------------------------------------
    public static final int     BINDING_SIZE = RiverStrengths.COUNT * 4;
    public static final Binding BINDING      = new Binding();
    public static class Binding extends GenericBinding<RiverHist>
    {
        public RiverHist read(TupleInput input)
        {
            int hist[] = new int[ RiverStrengths.COUNT ];
            for (int i = 0; i < hist.length; i++) {
                hist[i] = input.readInt();
            }
            return new RiverHist(hist);
        }

        public void write(RiverHist o, TupleOutput to)
        {
            for (int i = 0; i < o.HIST.length; i++) {
                to.writeInt( o.HIST[i] );
            }
        }
    }


    //--------------------------------------------------------------------
    public static void main(String[] args)
    {
        StrengthHist h = new StrengthHist();
        System.out.println(h.secureHashCode());

        h.count((short) 0);
        System.out.println(h.secureHashCode());

        h.count((short) 1);
        System.out.println(h.secureHashCode());

        h.count((short) 2);
        System.out.println(h.secureHashCode());
    }
}
