package ao.decision.data;

import ao.decision.attr.Attribute;
import ao.decision.attr.AttributeSet;
import ao.decision.Histogram;

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
        data = new ArrayList<Example<T>>();
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
    public void addAll(List<Example<T>> examples)
    {
        for (Example<T> example : examples)
        {
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
            hist.count( e.target() );
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
		for (Attribute<?> attribute : split.keySet())
        {
            DataSet attrData = split.get(attribute);
			remainder += (attrData.size() / size()) * attrData.entropy();
		}
		return entropy() - remainder;
    }

    public Map<Attribute, DataSet<T>> split(AttributeSet<?> on)
    {
        assert anExample().attribute( on ) != null;

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
		return entropy(normalize(data));
    }


    //--------------------------------------------------------------------
    private static double entropy(double[] probabilities)
    {
        double total = 0;
		for (double d : probabilities)
        {
            total -= log2(d) * d; // log2(d)*d is negative
		}
		return total;
    }

    private static double[] normalize(double[] data)
    {
        double total = 0;
        for (double datum : data) total += datum;
        if (total == 0) return null;

        double normal[] = new double[ data.length ];
        for (int i = 0; i < normal.length; i++)
        {
            normal[ i ] = data[i] / total;
        }
        return normal;
    }

    private static double log2(double d)
    {
        return Math.log(d) / Math.log(2);
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
