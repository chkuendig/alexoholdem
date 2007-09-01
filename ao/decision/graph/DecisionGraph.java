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
 * see Tan & Dowe (2003)
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
    private DecisionGraph()
    {
        nodes   = new TreeMap<Attribute<?>, DecisionGraph<T>>();
        parents = new ArrayList<DecisionGraph<T>>();
    }
    public DecisionGraph(DataSet<T> ds)
    {
        this();
        hist = ds.frequencies();
        data = ds;
    }
    private DecisionGraph(DecisionGraph<T> copyDataFrom)
    {
        this();
        hist = copyDataFrom.hist;
        data = copyDataFrom.data;
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

    private List<DecisionGraph<T>> joins()
    {
        List<DecisionGraph<T>> joins =
                new ArrayList<DecisionGraph<T>>();

        if (isJoin())
        {
            joins.add( this );
        }
        else
        {
            for (DecisionGraph<T> child : kids())
            {
                joins.addAll( child.joins() );
            }
        }

        return joins;
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
            DecisionGraph<T> root = subTree(openRoots.poll());
            newJoins.addAll( root.joins() );
            length += root.treeMessageLength();

            if (newJoins.isEmpty() && oldJoins.isEmpty()) break;

            int n = newJoins.size();
            int q = oldJoins.size();
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
            length += comboPatternLength(n, q,
                                         oldCombos, newCombos,
                                         oldJoins,  newJoins);
        }

        oldJoins.addAll( newJoins );
        newJoins.clear();
        return length;
    }

    private double comboPatternLength(
            int n, int q,
            List<Collection<DecisionGraph<T>>> oldCombos,
            List<Collection<DecisionGraph<T>>> newCombos,
            List<DecisionGraph<T>>             remainOldJoins,
            List<DecisionGraph<T>>             remainNewJoins)
    {
        // # of childred of joins, already transmitted.
        int m = oldCombos.size();

        // pending nodes not involved in any join
        int p = remainOldJoins.size() + remainNewJoins.size();

        // # of nodes in each group of joining leafs
        int j[] = new int[ m ];
        for (int i = 0; i < m; i++)
        {
            j[ i ] = oldCombos.get(i).size() +
                     newCombos.get(i).size();
        }

        // 
        int y;
        

        return 0;
    }

    private List<Collection<DecisionGraph<T>>>
                extractComboPatterns(List<DecisionGraph<T>> oldJoins,
                                     List<DecisionGraph<T>> newJoins)
    {
        List<DecisionGraph<T>> joins = new ArrayList<DecisionGraph<T>>();
        joins.addAll( oldJoins );
        joins.addAll( newJoins );

        List<Collection<DecisionGraph<T>>> combos =
                new ArrayList<Collection<DecisionGraph<T>>>();
        while (! joins.isEmpty())
        {
            DecisionGraph<T> aJoin = joins.get( joins.size()-1 );
            Collection<DecisionGraph<T>> combo =
                    new ArrayList<DecisionGraph<T>>();

            DecisionGraph<T> joinDest = aJoin.joinNode;
            for (DecisionGraph<T> partner : joins)
            {
                if (partner.joinNode.equals( joinDest ))
                {
                    combo.add( partner );
                }
            }

            if (combo.size() == joinDest.parents.size())
            {
                combos.add( combo );
            }
            joins.removeAll( combo );
        }
        return combos;
    }


    /**
     * Takes a graph, and extracts a tree from it treating join
     *  nodes as leaves.  The internal nodes are rebuilt,
     *  however the leafs and joins are left intact, still pointing
     *  to their former parents.
     *
     * @param copyRoot graph to extract tree from.
     * @return the extracted tree.
     */
    private DecisionGraph<T> subTree(DecisionGraph<T> copyRoot)
    {
        DecisionGraph<T> root = new DecisionGraph<T>( copyRoot );

        for (Map.Entry<Attribute<?>, DecisionGraph<T>> attribute :
                copyRoot.nodes.entrySet())
        {
            DecisionGraph<T> attrBranch = attribute.getValue();

            if (attrBranch.isInternal())
            {
                root.addNode(attribute.getKey(),
                             subTree(attrBranch));
            }
            else
            {
                root.nodes.put(attribute.getKey(), attrBranch);
            }
        }

        return root;
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
        return !(isLeaf() || isJoin());
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
