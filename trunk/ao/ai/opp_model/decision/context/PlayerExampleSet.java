package ao.ai.opp_model.decision.context;

import ao.ai.opp_model.decision.attr.Attribute;
import ao.ai.opp_model.decision.data.ActionExample;
import ao.ai.opp_model.decision.data.DataSet;
import ao.ai.opp_model.decision.data.HoldemExample;
import ao.holdem.model.act.SimpleAction;

/**
 *
 */
public class PlayerExampleSet
{
    //-------------------------------------------------------------------
    private DataSet<SimpleAction> firstActs;
    private DataSet<SimpleAction> preFlops;
    private DataSet<SimpleAction> postFlops;


    //-------------------------------------------------------------------
    public PlayerExampleSet()
    {
        firstActs = new DataSet<SimpleAction>();
        preFlops  = new DataSet<SimpleAction>();
        postFlops = new DataSet<SimpleAction>();
    }


    //-------------------------------------------------------------------
    public void addAll(PlayerExampleSet data)
    {
        firstActs.addAll( data.firstActs );
        preFlops.addAll(  data.preFlops );
        postFlops.addAll( data.postFlops );
    }


    //-------------------------------------------------------------------
    public void add(HoldemContext context,
                    Attribute<SimpleAction> targetAttribute)
    {
        add(new ActionExample(context, targetAttribute));
    }
    public void add(HoldemExample<SimpleAction> example)
    {
        DataSet<SimpleAction> addTo =
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
    public DataSet<SimpleAction> firstActs()
    {
        return firstActs;
    }
    public DataSet<SimpleAction> preFlops()
    {
        return preFlops;
    }
    public DataSet<SimpleAction> postFlops()
    {
        return postFlops;
    }
}
