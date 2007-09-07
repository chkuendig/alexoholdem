package ao.decision;

import ao.decision.attr.Attribute;
import ao.decision.attr.AttributePool;
import ao.decision.data.Context;
import ao.decision.data.DataSet;
import ao.decision.data.Example;
import ao.decision.graph.DecisionGraphLearner;

import java.util.Arrays;
import java.util.LinkedList;

/**
 *
 */
public class DecisionTest
{
    public void testDecisionTree()
    {
        AttributePool    attr = new AttributePool();
        DataSet<Boolean> data = new DataSet<Boolean>();

        // (a ^ b) v (c ^ d)
        for (int i = 0; i < 20; i++)
        {
            boolean yesNo[] = new boolean[]{true, false};
            for (boolean a : yesNo)
            {
                for (boolean b : yesNo)
                {
                    for (boolean c : yesNo)
                    {
                        for (boolean d : yesNo)
                        {
                            boolean func = (a && b) || (c && d);
//                            if (Rand.nextDouble() < 0.1) // introduce noice
//                            {
//                                func = !func;
//                            }

                            data.add( function(attr, a, b, c, d, func) );
                        }
                    }
                }
            }
        }

        DecisionLearner<Boolean> learner =
                new DecisionGraphLearner<Boolean>();
        learner.train( data );

        System.out.println(
                learner.predict(context(attr, true, false, true, true)));
    }

    private Example<Boolean> function(
            AttributePool attr,
            Boolean...    vars)
    {
        return context(attr, Arrays.copyOf(vars, vars.length - 1)).
                withTarget(attr.fromTyped(vars[ vars.length - 1 ]));
    }

    private Context context(
            AttributePool attr,
            Boolean...    vars)
    {
        LinkedList<Attribute> varAttributes =
                new LinkedList<Attribute>();
        Integer type = 0;
        for (Boolean var : vars)
        {
            varAttributes.add( attr.fromUntyped(type++, var) );
        }
        return new Context(varAttributes);
    }
}
