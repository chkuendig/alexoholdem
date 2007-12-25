package ao.ai.opp_model.decision.input.processed.attribute;

import ao.ai.opp_model.decision.input.processed.data.LocalDatum;
import ao.ai.opp_model.decision.input.processed.data.Value;
import ao.ai.opp_model.decision.input.processed.data.ValueRange;
import ao.ai.opp_model.decision.classification.processed.Classification;
import ao.ai.opp_model.decision.classification.processed.Distribution;
import ao.util.rand.Rand;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 *
 */
public class Continuous extends TypedAttribute
{
    //--------------------------------------------------------------------
    private static final int    VIEW_FOLDS         = 5;
    private static final double FOLD_LENGTH_WEIGHT = 1.0;


    //--------------------------------------------------------------------
    private List<Value> values;

    private Value sorted[];
    private int   from; // including
    private int   cut;  // up to but not including
    private int   to;   // up to but not including
    private int   percision;


    //--------------------------------------------------------------------
    public Continuous(String type)
    {
        super(type);

        values     = new ArrayList<Value>();
        from       = -1;
        cut        = -1;
        to         = -1;
        percision  = -1;
    }

    public Continuous(
            String type,
            Value  inOrder[],
            int    fromIndex,
            int    toIndex)
    {
        this(type, inOrder, fromIndex, -1, toIndex, 0);
    }

    private Continuous(
            String type,
            Value  inOrder[],
            int    fromIndex,
            int    cutIndex,
            int    toIndex,
            int    numberFolds)
    {
        super(type);

        values    = null;
        sorted    = inOrder;
        from      = fromIndex;
        cut       = cutIndex;
        to        = toIndex;
        percision = numberFolds;
    }


    //--------------------------------------------------------------------
    public boolean isSingleUse()
    {
        return false;
    }


    //--------------------------------------------------------------------
    public Collection<? extends LocalDatum> partition()
    {
        return Arrays.asList(
                new ValueRange(this, sorted, from, cut),
                new ValueRange(this, sorted, cut,  to));
    }


    //--------------------------------------------------------------------
    public Attribute randomView()
    {
        sortAttributes();
        double splitAt = Rand.nextDouble();
        return new Continuous(type(), sorted,
                              from,
                              from + (int)(Math.ceil(
                                            splitAt * (to - from - 1))),
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

    public Collection<Continuous> views(int folds)
    {
        sortAttributes();

        int size = valueCount();
        Collection<Continuous> parts =
                new ArrayList<Continuous>();
        for (int fold = 0; fold < folds; fold++)
        {
            int cost   = fold*2 + 1;
            int splits = 1 << (fold + 1);
            for (int split = 1; split < splits; split += 2)
            {
                double percentile = ((double) split) / splits;
                int    pivotDelta = (int)(size * percentile);
                if (pivotDelta < 3) break;

                parts.add(new Continuous(
                            type(), sorted,
                            from, from + pivotDelta, to,
                            cost));
            }
        }
        return parts;
    }

    private void sortAttributes()
    {
        if (sorted != null) return;

        sorted = values.toArray( new Value[values.size()] );
        Arrays.sort(sorted);
        values = null;

        // corrupt values for stable comparison
        for (int i = 0; i < sorted.length - 1; i++)
        {
            if (sorted[i].equals( sorted[i + 1] ))
            {
                sorted[i + 1] = sorted[i + 1].corruptUpwards();
            }
            else if (sorted[i].compareTo( sorted[i + 1] ) > 0)
            {
                sorted[i + 1] = sorted[i].corruptUpwards();
            }
        }

        // corrupt edges
        sorted[0                ] = sorted[0                ].toLeast();
        sorted[sorted.length - 1] = sorted[sorted.length - 1].toMost();

        from      = 0;
        to        = sorted.length;
        percision = 0;
    }


    //--------------------------------------------------------------------
    public double viewChoiceLength()
    {
        return percision * FOLD_LENGTH_WEIGHT;
    }

    private int valueCount()
    {
        return (values != null)
                ? values.size()
                : to - from;
    }


    //--------------------------------------------------------------------
    public Value add(double value)
    {
        assert values != null : "views already derived";

        Value val = new Value(this, value);
        values.add( val );
        return val;
    }


    //--------------------------------------------------------------------
    public Classification newClassification()
    {
        return new Distribution();
    }
}
