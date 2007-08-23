package ao.decision;

import ao.decision.attr.Attribute;
import ao.decision.attr.AttributePool;
import ao.decision.data.DataSet;
import ao.decision.data.Example;

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

        // a XOR b
        data.add( function(attr, false, false, false) );
        data.add( function(attr, false, true,   true) );
        data.add( function(attr, true,  false,  true) );
        data.add( function(attr, true,  true,  false) );

        DecisionLearner<Boolean> learner =
                new DecisionLearner<Boolean>();
        learner.train( data );

        learner.predict();
    }

    private Example<Boolean> function(
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
        Attribute<Boolean> target = varAttributes.removeLast();

        return new Example<Boolean>(varAttributes, target);
    }

}
