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
    public DecisionGraphLearner() {}


    //--------------------------------------------------------------------
    public synchronized void train(DataSet<T> ds)
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
//            System.out.println("---------------------------------------");
//            System.out.println("progress " + root + " : " + cutoffLength);
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

            JoinSet<T> joins = new JoinSet<T>(splits);
            for (int joinBy = 2;
                     joinBy <= Math.min(leafs.size(), 6);
                     joinBy++)
            {
                for (DecisionGraph<T> tryJoin[] :
                        leafPermute(leafs, joinBy))
                {
                    joins.add( tryJoin );
                }
            }

            ((joins.shortestUseful() == null)
                    ? splits.shortest()
                    : joins.shortestUseful()).apply();
            cutoffLength = root.messageLength();
        }

        root.freeze();
        return root;
    }

    @SuppressWarnings("unchecked")
    private Iterable<DecisionGraph<T>[]> leafPermute(
            List<DecisionGraph<T>> leafs, int by)
    {
        return new Combiner<DecisionGraph<T>>(
                    leafs.toArray(
                            new DecisionGraph[leafs.size()]), by);
    }



    //--------------------------------------------------------------------
    public Histogram<T> predict(Context context)
    {
        return graph.predict( context );
    }
}
