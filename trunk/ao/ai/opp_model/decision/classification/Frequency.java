package ao.ai.opp_model.decision.classification;

import ao.ai.opp_model.decision.attribute.Attribute;
import ao.ai.opp_model.decision.attribute.Multistate;
import ao.ai.opp_model.decision.data.Datum;
import ao.ai.opp_model.decision.data.State;
import ao.util.stats.Info;

/**
 *
 */
public class Frequency extends Classification
{
    //--------------------------------------------------------------------
    private Histogram<Datum> hist;


    //--------------------------------------------------------------------
    public Frequency()
    {
        hist = new Histogram<Datum>();
    }


    //--------------------------------------------------------------------
    public void add(Datum datum)
    {
        hist.add( datum );
    }


    //--------------------------------------------------------------------
    public double probabilityOf(Datum datum)
    {
        return hist.probabilityOf( datum );
    }

    public int countOfState(Object value)
    {
        for (Datum item : hist.classes())
        {
            if (((State) item).state().equals( value ))
            {
                return hist.countOf( item );
            }
        }
        return 0;
    }

    public void put(Datum datum, int value)
    {
        hist.put(datum, value);
    }


    //--------------------------------------------------------------------
    public Datum mostProbable()
    {
        return hist.mostFrequent();
    }

    public Datum mostProbable(ConfusionMatrix<Datum> confusion)
    {
        return confusion.adjust( hist );
    }


    //--------------------------------------------------------------------
    //If there are M classes,
    //and in the first j things of a category, i[m] have had class m,
    // the class of the (j + l)th thing is encoded assigning a probability
    //  q[m] = (i[m] + alpha)/(j + M * alpha)
    public double transmissionCost(double alpha)
    {
        int numClasses = hist.classCount();

        int    j      = 0;
        double length = 0;
        for (Datum clazz : hist.classes())
        {
            int classCount = hist.countOf( clazz );
            for (int i = 0; i < classCount; i++)
            {
                double p = (i + alpha)/(j + numClasses*alpha);
                length  -= Info.log2(p);
                j++;
            }
        }
        return length;
    }


    //--------------------------------------------------------------------
    @Override
    public String toString()
    {
        if (hist.isEmpty()) return "Empty";

        State     aState = (State) hist.classes().iterator().next();
        Attribute attr   = aState.attribute();

        StringBuilder b = new StringBuilder();
        for (Datum datum : ((Multistate) attr).orderedPartition())
        {
            b.append(datum)
             .append("\t")
             .append( hist.countOf(datum) )
             .append("\t");
        }
        return b.deleteCharAt( b.length()-1 ).toString();

//        return hist.toString() + "\t" +
//               Info.cost(probabilityOf(mostProbable()));
    }
}
