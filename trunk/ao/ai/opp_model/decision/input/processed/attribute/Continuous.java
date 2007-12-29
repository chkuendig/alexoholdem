package ao.ai.opp_model.decision.input.processed.attribute;

import ao.ai.opp_model.decision.classification.processed.Classification;
import ao.ai.opp_model.decision.classification.processed.Distribution;
import ao.ai.opp_model.decision.input.processed.data.LocalDatum;
import ao.ai.opp_model.decision.input.processed.data.Value;
import ao.ai.opp_model.decision.input.processed.data.ValueRange;
import ao.util.rand.Rand;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

/**
 *
 */
public class Continuous extends TypedAttribute
{
    //--------------------------------------------------------------------
    private static final int    VIEW_FOLDS         = 5;
    private static final double FOLD_LENGTH_WEIGHT = 1.0;


    //--------------------------------------------------------------------
    private SortedList<Value> uniques;
    private double            from; // including
    private double            cut;  // up to but not including
    private double            to;   // up to but not including
    private int               percision;


    //--------------------------------------------------------------------
    public Continuous(String type)
    {
        super(type);

        uniques    = new SortedList<Value>();
        from       =  0;
        cut        = -1;
        to         =  1;
        percision  = -1;
    }

    public Continuous(
            String            type,
            SortedList<Value> uniqueVals,
            double            fromPercentile,
            double            toPercentile)
    {
        this(type, uniqueVals, fromPercentile, -1, toPercentile, 0);
    }

    private Continuous(
            String            type,
            SortedList<Value> uniqueVals,
            double            fromPercentile,
            double            cutPercentile,
            double            toPercentile,
            int               numberFolds)
    {
        super(type);

        uniques   = uniqueVals;
        from      = fromPercentile;
        cut       = cutPercentile;
        to        = toPercentile;
        percision = numberFolds;
    }


    //--------------------------------------------------------------------
    public boolean isSingleUse()
    {
        return false;
    }

    public boolean isWellInformed()
    {
        return (uniques.percentileUp(to) -
                uniques.percentileDown(from)) > 1;
    }


    //--------------------------------------------------------------------
    public Collection<? extends LocalDatum> partition()
    {
        return Arrays.asList(
                new ValueRange(this, uniques, from, cut, false),
                new ValueRange(this, uniques, cut,  to,  true));
    }


    //--------------------------------------------------------------------
    public Attribute randomView()
    {
        return new Continuous(type(), uniques,
                              from,
                              Rand.nextDouble(from, to),
                              to,
                              -1);
    }


    // We assign a probability 1/2 (code length 1 bit) to the cut-point
    //  being at the median observed value.  Either quartile costs 3 bits
    //  (probability 1/8), octiles cost 5 bits (probability 1/32),
    //  and so on.  This coding scheme uses the fact that
    //      1/2 + 2/8 + 4/32 + ... = 1.
    public Collection<? extends Attribute> views()
    {
        return views(VIEW_FOLDS);
    }

    private Collection<Continuous> views(int folds)
    {
        //int size = uniques;
        Collection<Continuous> parts =
                new ArrayList<Continuous>();
        for (int fold = 0; fold < folds; fold++)
        {
            int cost   = fold*2 + 1;
            int splits = 1 << (fold + 1);
            for (int split = 1; split < splits; split += 2)
            {
                double percentile = ((double) split) / splits;
                double cutPercent = from + (to - from) * percentile;
                int    pivotDelta =
                        uniques.percentileDown(cutPercent) -
                        uniques.percentileDown(from);
                if (pivotDelta < 3) break;

                parts.add(new Continuous(
                            type(), uniques,
                            from, cutPercent, to,
                            cost));
            }
        }
        return parts;
    }


    //--------------------------------------------------------------------
    public double viewChoiceLength()
    {
        return percision * FOLD_LENGTH_WEIGHT;
    }


    //--------------------------------------------------------------------
    public Value add(double value)
    {
        Value val = new Value(this, value);
        uniques.addIfAbsent( val );
        return val;
    }


    //--------------------------------------------------------------------
    public Classification newClassification()
    {
        return new Distribution();
    }
}
