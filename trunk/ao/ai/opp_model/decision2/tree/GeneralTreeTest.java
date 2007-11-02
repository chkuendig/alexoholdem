package ao.ai.opp_model.decision2.tree;

import ao.ai.opp_model.decision2.data.DataPool;
import ao.ai.opp_model.decision2.data.Datum;
import ao.ai.opp_model.decision2.example.Context;
import ao.ai.opp_model.decision2.example.Example;
import ao.ai.opp_model.decision2.example.LearningSet;
import ao.util.rand.Rand;

import java.util.Arrays;
import java.util.LinkedList;

/**
 *
 */
public class GeneralTreeTest
{
    private GeneralTreeTest() {}

    public static void main(String[] args)
    {
//        new GeneralTreeTest().testMultivalue();
        new GeneralTreeTest().testContinuous();
    }

    public void testContinuous()
    {
        LearningSet        examples = new LearningSet();
        GeneralTreeLearner learner  = new GeneralTreeLearner();

        // 0.00 .. 0.33 => cold
        // 0.33 .. 0.67 => warm
        // 0.67 .. 1.00 => hot
        for (int i = 0; i < 100; i++)
        {
            double temp = Rand.nextDouble();

            TempClass clazz =
                    (temp < 0.33)
                     ? TempClass.COLD
                     : (temp < 0.67)
                        ? TempClass.WARM
                        : TempClass.HOT;

            examples.add(
                    new Example(
                            new Context(Arrays.asList(
                                    learner.pool().newContinuous(
                                        "temp", temp))),
                            learner.pool().newMultistate("target", clazz)));
        }
        learner.train( examples );

//        System.out.println(
//                learner.classify(context(learner.pool(),
//                                         true, false, true, true)));
    }

    private static enum TempClass
    {
        COLD, WARM, HOT
    }


    //---------------------------------------------------------------------
    public void testMultivalue()
    {
        LearningSet        examples = new LearningSet();
        GeneralTreeLearner learner  = new GeneralTreeLearner();

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
            DataPool   attr,
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
        return new Context(varAttributes);
    }
}
