package ao.bucket.abstraction.access.odds;

import ao.holdem.persist.GenericBinding;
import ao.odds.agglom.hist.RiverStrengths;
import ao.util.data.primitive.IntList;
import com.sleepycat.bind.tuple.TupleInput;
import com.sleepycat.bind.tuple.TupleOutput;

/**
 * Date: Jan 30, 2009
 * Time: 4:00:19 PM
 */
public class SlimRiverHist
{
    //--------------------------------------------------------------------
    private final int[] HIST;


    //--------------------------------------------------------------------
    public SlimRiverHist(int[] riverHist)
    {
        this(false, riverHist);
    }
    private SlimRiverHist(boolean isSlim,
                          int[]   riverHistOrSlim)
    {
        if (isSlim) {
            HIST = riverHistOrSlim;
        } else {
            IntList slim = new IntList();
            slim(slim, riverHistOrSlim);
            HIST = slim.toArray();
        }
    }

    private void slim(IntList slim, int[] riverHist)
    {
        int skipped = 0;
        for (short i = 0; i < RiverStrengths.COUNT; i++)
        {
            int count = riverHist [ i ];
            if (count == 0)
            {
                skipped++;
            }
            else
            {
                if (skipped > 0)
                {
                    slim.add(-skipped);
                    skipped = 0;
                }
                slim.add(count);
            }
        }
    }


    //--------------------------------------------------------------------
    public int length()
    {
        return HIST.length;
    }

    public long totalCount()
    {
        long total = 0;
        for (int h : HIST) {
            if (h > 0) total += h;
        }
        return total;
    }


    //--------------------------------------------------------------------
    public double mean()
    {
        long sum      = 0;
        long count    = 0;
        int  strength = 1;

        for (int histCount : HIST)
        {
            if (histCount > 0)
            {
                sum   += (long) histCount * strength;
                count += histCount;
                strength++;
            }
            else
            {
                strength -= histCount;
            }
        }

        return ((double) sum / count) /
                    (RiverStrengths.COUNT + 1);
    }


    //--------------------------------------------------------------------
    public double nonLossProb(SlimRiverHist that)
    {
        return -1;
    }


    //--------------------------------------------------------------------
    public int bindingSize()
    {
        return 4 * (1 + HIST.length);
    }

    public static final int     BINDING_MAX_SIZE =
                                            RiverStrengths.COUNT * 4 + 4;
    public static final Binding BINDING          = new Binding();
    public static class Binding extends GenericBinding<SlimRiverHist>
    {
        public SlimRiverHist read(TupleInput input)
        {
            int length = input.readInt();
            int hist[] = new int[ length ];
            for (int i = 0; i < hist.length; i++) {
                hist[i] = input.readInt();
            }
            return new SlimRiverHist(true, hist);
        }

        public void write(SlimRiverHist o, TupleOutput to)
        {
            to.writeInt( o.HIST.length );
            for (int i = 0; i < o.HIST.length; i++) {
                to.writeInt( o.HIST[i] );
            }
        }
    }
}
