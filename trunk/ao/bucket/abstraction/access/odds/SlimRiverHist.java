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
    public static void main(String[] args)
    {
        RiverHist rhA = new RiverHist();
        RiverHist rhB = new RiverHist();

        rhA.count((short) 0, (byte) 1);
        rhA.count((short) 1, (byte) 1);

        rhB.count((short) 1, (byte) 1);
        rhB.count((short) 2, (byte) 1);

        SlimRiverHist srhA = rhA.slim();
        SlimRiverHist srhB = rhB.slim();
        System.out.println("a vs b: " +
                           srhA.nonLossProb(srhB));
    }


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
        long thisSum =      totalCount();
        long thatSum = that.totalCount();
        if (thisSum == 0 || thatSum == 0) return Double.NaN;

        double winProb     = 0;
        double tieProb     = 0;
        double cumProbThat = 0;

        int thisStrength = 0;
        int thatStrength = 0;
        int thatPos      = 0;
        for (int countOrSkip : HIST)
        {
            if (countOrSkip < 0)
            {
                thisStrength -= countOrSkip;
            }
            else
            {
                while (thatStrength <= thisStrength &&
                       thatPos < that.HIST.length)
                {
                    if (that.HIST[thatPos] < 0)
                    {
                        thatStrength -= that.HIST[thatPos];
                    }
                    else
                    {
                        if ((thatStrength + 1) > thisStrength) break;

                        double thatPointProb =
                                (double) that.HIST[thatPos] / thatSum;
                        cumProbThat += thatPointProb;
                        if ((thatPos + 1) == that.HIST.length) break;
                        thatStrength++;
                    }
                    thatPos++;
                }

                double thisPointProb =
                        (double) countOrSkip / thisSum;
                if (thisStrength == thatStrength) {
                    double thatPointProb =
                            (double) that.HIST[thatPos] / thatSum;
//                    if (thatPos != that.HIST.length) {
//                        cumProbThat += thatPointProb;
//                    }
                    tieProb += thisPointProb * thatPointProb;
                }

                winProb += thisPointProb * cumProbThat;

                thisStrength++;
            }
        }

        return winProb + tieProb / 2;
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
