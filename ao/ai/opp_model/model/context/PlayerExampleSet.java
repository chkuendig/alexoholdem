package ao.ai.opp_model.model.context;

import ao.ai.opp_model.model.data.ActionExample;
import ao.ai.opp_model.decision2.example.LearningSet;

/**
 *
 */
public class PlayerExampleSet
{
    //-------------------------------------------------------------------
    private LearningSet firstActs;
    private LearningSet preFlops;
    private LearningSet postFlops;


    //-------------------------------------------------------------------
    public PlayerExampleSet()
    {
        firstActs = new LearningSet();
        preFlops  = new LearningSet();
        postFlops = new LearningSet();
    }


    //-------------------------------------------------------------------
    public void addAll(PlayerExampleSet data)
    {
        firstActs.addAll( data.firstActs );
         preFlops.addAll( data.preFlops );
        postFlops.addAll( data.postFlops );
    }


    //-------------------------------------------------------------------
    public void add(ActionExample example)
    {
        LearningSet addTo =
                example.isApplicableTo( ContextDomain.POST_FLOP )
                ? postFlops
                : example.isApplicableTo( ContextDomain.PRE_FLOP )
                  ? preFlops
                  : example.isApplicableTo( ContextDomain.FIRST_ACT )
                    ? firstActs : null;
        assert addTo != null;

        addTo.add( example );
    }


    //-------------------------------------------------------------------
    public LearningSet firstActs()
    {
        return firstActs;
    }
    public LearningSet preFlops()
    {
        return preFlops;
    }
    public LearningSet postFlops()
    {
        return postFlops;
    }
}
