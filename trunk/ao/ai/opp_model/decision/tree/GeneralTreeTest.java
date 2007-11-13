package ao.ai.opp_model.decision.tree;

import ao.ai.opp_model.decision.data.DataPool;
import ao.ai.opp_model.decision.data.Datum;
import ao.ai.opp_model.decision.example.*;
import ao.util.rand.Rand;

import java.util.Arrays;
import java.util.LinkedList;

/**
 *
 */
public class GeneralTreeTest
{
    //---------------------------------------------------------------------
    private GeneralTreeTest() {}


    //---------------------------------------------------------------------
    public static void main(String[] args)
    {
        new GeneralTreeTest().testMultivalue();
//        new GeneralTreeTest().testContinuous();
    }


    //---------------------------------------------------------------------
    public void testContinuous()
    {
        LearningSet        examples = new LearningSet();
        GeneralTreeLearner learner  = new GeneralTreeLearner();

        for (int i = 0; i < 40; i++)
        {
            double temp = Rand.nextDouble();

            TempClass clazz = TempClass.fromTemp(temp);
            examples.add(
                    new ExampleImpl(
                            new ContextImpl(Arrays.asList(
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
        COLD(0.33), FRIDGIT(0.61), WARM(0.66), HOT(0.9), BOILING(1);

        private final double  lessThan;
        private TempClass(double lt) {lessThan = lt;}

        public static TempClass fromTemp(double temp)
        {
            for (TempClass tempClass : values())
            {
                if (temp < tempClass.lessThan)
                {
                    return tempClass;
                }
            }
            return null;
        }
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
                            if (Rand.nextDouble() < 0.5) // introduce noice
                            {
                                func = !func;
                            }

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
        return new ContextImpl(varAttributes);
    }
}
