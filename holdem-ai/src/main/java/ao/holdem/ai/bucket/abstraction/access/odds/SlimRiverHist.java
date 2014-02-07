package ao.holdem.ai.bucket.abstraction.access.odds;

import ao.holdem.persist.GenericBinding;
import ao.holdem.ai.ai.odds.agglom.hist.CompactRiverStrengths;
import ao.util.data.primitive.IntList;
import ao.util.math.rand.Rand;
import com.sleepycat.bind.tuple.TupleInput;
import com.sleepycat.bind.tuple.TupleOutput;
import org.apache.log4j.Logger;

/**
 * Date: Jan 30, 2009
 * Time: 4:00:19 PM
 */
public class SlimRiverHist
{
    //--------------------------------------------------------------------
    private static final Logger LOG =
            Logger.getLogger(SlimRiverHist.class);


    public static void main(String[] args)
    {
        for (int i = 0; i < 100000; i++) {
            RiverHist rhA = randomHist();
            RiverHist rhB = randomHist();

            SlimRiverHist srhA = rhA.slim();
            SlimRiverHist srhB = rhB.slim();

            double     prob =  rhA.nonLossProb(  rhB );
            double slimProb = srhA.nonLossProb( srhB );
            if (prob != slimProb) {
                LOG.debug(
                        prob + "\t" + slimProb + "\t" +
                        Math.abs(prob - slimProb));
            }
        }
    }

    private static RiverHist randomHist()
    {
        RiverHist hist = new RiverHist();
        for (int i = 1 + Rand.nextInt(1000); i > 0; i--) {
            hist.count((short)
                    Rand.nextInt( CompactRiverStrengths.COUNT ));
        }
        return hist;
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
            HIST = slim.toIntArray();
        }
    }

    private void slim(IntList slim, int[] riverHist)
    {
        int skipped = 0;
        for (short i = 0; i < CompactRiverStrengths.COUNT; i++)
        {
            int count = riverHist[ i ];
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
                    (CompactRiverStrengths.COUNT + 1);
    }


    //--------------------------------------------------------------------
    public double nonLossProb(SlimRiverHist that)
    {
        long thisSum =      totalCount();
        long thatSum = that.totalCount();
        if (thisSum == 0 || thatSum == 0) return Double.NaN;

        double winProb     = 0;
        double tieProb     = 0;
        double thatCumProb = 0;

        int thatIndex    = 0;
        int thisStrength = 0;
        int thatStrength = 0;
        for (int skipOrCount : HIST) {
            if (skipOrCount < 0) {
                thisStrength -= skipOrCount;
                continue;
            }

            double thisPointProb = (double) skipOrCount / thisSum;
            double thatPointProb = 0;
            for (; thatIndex < that.HIST.length; thatIndex++) {
                int thatSkipOrCount = that.HIST[ thatIndex ];
                if (thatSkipOrCount < 0) {
                    thatStrength -= thatSkipOrCount;
                    continue;
                }

                thatPointProb =
                        (double) that.HIST[thatIndex] / thatSum;
                if (thisStrength <= thatStrength) break;

                thatCumProb += thatPointProb;
                if (thatIndex != (that.HIST.length - 1)) {
                    thatStrength++;
                }
            }

            winProb += thisPointProb * thatCumProb;
            if (thisStrength == thatStrength) {
                tieProb += thisPointProb * thatPointProb;
            }
            
            thisStrength++;
        }

        return winProb + tieProb / 2;
    }


    //--------------------------------------------------------------------
    public int bindingSize()
    {
        return 4 * (1 + HIST.length);
    }

    public static final int     BINDING_MAX_SIZE =
                                           (1 + CompactRiverStrengths.COUNT) * 4;
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
