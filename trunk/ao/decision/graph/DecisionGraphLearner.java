package ao.decision.graph;

import ao.decision.DecisionLearner;
import ao.decision.attr.AttributeSet;
import ao.decision.data.Context;
import ao.decision.data.DataSet;
import ao.decision.data.Histogram;
import ao.util.stats.Combiner;

import java.util.List;

/**
 *
 */
public class DecisionGraphLearner<T>
        implements DecisionLearner<T>
{
    //--------------------------------------------------------------------
    private DecisionGraph<T> graph;


    //--------------------------------------------------------------------
    public DecisionGraphLearner()
    {

    }


    //--------------------------------------------------------------------
    public void train(DataSet<T> ds)
    {
        graph = induce(ds);
        System.out.println("graph = " + graph);
    }

    private DecisionGraph<T> induce(DataSet<T> ds)
    {
        DecisionGraph<T> root = new DecisionGraph<T>(ds);
        double cutoffLength = root.messageLength();

        while (true)
        {
            SplitSet<T> splits = new SplitSet<T>( cutoffLength );

            List<DecisionGraph<T>> leafs = root.leafs();
            for (DecisionGraph<T> leaf : leafs)
            {
                for (AttributeSet<?> attr : leaf.unsplitContexts())
                {
                    splits.add(leaf, attr);
                }
            }
            if (splits.shortest() == null) break;


            double           jMml        = Double.MAX_VALUE;
            DecisionGraph<T> jMmlCombo[] = null;
            for (int joinBy = 2; joinBy <= leafs.size(); joinBy++)
            {
                Combiner<DecisionGraph<T>> joinCombos =
                        new Combiner<DecisionGraph<T>>(
                                leafs.toArray(
                                    (DecisionGraph<T>[])new Object[0]),
                                joinBy);
                for (DecisionGraph<T> tryJoin[] : joinCombos)
                {
                    if (! splits.savingsOnIdenticalAttributes(tryJoin) )
                    {
                        continue;
                    }

                    DecisionGraph.join( tryJoin );

                    double tentativeLength = root.messageLength();
                    if (jMml > tentativeLength)
                    {
                        jMml      = tentativeLength;
                        jMmlCombo = tryJoin;
                    }

                    DecisionGraph.unjoin(tryJoin);
                }
            }

            if (cutoffLength > mml)
            {
                assert mmlLeaf != null; // to get rid of warning

                cutoffLength = mml;
                mmlLeaf.split( mmlAttr );
            }
            else break;
        }

        root.freeze();
        return root;
    }

//    private Map<Double, >



    //--------------------------------------------------------------------
    public Histogram<T> predict(Context context)
    {
        return null;
    }


}
