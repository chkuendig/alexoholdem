package ao.decision;

import ao.holdem.bots.opp_model.mix.Classification;

/**
 *
 */
public class DecisionGraph<T extends Enum>
{

    //--------------------------------------------------------------------
    public DecisionGraph()
    {

    }

    //--------------------------------------------------------------------
//    public CategorySet()
//    {
//
//    }


    //--------------------------------------------------------------------
    // minumum number of bits needed to encode the graph.
    public int codingComplexity()
    {
        return 0;
    }


    //--------------------------------------------------------------------
    public Classification<T> predict(CategorySet basedOn)
    {
        return null;
    }
}
