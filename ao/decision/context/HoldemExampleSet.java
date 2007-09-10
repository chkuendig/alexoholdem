package ao.decision.context;

import ao.decision.attr.Attribute;
import ao.decision.data.HoldemExample;
import ao.holdem.def.state.env.TakenAction;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class HoldemExampleSet
{
    //-------------------------------------------------------------------
    private List<HoldemExample> firstActs;
    private List<HoldemExample> preFlops;
    private List<HoldemExample> postFlops;


    //-------------------------------------------------------------------
    public HoldemExampleSet()
    {
        firstActs = new ArrayList<HoldemExample>();
        preFlops  = new ArrayList<HoldemExample>();
        postFlops = new ArrayList<HoldemExample>();
    }


    //-------------------------------------------------------------------
    public void addAll(HoldemExampleSet data)
    {
        firstActs.addAll( data.firstActs );
        preFlops.addAll(  data.preFlops );
        postFlops.addAll( data.postFlops );
    }


    //-------------------------------------------------------------------
    public void add(HoldemContext context,
                    Attribute<TakenAction> targetAttribute)
    {
        add(new HoldemExample(context, targetAttribute));
    }
    public void add(HoldemExample example)
    {
        switch (example.domain())
        {
            case FIRST_ACT: firstActs.add( example ); break;
            case PRE_FLOP:  preFlops.add(  example ); break;
            case POST_FLOP: postFlops.add( example ); break;
        }
    }


    //-------------------------------------------------------------------
    public List<HoldemExample> firstActs()
    {
        return firstActs;
    }
    public List<HoldemExample> preFlops()
    {
        return preFlops;
    }
    public List<HoldemExample> postFlops()
    {
        return postFlops;
    }
}
