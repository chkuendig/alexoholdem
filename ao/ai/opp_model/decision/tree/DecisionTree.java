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
public class DecisionTree<T> implements Predictor<T>
{
    //--------------------------------------------------------------------
    private static final double ALPHA = 0.3;//0.3


    //--------------------------------------------------------------------
    private DecisionTree<T>                    parent;
    private Map<Attribute<?>, DecisionTree<T>> nodes;
    private AttributeSet<?>                    attrSet;
    private Histogram<T>                       hist;
    private DataSet<T>                         data;


    //--------------------------------------------------------------------
    public DecisionTree(DataSet<T> ds)
    {
        nodes = new TreeMap<Attribute<?>, DecisionTree<T>>();
        hist  = ds.frequencies();
        data  = ds;
    }


    //--------------------------------------------------------------------
    public void freeze()
    {
        data = null;
        if (! isLeaf())
        {
            for (DecisionTree<T> child : kids())
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
            DecisionTree<T> subTree =
                    new DecisionTree<T>( splitPlane.getValue() );
            addNode(splitPlane.getKey(), subTree);
		}
    }

    public void unsplit()
    {
        attrSet = null;
        nodes.clear();
    }


    //--------------------------------------------------------------------
    public DecisionTree<T> root()
    {
        return isRoot()
               ? this : parent.root();
    }

    protected void setParent(DecisionTree<T> parent)
    {
        this.parent = parent;
    }

    public Collection<DecisionTree<T>> kids()
    {
        return nodes.values();
    }

    public Collection<DecisionTree<T>> leafs()
    {
        List<DecisionTree<T>> leafs = new ArrayList<DecisionTree<T>>();

        if (isLeaf())
        {
            leafs.add( this );
        }
        else
        {
            for (DecisionTree<T> child : kids())
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

        DecisionTree<T> cursor = this;
        while (cursor != null)
        {
            contexts.remove( cursor.attrSet );
            cursor = cursor.parent;
        }

        return contexts;
    }


    //--------------------------------------------------------------------
    private void addNode(Attribute<?>    attribute,
                         DecisionTree<T> tree)
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

    private double attributeAndChildLength(int numAttributes)
    {
        //if there are n attributes, the length of the code for the
        //  attribute labelling the root is logn, but the codes for
        //  attributes labelling deeper nodes will be shorter. At any
        //  node, only those discrete attributes that have not appeared
        //  in the path from the root to the node are eligible to label
        //  the node.
        double length = Info.log2( numAttributes );

        for (DecisionTree<T> child : kids())
        {
            length += child.codingComplexity(numAttributes - 1);
        }
        return length;
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


    private double typeLength(int numAttributes)
    {
        //The probability that the root is a leaf remains to be
        //  determined. Since in practice we hope to get a useful
        //  tree, this probability should be low. We have taken it as
        //  the reciprocal of the number of attributes, on the grounds
        //  that the more attributes there are, the better is the
        //  chance of finding one that is useful.
        // Use for each p = the reciprocal of the arity of the parent
        //  of the node. Thus, in a uniform b-ary tree, each node other
        //  than the root is regarded as having probability 1/b of not
        //  being a leaf.
        return isInternalLength(
                1.0 / (isRoot()
                       ? (double) numAttributes
                       : (double) parent.kids().size()));
    }

    //we expect 1s to occur with some fixed probability p independent
    //  of preceding digits. Coding techniques exist in effect allowing
    //  each 1 to be coded with (—logp) bits, and each 0 with
    //  (-log(l - p)) bits, and such a code is optimal if indeed the
    //  probability of Is is p.
    private double isInternalLength(double p)
    {
        return -(isInternal()
                 ? Info.log2(      p)
                 : Info.log2(1.0 - p));
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

        DecisionTree<T> subTree = nodes.get( attribute );
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

            DecisionTree child = nodes.get(attribute);
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
