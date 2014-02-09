package ao.holdem.abs.odds.agglom.hist;

import ao.holdem.persist.GenericBinding;
import ao.holdem.engine.state.eval.Eval5;
import ao.util.data.Arrs;
import ao.util.math.crypt.MD5;
import ao.util.math.crypt.SecureHash;
import com.sleepycat.bind.tuple.TupleInput;
import com.sleepycat.bind.tuple.TupleOutput;
import org.apache.log4j.Logger;

import java.util.Arrays;

/**
 * Date: Sep 29, 2008
 * Time: 5:26:34 PM
 *
 * Histogram of possible strengths of hands.
 */
public class StrengthHist implements Comparable<StrengthHist>
{
    //--------------------------------------------------------------------
    private static final Logger LOG =
        Logger.getLogger(StrengthHist.class);


    //--------------------------------------------------------------------
    private final int    HIST[];
    private       double mean = Double.NaN;


    //--------------------------------------------------------------------
    public StrengthHist()
    {
        this( new int[Eval5.VALUE_COUNT] );
    }
    private StrengthHist(int hist[])
    {
        HIST = hist;
    }


    //--------------------------------------------------------------------
    public void count(short value)
    {
        HIST[ value ]++;
        mean = Double.NaN;
    }


    //--------------------------------------------------------------------
    public int get(short value)
    {
        return HIST[ value ];
    }

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
    public long secureHashCode()
    {
        SecureHash hash = new MD5();
        for (int count : HIST) {
            hash.feed(count);
        }
        return hash.bigDigest().longValue();
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

        return (double) sum / count;
    }


    //--------------------------------------------------------------------
    public int compareTo(StrengthHist o)
    {
        return Double.compare(mean(), o.mean());
    }


    //--------------------------------------------------------------------
    @Override public String toString()
    {
        return Arrs.join(HIST, "\t");
    }

    @Override public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        StrengthHist strengthHist = (StrengthHist) o;
        return Arrays.equals(HIST, strengthHist.HIST);
    }

    @Override public int hashCode()
    {
        return Arrays.hashCode(HIST);
    }


    //--------------------------------------------------------------------
    public static final int     BINDING_SIZE = Eval5.VALUE_COUNT * 4;
    public static final Binding BINDING      = new Binding();
    public static class Binding extends GenericBinding<StrengthHist>
    {
        public StrengthHist read(TupleInput input)
        {
            int hist[] = new int[ Eval5.VALUE_COUNT ];
            for (int i = 0; i < hist.length; i++) {
                hist[i] = input.readInt();
            }
            return new StrengthHist(hist);
        }

        public void write(StrengthHist o, TupleOutput to)
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
        LOG.info(h.secureHashCode());

        h.count((short) 0);
        LOG.info(h.secureHashCode());

        h.count((short) 1);
        LOG.info(h.secureHashCode());

        h.count((short) 2);
        LOG.info(h.secureHashCode());
    }
}
