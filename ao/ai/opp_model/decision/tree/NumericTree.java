package ao.ai.opp_model.decision.tree;

import ao.ai.opp_model.decision.Predictor;
import ao.ai.opp_model.decision.attr.Attribute;
import ao.ai.opp_model.decision.attr.AttributeSet;
import ao.ai.opp_model.decision.data.Context;
import ao.ai.opp_model.decision.data.DataSet;
import ao.ai.opp_model.decision.data.Histogram;
import ao.util.stats.Info;
import ao.util.text.Txt;

import java.util.*;

/**
 * see "Coding Decision Trees" WALLACE & PATRICK (1993)
 */
public class NumericTree<T> implements Predictor<T>
{
    //--------------------------------------------------------------------
    private static final double ALPHA = 0.3;//0.3


    //--------------------------------------------------------------------
    private NumericTree<T>                    parent;
    private Map<Attribute<?>, NumericTree<T>> nodes;
    private AttributeSet<?>                   attrSet;
    private Histogram<T>                      hist;
    private DataSet<T>                        data;

    private Collection<AttributeSet<?>>       availableAttributs;


    //--------------------------------------------------------------------
    public NumericTree(DataSet<T> ds)
    {
        this(ds, ds.contextAttributes());
    }
    private NumericTree(DataSet<T>                  ds,
                        Collection<AttributeSet<?>> availAttributs)
    {
        nodes = new TreeMap<Attribute<?>, NumericTree<T>>();
        hist  = ds.frequencies();
        data  = ds;

        availableAttributs = availAttributs;
    }



    //--------------------------------------------------------------------
    public void freeze()
    {
        data = null;
        if (! isLeaf())
        {
            for (NumericTree<T> child : kids())
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
            NumericTree<T> subTree =
                    new NumericTree<T>( splitPlane.getValue() );
            addNode(splitPlane.getKey(), subTree);
		}
    }

    public void unsplit()
    {
        attrSet = null;
        nodes.clear();
    }


    //--------------------------------------------------------------------
    public NumericTree<T> root()
    {
        return isRoot()
               ? this : parent.root();
    }

    protected void setParent(NumericTree<T> parent)
    {
        this.parent = parent;
    }

    public Collection<NumericTree<T>> kids()
    {
        return nodes.values();
    }

    public Collection<NumericTree<T>> leafs()
    {
        List<NumericTree<T>> leafs = new ArrayList<NumericTree<T>>();

        if (isLeaf())
        {
            leafs.add( this );
        }
        else
        {
            for (NumericTree<T> child : kids())
            {
                leafs.addAll( child.leafs() );
            }
        }

        return leafs;
    }

    public Collection<AttributeSet<?>> availableContexts()
    {
        Set<AttributeSet<?>> contexts =
                new HashSet<AttributeSet<?>>(
                        data.contextAttributes());

        NumericTree<T> cursor = this;
        while (cursor != null)
        {
            contexts.remove( cursor.attrSet );
            cursor = cursor.parent;
        }

        return contexts;
    }


    //--------------------------------------------------------------------
    private void addNode(Attribute<?>   attribute,
                         NumericTree<T> tree)
    {
        assert !nodes.containsKey( attribute );
        nodes.put(attribute, tree);
        tree.setParent( this );
    }
//    public void removeNode(Attribute<?> attribute)
//    {
//        assert nodes.containsKey( attribute );
//        nodes.remove(attribute);
//    }



    //--------------------------------------------------------------------
    public double messageLength()
    {
        assert data != null : "cannot count length of frozen tree";
//        return codingComplexity(
//                        data.contextAttributes().size()) +
//                data.codingLength( root() );
        return codingComplexity(
                        data.contextAttributes().size());
    }

    public double codingComplexity(int numAttributes)
    {
        double length = typeLength(numAttributes);
        return length + (isInternal()
                         ? attributeAndChildLength(numAttributes)
                         : categoryLength(ALPHA));
    }

    private double attributeAndChildLength(int availAttributes)
    {
        int nextAvailAttrs =
                nextAvailableAttributes(availAttributes);

        double childComplexity = 0;
        for (NumericTree<T> child : kids())
        {
            childComplexity +=
                    child.codingComplexity( nextAvailAttrs );
        }
        
        return branchChoiceLength(availAttributes) +
                Info.log2(availAttributes) +
                childComplexity;
    }

    // A discrete attribute cannot be tested by more than
    //  one branch node in any path,  If a branch tests a
    //  discrete attribute, A is decremented for each of
    //  the child trees.
    private int nextAvailableAttributes(int availAttributes)
    {
        return attrSet.isDescrete()
                ? availAttributes - 1
                : availAttributes;
    }

    // For each 'branch' codeward, we need to sate which of the
    //  input attributes will be tested.  This requires
    //  lg(A) bits, where A is the number of attributes able to
    //  be tested at that branch.
    private double branchChoiceLength(int availAttributes)
    {
        return Info.log2( availAttributes );
    }



    //If there are M classes,
    //and in the first j things of a category, i[m] have had class m,
    // the class of the (j + l)th thing is encoded assigning a probability
    //  q[m] = (i[m] + alpha)/(j + M * alpha)
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
//    private double categoryLength(double alpha)
//    {
//        int numClasses = hist.numClasses();
//
//        double length = Info.log2( numClasses );
//        for (Attribute<T> clazz : hist.attributes())
//        {
//            length += Info.log2( hist.countOf(clazz) );
//        }
//        return length;
//    }


    // For each node we firstly need to state whether
    //  it is a branch of a leaf.
    private double typeLength(int numAttributes)
    {
        if (isRoot())
        {
            // At the root of the tree:
            //   * a branch costs lg(n+1) - lg(n) bits
            //	 * a leaf   costs lg(n+1)         bits
            // where n is the number of input attributes.

            double log2nPlusOne = Info.log2(numAttributes + 1);
            return isInternal()
                    ? log2nPlusOne - Info.log2(numAttributes)
                    : log2nPlusOne;
        }
        else
        {
            // For any non-root node, the codeward:
            //	 * a branch requires lg(s) bits
            //	 * a leaf requires -lg((s - 1)/s) bits
            // where s represents the number of childred of
            //  the node's parent (always greater than 1).

            int parentKids = parent.kids().size();
            return isInternal()
                    ? Info.log2(parentKids)
                    : Info.log2((parentKids - 1) /
                                        parentKids);
        }
    }


    //--------------------------------------------------------------------
    private boolean isRoot()
    {
        return parent == null;
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

        NumericTree<T> subTree = nodes.get( attribute );
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
		if (attrSet == null) return;

        buf.append(Txt.nTimes("\t", depth));
        buf.append("***");
        buf.append(attrSet.type()).append("\n");

        for (Attribute<?> attribute : nodes.keySet())
        {
            buf.append(Txt.nTimes("\t", depth + 1));
            buf.append("+").append(attribute);

            NumericTree child = nodes.get(attribute);
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