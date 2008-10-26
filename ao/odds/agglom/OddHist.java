package ao.odds.agglom;

import ao.holdem.persist.GenericBinding;
import ao.odds.eval.eval5.Eval5;
import com.sleepycat.bind.tuple.TupleInput;
import com.sleepycat.bind.tuple.TupleOutput;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

/**
 * Date: Sep 29, 2008
 * Time: 5:26:34 PM
 *
 * Histogram of possible strengths of hands.
 */
public class OddHist implements Comparable<OddHist>
{
    //--------------------------------------------------------------------
    private final int    HIST[];
    private       double mean = Double.NaN;


    //--------------------------------------------------------------------
    public OddHist()
    {
        this( new int[Eval5.VALUE_COUNT] );
    }
    private OddHist(int hist[])
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


    //--------------------------------------------------------------------
    public long secureHashCode()
    {
        try
        {
            return computeSecureHashCode();
        }
        catch (NoSuchAlgorithmException e)
        {
            throw new Error( e );
        }
    }
    private long computeSecureHashCode()
            throws NoSuchAlgorithmException
    {
        MessageDigest m = MessageDigest.getInstance("MD5");

        for (int count : HIST)
        {
            m.update((byte) (count >>> 24));
            m.update((byte) (count >>> 16));
            m.update((byte) (count >>>  8));
            m.update((byte)  count        );
        }

        return new BigInteger(1, m.digest()).longValue();
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
        double total = 0;
        int    count = 0;

        for (int i = 0; i < HIST.length; i++)
        {
            int hist = HIST[i];

            total += hist * i;
            count += hist;
        }

        return total / count;
    }


    //--------------------------------------------------------------------
    public int compareTo(OddHist o)
    {
        return Double.compare(mean(), o.mean());
    }


    //--------------------------------------------------------------------
    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        OddHist oddHist = (OddHist) o;
        return Arrays.equals(HIST, oddHist.HIST);
    }

    @Override
    public int hashCode()
    {
        return Arrays.hashCode(HIST);
    }


    //--------------------------------------------------------------------
    public static final Binding BINDING = new Binding();
    public static class Binding extends GenericBinding<OddHist>
    {
        public OddHist read(TupleInput input)
        {
            int hist[] = new int[Eval5.VALUE_COUNT];

            for (int i = 0; i < hist.length; i++)
            {
                hist[i] = input.readInt();
            }

            return new OddHist(hist);
        }

        public void write(OddHist o, TupleOutput to)
        {
            for (int i = 0; i < o.HIST.length; i++)
            {
                to.writeInt( o.HIST[i] );
            }
        }
    }


    //--------------------------------------------------------------------
    public static void main(String[] args)
    {
        OddHist h = new OddHist();
        System.out.println(h.secureHashCode());

        h.count((short) 0);
        System.out.println(h.secureHashCode());

        h.count((short) 1);
        System.out.println(h.secureHashCode());

        h.count((short) 2);
        System.out.println(h.secureHashCode());
    }
}
