package ao.ai.opp_model.decision.data;

import ao.ai.opp_model.decision.Predictor;
import ao.ai.opp_model.decision.attr.Attribute;
import ao.ai.opp_model.decision.attr.AttributeSet;
import ao.util.stats.Info;

import java.util.*;

/**
 * 
 */
public class DataSet<T>
{
    //--------------------------------------------------------------------
    private List<Example<T>> data;


    //--------------------------------------------------------------------
    public DataSet()
    {
        data = new LinkedList<Example<T>>();
    }


    //--------------------------------------------------------------------
    public Collection<AttributeSet<?>> contextAttributes()
    {
        return anExample().attributeSets();
    }


    //--------------------------------------------------------------------
//    public Map<Attribute<?>, DataSet<T>> filter(Attribute<?> matching)
//    {
//        DataSet<T> filtered = new DataSet<T>();
//        for (Example<T> e : data)
//        {
//            if (e.attribute( matching.set() ).equals( matching ))
//            {
//                filtered
//            }
//        }
//        return filtered;
//    }


    //--------------------------------------------------------------------
    public void addAll(DataSet<T> addend)
    {
        addAll(addend.data);
    }
    public void addAll(List<Example<T>> examples)
    {
        for (Example<T> example : examples)
        {
//            if (size() > 300) data.remove(0);
            add( example );
        }
    }
    public void add(Example<T> example)
    {
        if (! data.isEmpty())
        {
            assert targetSet().typeEquals( example.target().set() );
            assert anExample().attributeSets().containsAll(
                        example.attributeSets()) &&
                    anExample().attributes().size() ==
                        example.attributes().size();
        }

        data.add( example );
    }

    public int size()
    {
        return data.size();
    }

    public boolean isEmpty()
    {
        return data.isEmpty();
    }


    //--------------------------------------------------------------------
    public Histogram<T> frequencies()
    {
        Histogram<T> hist = new Histogram<T>();
        for (Example<T> e : data)
        {
            hist.add( e.target() );
        }
        return hist;
    }


    //--------------------------------------------------------------------
    public List<Example<T>> examples()
    {
        return data;
    }


    //--------------------------------------------------------------------
    public double informationGain(AttributeSet<?> forSplittingOn)
    {
        Map<Attribute, DataSet<T>> split = split(forSplittingOn);

        double remainder = 0.0;
        for (DataSet<T> splitData : split.values())
        {
			remainder += (splitData.size() / (double)size())
                            * splitData.entropy();
		}
		return entropy() - remainder;
    }

    public Map<Attribute, DataSet<T>> split(AttributeSet<?> on)
    {
        assert anExample().attribute( on ) != null;

        on.

        // TODO: handle continuous values!!
        return splitLiterally( on );
    }
    public Map<Attribute, DataSet<T>> splitLiterally(AttributeSet<?> on)
    {
        Map<Attribute, DataSet<T>> split =
                new HashMap<Attribute, DataSet<T>>();
        for (Example<T> e : data)
        {
            DataSet<T> subset = split.get( e.attribute( on ) );
            if (subset == null)
            {
                subset = new DataSet<T>();
                split.put( e.attribute( on ), subset );
            }
            subset.add( e );
		}
		return split;
	}


    //--------------------------------------------------------------------
    public double codingLength(Predictor<T> givenTheory)
    {
        double length = 0;
        for (Example<T> e : data)
        {
            Histogram<T> frequencies = givenTheory.predict( e );
            double p = frequencies.probabilityOf( e.target() );
            length -= (p == 0)
                        ? -24
                        : Info.log2(p);
        }
        return length;
    }


    //--------------------------------------------------------------------
    public double entropy()
    {
        if (data.isEmpty()) return 0;

        Map<Object, Integer> counts = new HashMap<Object, Integer>();
        for (Example datum : data)
        {
            Integer val = counts.get( datum.target().value() );
            counts.put( datum.target().value(),
                        (val == null ? 1 : ++val) );
        }                    

		double[] data = new double[counts.keySet().size()];
		Iterator<Integer> iter = counts.values().iterator();
		for (int i = 0; i < data.length; i++) {
			data[i] = iter.next();
		}
		return Info.entropy(Info.normalize(data));
    }

    //--------------------------------------------------------------------
    private Example<T> anExample()
    {
        return (data.isEmpty())
                ? null : data.get(0);
    }
    private AttributeSet<?> targetSet()
    {
        return (data.isEmpty())
                ? null
                : anExample().target().set();
    }
}
