package ao.ai.opp_model.decision2.random;

import ao.ai.opp_model.decision2.Classifier;
import ao.ai.opp_model.decision2.data.DataPool;
import ao.ai.opp_model.decision2.data.Datum;
import ao.ai.opp_model.decision2.example.Context;
import ao.ai.opp_model.decision2.example.ContextImpl;
import ao.ai.opp_model.decision2.example.Example;
import ao.ai.opp_model.decision2.example.LearningSet;

import java.util.Arrays;
import java.util.LinkedList;

/**
 *
 */
public class RandomTreeTest
{
    //---------------------------------------------------------------------
    private RandomTreeTest() {}


    //---------------------------------------------------------------------
    public static void main(String[] args)
    {
        new RandomTreeTest().testMultivalue();
    }


    //---------------------------------------------------------------------
    public void testMultivalue()
    {
        LearningSet examples = new LearningSet();
        Classifier  learner  = new RandomLearner();

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

                            examples.add( function(learner.pool(),
                                                   a, b, c, d, func) );
                        }
                    }
                }
            }
        }
        learner.train( examples );

        System.out.println(
                learner.classify(context(learner.pool(),
                                         true, false, true, true)));
    }

    private Example function(
            DataPool attr,
            Boolean... vars)
    {
        return context(attr, Arrays.copyOf(vars, vars.length - 1)).
                withTarget(attr.fromTyped(vars[ vars.length - 1 ]));
    }

    private Context context(
            DataPool   attr,
            Boolean... vars)
    {
        LinkedList<Datum> varAttributes =
                new LinkedList<Datum>();
        Integer type = 0;
        for (Boolean var : vars)
        {
            varAttributes.add(
                    attr.newMultistate(String.valueOf(type++), var) );
        }
        return new ContextImpl(varAttributes);
    }
}
