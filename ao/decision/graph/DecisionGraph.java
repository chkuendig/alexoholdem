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
    private DecisionGraph<T>                    joinNode;
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
        DataSet<T> joinedData = new DataSet<T>();
        for (DecisionGraph<T> toJoin : graphs)
        {
            joinedData.addAll(toJoin.data);
        }

        DecisionGraph<T> joined = new DecisionGraph<T>(joinedData);
        for (DecisionGraph<T> toJoin : graphs)
        {
            toJoin.joinTo( joined );
        }
    }
    public static <T> void unjoin(DecisionGraph<T>[] graphs)
    {
        for (DecisionGraph<T> toDisjoin : graphs)
        {
            toDisjoin.joinNode = null;
        }
    }


    //--------------------------------------------------------------------
    public DecisionGraph<T> root()
    {
        return isRoot()
               ? this : aParent().root();
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
        assert joinNode == null;

        nodes.put(attribute, tree);
        tree.parents.add( this );
    }
    private void joinTo(DecisionGraph<T> tree)
    {
        assert joinNode == null;
        assert nodes.isEmpty();

        joinNode = tree;
        tree.parents.add( this );
    }


    //--------------------------------------------------------------------
    public double messageLength()
    {
        double length = 0;
        Queue<DecisionGraph<T>> openRoots =
                new LinkedList<DecisionGraph<T>>();
        List<DecisionGraph<T>> oldJoins =
                new ArrayList<DecisionGraph<T>>();
        List<DecisionGraph<T>> newJoins =
                new ArrayList<DecisionGraph<T>>();

        openRoots.add( new DecisionGraph<T>(data) );
        while (! openRoots.isEmpty())
        {
            DecisionGraph<T> root = openRoots.poll();
            newJoins.addAll( subTree(root, root.nodes) );
            length += root.treeMessageLength();

            if (newJoins.isEmpty() && oldJoins.isEmpty()) break;

            double n = newJoins.size();
            double q = oldJoins.size();
            length += Info.log2(Math.min(n, (n+q)/2.0));// transmit M

            List<Collection<DecisionGraph<T>>> comboPatterns =
                    extractComboPatterns(oldJoins, newJoins);
            List<Collection<DecisionGraph<T>>> oldCombos =
                    new ArrayList<Collection<DecisionGraph<T>>>();
            List<Collection<DecisionGraph<T>>> newCombos =
                    new ArrayList<Collection<DecisionGraph<T>>>();

            for (Collection<DecisionGraph<T>> combo : comboPatterns)
            {
                openRoots.add( combo.iterator().next().joinNode );

                Collection<DecisionGraph<T>> oldCombo =
                    new ArrayList<DecisionGraph<T>>();
                oldCombos.add( oldCombo );

                Collection<DecisionGraph<T>> newCombo =
                        new ArrayList<DecisionGraph<T>>();
                newCombos.add( newCombo );

                for (DecisionGraph<T> aJoin : combo)
                {
                    if (oldJoins.contains( aJoin ))
                    {
                        oldJoins.remove( aJoin );
                        oldCombo.add( aJoin );
                    }
                    else
                    {
                        newJoins.remove( aJoin );
                        newCombo.add( aJoin );
                    }
                }
            }
            length += comboPatternLength(oldCombos, newCombos,
                                         oldJoins,  newJoins);
        }

        oldJoins.addAll( newJoins );
        newJoins.clear();
        return length;
    }

    private double comboPatternLength(
            List<Collection<DecisionGraph<T>>> oldCombos,
            List<Collection<DecisionGraph<T>>> newCombos,
            List<DecisionGraph<T>>             remainOldJoins,
            List<DecisionGraph<T>>             remainNewJoins)
    {        
        return 0;
    }

    private List<Collection<DecisionGraph<T>>>
                extractComboPatterns(List<DecisionGraph<T>> oldJoins,
                                     List<DecisionGraph<T>> newJoins)
    {
        return null;
    }

    /**
     * Takes a graph, and extracts a tree from it treating join
     *  nodes as leaves.
     *
     * @param root
     * @param branches
     * @return list of join nodes (as leaves)
     */
    private List<DecisionGraph<T>>
                subTree(DecisionGraph<T>                    root,
                        Map<Attribute<?>, DecisionGraph<T>> branches)
    {
        List<DecisionGraph<T>> openJoins =
                new ArrayList<DecisionGraph<T>>();

        for (Map.Entry<Attribute<?>, DecisionGraph<T>> branch :
                branches.entrySet())
        {
            DecisionGraph<T> subRoot =
                    new DecisionGraph<T>(branch.getValue().data);
            if (branch.getValue().isJoin())
            {
                openJoins.add( branch.getValue() );

                // to indicate that its a join and not a leaf
                subRoot.parents.add( subRoot );
            }

            root.addNode(branch.getKey(), subRoot);
            openJoins.addAll(
                    subTree(subRoot, branch.getValue().nodes));
        }

        return openJoins;
    }


    //--------------------------------------------------------------------
    public double treeMessageLength()
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
                         : categoryLength(0.5));
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
    private boolean isJoin()
    {
        return parents.size() > 1;
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
