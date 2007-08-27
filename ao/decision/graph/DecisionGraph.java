package ao.decision.graph;

import ao.decision.Predictor;
import ao.decision.attr.Attribute;
import ao.decision.attr.AttributeSet;
import ao.decision.data.Context;
import ao.decision.data.DataSet;
import ao.decision.data.Histogram;
import ao.util.stats.Info;
import ao.util.text.Txt;

import java.util.*;

/**
 * 
 */
public class DecisionGraph<T> implements Predictor<T>
{
    //--------------------------------------------------------------------
    private List<DecisionGraph<T>>              parents;
    private Map<Attribute<?>, DecisionGraph<T>> nodes;
    private AttributeSet<?>                     attrSet;
    private Histogram<T>                        hist;
    private DataSet<T>                          data;


    //--------------------------------------------------------------------
    public DecisionGraph(DataSet<T> ds)
    {
        nodes   = new TreeMap<Attribute<?>, DecisionGraph<T>>();
        parents = new ArrayList<DecisionGraph<T>>();
        hist    = ds.frequencies();
        data    = ds;
    }


    //--------------------------------------------------------------------
    public void freeze()
    {
        data = null;
        if (! isLeaf())
        {
            for (DecisionGraph<T> child : kids())
            {
                child.freeze();
            }
        }
    }

    public DataSet<T> trainingData()
    {
        return data;
    }


    //--------------------------------------------------------------------
    public void split(AttributeSet<?> on)
    {
        assert data != null : "cannot split frozen tree";
        unsplit();

        attrSet = on;
        for (Map.Entry<Attribute, DataSet<T>> splitPlane :
                data.split( on ).entrySet())
        {
            DecisionGraph<T> subTree =
                    new DecisionGraph<T>( splitPlane.getValue() );
            addNode(splitPlane.getKey(), subTree);
		}
    }

    public void unsplit()
    {
        attrSet = null;
        nodes.clear();
    }


    //--------------------------------------------------------------------
    public static <T> void join(DecisionGraph<T>[] graphs)
    {

    }
    public static <T> void unjoin(DecisionGraph<T>[] graphs)
    {

    }
//    public void join(Collection<DecisionGraph<T>> with)
//    {
//
//    }
//
//    public void unjoin()
//    {
//
//    }


    //--------------------------------------------------------------------
    public DecisionGraph<T> root()
    {
        return isRoot()
               ? this : aParent().root();
    }

    protected void addParent(DecisionGraph<T> parent)
    {
        parents.add(parent);
    }

    private DecisionGraph<T> aParent()
    {
        return parents.get(0);
    }

    public Collection<DecisionGraph<T>> kids()
    {
        return nodes.values();
    }

    public List<DecisionGraph<T>> leafs()
    {
        List<DecisionGraph<T>> leafs =
                new ArrayList<DecisionGraph<T>>();

        if (isLeaf())
        {
            leafs.add( this );
        }
        else
        {
            for (DecisionGraph<T> child : kids())
            {
                leafs.addAll( child.leafs() );
            }
        }

        return leafs;
    }

    public Collection<AttributeSet<?>> unsplitContexts()
    {
        Set<AttributeSet<?>> contexts =
                new HashSet<AttributeSet<?>>(
                        data.contextAttributes());

        DecisionGraph<T> cursor = this;
        while (cursor != null)
        {
            contexts.remove( cursor.attrSet );
            cursor = cursor.aParent();
        }

        return contexts;
    }


    //--------------------------------------------------------------------
    private void addNode(Attribute<?>     attribute,
                         DecisionGraph<T> tree)
    {
        assert !nodes.containsKey( attribute );
        nodes.put(attribute, tree);
        tree.addParent( this );
    }



    //--------------------------------------------------------------------
    public double messageLength()
    {
        assert data != null : "cannot count length of frozen tree";
        return codingComplexity(
                        data.contextAttributes().size()) +
                data.codingLength( root() );
    }

    public double codingComplexity(int numAttributes)
    {
        double length = typeLength(numAttributes);
        return length + (isInternal()
                         ? attributeAndChildLength(numAttributes)
                         : categoryLength(0.3));
    }

    private double attributeAndChildLength(int numAttributes)
    {
        double length = Info.log2( numAttributes );

        for (DecisionGraph<T> child : kids())
        {
            length += child.codingComplexity(numAttributes - 1);
        }
        return length;
    }

    private double categoryLength(double alpha)
    {
        int numClasses = hist.numClasses();

        int    j      = 0;
        double length = 0;
        for (Attribute<T> clazz : hist.attributes())
        {
            int classCount = hist.countOf(clazz);
            for (int i = 0; i < classCount; i++)
            {
                double p = (i + alpha)/(j + numClasses*alpha);
                length  -= Info.log2(p);
                j++;
            }
        }
        return length;
    }

    private double typeLength(int numAttributes)
    {
        return isInternalLength(
                1.0 / (isRoot()
                       ? (double) numAttributes
                       : (double) aParent().kids().size()));
    }

    private double isInternalLength(double p)
    {
        return -(isInternal()
                 ? Info.log2(      p)
                 : Info.log2(1.0 - p));
    }


    //--------------------------------------------------------------------
    private boolean isRoot()
    {
        return parents.isEmpty();
    }

    private boolean isInternal()
    {
        return !isLeaf();
    }

    private boolean isLeaf()
    {
        return kids().isEmpty();
    }


    //--------------------------------------------------------------------
    public Histogram<T> predict(Context basedOn)
    {
        Attribute<?> attribute = basedOn.attribute(attrSet);
        if (attribute == null) return hist;

        DecisionGraph<T> subTree = nodes.get( attribute );
        if (subTree == null)   return hist;

        return subTree.predict( basedOn );
    }


    //--------------------------------------------------------------------
    public String toString()
    {
        StringBuilder b = new StringBuilder();
        appendTree(1, b);
        return b.toString();
    }

    private void appendTree(int depth, StringBuilder buf)
    {
		if (attrSet != null)
        {
			buf.append(Txt.nTimes("\t", depth));
			buf.append("***");
            buf.append(attrSet.type()).append("\n");

            for (Attribute<?> attribute : nodes.keySet())
            {
				buf.append(Txt.nTimes("\t", depth + 1));
                buf.append("+").append(attribute);

				DecisionGraph child = nodes.get(attribute);
                if (child.isLeaf())
                {
                    buf.append(" ");
                    buf.append( child.hist );
                    buf.append("\n");
                }
                else
                {
                    buf.append("\n");
                    child.appendTree(depth + 1, buf);
                }
			}
		}
	}
}
