package ao.ai.opp_model.decision.context;

import ao.ai.opp_model.decision.attr.Attribute;
import ao.ai.opp_model.decision.data.ActionExample;
import ao.holdem.model.act.SimpleAction;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class ActionExampleSet
{
    //-------------------------------------------------------------------
    private List<ActionExample> firstActs;
    private List<ActionExample> preFlops;
    private List<ActionExample> postFlops;


    //-------------------------------------------------------------------
    public ActionExampleSet()
    {
        firstActs = new ArrayList<ActionExample>();
        preFlops  = new ArrayList<ActionExample>();
        postFlops = new ArrayList<ActionExample>();
    }


    //-------------------------------------------------------------------
    public void addAll(ActionExampleSet data)
    {
        firstActs.addAll( data.firstActs );
        preFlops.addAll(  data.preFlops );
        postFlops.addAll( data.postFlops );
    }


    //-------------------------------------------------------------------
    public void add(ActionContext context,
                    Attribute<SimpleAction> targetAttribute)
    {
        add(new ActionExample(context, targetAttribute));
    }
    public void add(ActionExample example)
    {
        switch (example.domain())
        {
            case FIRST_ACT: firstActs.add( example ); break;
            case PRE_FLOP:  preFlops.add(  example ); break;
            case POST_FLOP: postFlops.add( example ); break;
        }
    }


    //-------------------------------------------------------------------
    public List<ActionExample> firstActs()
    {
        return firstActs;
    }
    public List<ActionExample> preFlops()
    {
        return preFlops;
    }
    public List<ActionExample> postFlops()
    {
        return postFlops;
    }
}
