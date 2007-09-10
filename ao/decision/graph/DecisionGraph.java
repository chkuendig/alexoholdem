package ao.decision.graph;

import ao.decision.Predictor;
import ao.decision.attr.Attribute;
import ao.decision.attr.AttributeSet;
import ao.decision.data.DataSet;
import ao.decision.data.Histogram;
import ao.util.stats.Combo;
import ao.util.stats.Info;
import ao.util.text.Txt;

import java.util.*;

/**
 * see Tan & Dowe (2003)
 */
public class DecisionGraph<T> implements Predictor<T>
{
    //--------------------------------------------------------------------
    private static final double DEFAULT_LEAF_PROB = 0.8;
    private static final double ALPHA             = 0.01;
    private static final double ZERO_PROB         = 0.01;

    private static int nextId = 0;


    //--------------------------------------------------------------------
    private int                                 id;
    private List<DecisionGraph<T>>              parents;
    private Map<Attribute<?>, DecisionGraph<T>> nodes;
    private DecisionGraph<T>                    joinNode;
    private AttributeSet<?>                     attrSet;
    private Histogram<T>                        hist;
    private DataSet<T>                          data;

    private int leafNonInternals;
    private int forwardNonInternals;


    //--------------------------------------------------------------------
    private DecisionGraph()
    {
        id      = nextId++;
        nodes   = new TreeMap<Attribute<?>, DecisionGraph<T>>();
        parents = new ArrayList<DecisionGraph<T>>();
    }
    public DecisionGraph(DataSet<T> ds)
    {
        this();
        hist = ds.frequencies();
        data = ds;
    }
    private DecisionGraph(DecisionGraph<T> copyNonKids)
    {
        this();
        id      = copyNonKids.id;
        hist    = copyNonKids.hist;
        data    = copyNonKids.data;
        attrSet = copyNonKids.attrSet;

        leafNonInternals    = copyNonKids.leafNonInternals;
        forwardNonInternals = copyNonKids.forwardNonInternals;
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
        return parents.isEmpty() ? null : parents.get(0);
    }

    public Collection<DecisionGraph<T>> kids()
    {
        return nodes.values();
    }

    public List<DecisionGraph<T>> leafs()
    {
        Collection<DecisionGraph<T>> leafs   = recursiveLeafs();
        Set<DecisionGraph<T>>        leafSet =
                new HashSet<DecisionGraph<T>>(leafs);
        return new ArrayList<DecisionGraph<T>>(leafSet);
    }
    private Collection<DecisionGraph<T>> recursiveLeafs()
    {
        Collection<DecisionGraph<T>> leafs =
                new ArrayList<DecisionGraph<T>>();

        if (isLeaf())
        {
            leafs.add( this );
        }
        else
        {
            if (isForward())
            {
                leafs.addAll( joinNode.leafs() );
            }
            else
            {
                for (DecisionGraph<T> child : kids())
                {
                    leafs.addAll( child.leafs() );
                }
            }
        }

        return leafs;
    }

    private List<DecisionGraph<T>> forwards()
    {
        List<DecisionGraph<T>> forewards =
                new ArrayList<DecisionGraph<T>>();

        if (isForward())
        {
            forewards.add( this );
        }
        else
        {
            for (DecisionGraph<T> child : kids())
            {
                forewards.addAll( child.forwards() );
            }
        }

        return forewards;
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
//        return graphCodingLength() +
//               data.codingLength( root() );
        return graphCodingLength();
    }

    private double graphCodingLength()
    {
        leafNonInternals    = 0;
        forwardNonInternals = 0;

        double length = 0;
        Queue<DecisionGraph<T>> openRoots =
                new LinkedList<DecisionGraph<T>>();
        List<DecisionGraph<T>> oldJoins =
                new ArrayList<DecisionGraph<T>>();
        List<DecisionGraph<T>> newForewards =
                new ArrayList<DecisionGraph<T>>();

        openRoots.add( this );
        while (! openRoots.isEmpty())
        {
            DecisionGraph<T> root = subTree(openRoots.poll());
            newForewards.addAll( root.forwards() );
            length += root.treeCodingLength();

            if (newForewards.isEmpty() && oldJoins.isEmpty()) break;

            int n = newForewards.size();
            int q = oldJoins.size();
            length += Info.log2(Math.min(n, (n+q)/2.0));// transmit M

            List<Collection<DecisionGraph<T>>> comboPatterns =
                    extractComboPatterns(oldJoins, newForewards);
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
                        newForewards.remove( aJoin );
                        newCombo.add( aJoin );
                    }
                }
            }
            length += comboPatternLength(n, q,
                                         oldCombos, newCombos,
                                         oldJoins,  newForewards);
        }

        oldJoins.addAll( newForewards );
        newForewards.clear();
        return length;
    }

    
    //--------------------------------------------------------------------
    private double comboPatternLength(
            int n, int q,
            List<Collection<DecisionGraph<T>>> oldCombos,
            List<Collection<DecisionGraph<T>>> newCombos,
            List<DecisionGraph<T>>             remainOldJoins,
            List<DecisionGraph<T>>             remainNewJoins)
    {
        // # of childred of joins, already transmitted.
        int m = oldCombos.size();

        // "new" nodes, among pending nodes
        int y = remainNewJoins.size();

        // pending nodes not involved in any join
        int p = remainOldJoins.size() + y;

        int x[] = new int[ m ]; // "new" nodes in each joining group
        int j[] = new int[ m ]; // total nodes in each joining group
        for (int i = 0; i < m; i++)
        {
            x[ i ] = newCombos.get(i).size();
            j[ i ] = oldCombos.get(i).size() + x[ i ];
        }

        return Info.log2( pjSolutionCount(n, q, m) ) +
               Info.log2( xySolutionCount(p, n, j) ) +
               Info.log2( openListPermuations(n, q, y, p, x, j) );
    }


    //--------------------------------------------------------------------
    private int pjSolutionCount(int n, int q, int m)
    {
        // # of solutions to
        //  p + j[0] + j[1] + ... + j[m-1] = n + q
        //  where p >= 0, j[i] >= 2, i = 0..(m-1)
        int goal = n + q;

        int count = 0;
        int limit = goal - 2*m;
        for (int p = 0; p <= limit; p++)
        {
            count += jPermute(p, 0, m, goal);
        }
        return count;
    }
    private int jPermute(int total, int i, int m, int goal)
    {
        if (total > goal) return 0;
        if (i == m) return (total == goal) ? 1 : 0;

        int count = 0;
        int limit = goal - total - 2*(m - i - 1);
        for (int j = 2; j <= limit; j++)
        {
            count += jPermute(total + j, i + 1, m, goal);
        }
        return count;
    }


    //--------------------------------------------------------------------
    private int xySolutionCount(int p, int n, int j[])
    {
        // # of solutions to
        //  y + x[0] + x[1] + ... + x[m] = n
        //  where 0 <= y <= p, 1 <= x[i] <= j[i], i = 0..(m-1)

        int count = 0;
        for (int y = 0; y <= p; y++)
        {
            count += xPermute(y, 0, j, n);
        }
        return count;
    }
    private int xPermute(int total, int i, int j[], int goal)
    {
        if (total > goal) return 0;
        if (i == j.length) return (total == goal) ? 1 : 0;

        int count = 0;
        for (int x = 1; x <= j[i]; x++)
        {
            count += xPermute(total + x, i + 1, j, goal);
        }
        return count;
    }


    //--------------------------------------------------------------------
    private long openListPermuations(
            int n, int q, int y, int p, int x[], int j[])
    {
        long nFact = Combo.smallFactorial( n );
        long qFact = Combo.smallFactorial( q );

        long yFact       = Combo.smallFactorial( y     );
        long pyDeltaFact = Combo.smallFactorial( p - y );

        long xFactProd       = 1;
        long jxDeltaFactProd = 1;
        for (int i = 0; i < x.length; i++)
        {
            xFactProd       *= Combo.smallFactorial(        x[i] );
            jxDeltaFactProd *= Combo.smallFactorial( j[i] - x[i] );
        }

        return (nFact / (yFact * xFactProd)) *
                (qFact / (pyDeltaFact * jxDeltaFactProd));
    }


    //--------------------------------------------------------------------
    private List<Collection<DecisionGraph<T>>>
                extractComboPatterns(List<DecisionGraph<T>> forewards)
    {
        List<Collection<DecisionGraph<T>>> combos =
                new ArrayList<Collection<DecisionGraph<T>>>();
        while (! forewards.isEmpty())
        {
            DecisionGraph<T> aJoin = forewards.get( forewards.size()-1 );
            Collection<DecisionGraph<T>> combo =
                    new ArrayList<DecisionGraph<T>>();

            DecisionGraph<T> joinDest = aJoin.joinNode;
            for (DecisionGraph<T> partner : forewards)
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
            forewards.removeAll( combo );
        }
        return combos;
    }
    private List<Collection<DecisionGraph<T>>>
                extractComboPatterns(List<DecisionGraph<T>> oldJoins,
                                     List<DecisionGraph<T>> newJoins)
    {
        List<DecisionGraph<T>> joins = new ArrayList<DecisionGraph<T>>();
        joins.addAll( oldJoins );
        joins.addAll( newJoins );
        return extractComboPatterns(joins);
    }


    //--------------------------------------------------------------------
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
    private double treeCodingLength()
    {
        assert data != null : "cannot count length of frozen tree";
        return codingComplexity(
                        data.contextAttributes().size(), null, this);
    }

    private double codingComplexity(
            int              availAttributes,
            DecisionGraph<T> parent,
            DecisionGraph<T> root)
    {
        double type   = typeLength(availAttributes, parent, root);
        double struct = structureLength(availAttributes, root);
        return type + struct;
    }


    //--------------------------------------------------------------------
    private double structureLength(
            int              availAttributes,
            DecisionGraph<T> root)
    {
        if (isInternal())
        {
            return attributeAndChildLength(availAttributes, root);
        }
        else if (isLeaf())
        {
            return categoryLength(ALPHA);
//            return 0;
        }
        else /* if (isForward()) */
        {
            // already accounted for by appending lengths
            //  of decomposed sub-trees to the graph's length.
            return 0;
        }
    }


    private double attributeAndChildLength(
            int              availAttributes,
            DecisionGraph<T> root)
    {
        double attrTypeLength = Info.log2( availAttributes );

        double childLengthSum = 0;
        for (DecisionGraph<T> child : kids())
        {
            childLengthSum += child.codingComplexity(
                                availAttributes - 1, this, root);

            if (child.isForward())
            {
                root.forwardNonInternals++;
            }
            else if (child.isLeaf())
            {
                root.leafNonInternals++;
            }
        }
        return attrTypeLength + childLengthSum;
    }

    private double categoryLength(double alpha)
    {
        //int numClasses = hist.numClasses();
        int numClasses = hist.mostProbable().set().values().size();

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


    //--------------------------------------------------------------------
    private double typeLength(
            int              availAttributes,
            DecisionGraph<T> parent,
            DecisionGraph<T> root)
    {
        return Info.cost( typeProbability(availAttributes,
                                          parent,
                                          root) );
    }
    private double typeProbability(
            int              availAttributes,
            DecisionGraph<T> parent,
            DecisionGraph<T> root)
    {
        if (isRoot() || isJoin())
        {
            double leafProb = 1.0/((double) availAttributes);

            if (isLeaf())
            {
                return leafProb;
            }
            else /* if (isInternal()) */
            {
                return 1.0 - leafProb;
            }
        }
        else
        {
            double internalProb = 1.0/((double) parent.kids().size());

            if (isInternal())
            {
                return internalProb;
            }
            else
            {
                double nonInternalProb  = 1.0 - internalProb;
                return leafVsForwardProb(root) *
                        nonInternalProb;
            }
        }
    }
    private double leafVsForwardProb(DecisionGraph<T> root)
    {
        int nonInternalCount =
                    root.forwardNonInternals + root.leafNonInternals;
        if (nonInternalCount == 0)
        {
            return (isLeaf() ? DEFAULT_LEAF_PROB
                             : 1.0 - DEFAULT_LEAF_PROB);
        }

        int particularCount =
                isLeaf() ? root.leafNonInternals
                         : root.forwardNonInternals;
        return particularCount == 0
                ? ZERO_PROB
                : particularCount /
                    ((double) nonInternalCount);
    }


    //--------------------------------------------------------------------
    private boolean isRoot()
    {
        return parents.isEmpty();
    }
    private boolean isForward()
    {
        return joinNode != null;
    }
    private boolean isJoin()
    {
        return parents.size() > 1;
    }
    private boolean isInternal()
    {
        return !(kids().isEmpty() || isForward() || isJoin());
    }
    private boolean isLeaf()
    {
        return !isForward() && kids().isEmpty();
    }


    //--------------------------------------------------------------------
    public Histogram<T> predict(Context basedOn)
    {
        if (joinNode != null) return joinNode.predict(basedOn);

        Attribute<?> attribute = basedOn.attribute(attrSet);
        if (attribute == null) return hist;

        DecisionGraph<T> subTree = nodes.get( attribute );
        if (subTree == null)   return hist;

        return subTree.predict( basedOn );
    }


    //--------------------------------------------------------------------
    public String toString()
    {
        Queue<DecisionGraph<T>> closedRoots =
                new LinkedList<DecisionGraph<T>>();
        Queue<DecisionGraph<T>> openRoots =
                new LinkedList<DecisionGraph<T>>();
        List<DecisionGraph<T>> forewards =
                new ArrayList<DecisionGraph<T>>();

        openRoots.add( this );
        while (! openRoots.isEmpty())
        {
            DecisionGraph<T> root = subTree(openRoots.poll());
            closedRoots.add( root );

            forewards.addAll( root.forwards() );
            if (forewards.isEmpty()) break;

            List<Collection<DecisionGraph<T>>> comboPatterns =
                    extractComboPatterns(forewards);

            for (Collection<DecisionGraph<T>> combo : comboPatterns)
            {
                openRoots.add( combo.iterator().next().joinNode );
                forewards.removeAll( combo );
            }
        }

        StringBuilder b = new StringBuilder();
        for (DecisionGraph<T> root : closedRoots)
        {
            b.append("root ").append(root.id).append("\n");
            root.appendTree(1, b);
        }
        return b.toString();
    }

    private void appendTree(int depth, StringBuilder buf)
    {
        if (attrSet == null ||
                isForward() && kids().isEmpty())
        {
            buf.append( hist );
            return;
        }

        buf.append(Txt.nTimes("\t", depth));
        buf.append("***");
        buf.append(attrSet.type()).append("\n");

        for (Attribute<?> attribute : nodes.keySet())
        {
            buf.append(Txt.nTimes("\t", depth + 1));
            buf.append("+").append(attribute);

            DecisionGraph child = nodes.get(attribute);
            if (child.isInternal())
            {
                buf.append("\n");
                child.appendTree(depth + 1, buf);
            }
            else
            {
                buf.append(" ");
                buf.append( child.isLeaf() ? child.hist : child.joinNode.id );
                buf.append("\n");
            }
        }
	}


    //--------------------------------------------------------------------
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DecisionGraph that = (DecisionGraph) o;
        return id == that.id;
    }
    public int hashCode()
    {
        return id;
    }
}
