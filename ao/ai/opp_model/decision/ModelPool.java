package ao.ai.opp_model.decision;

import ao.ai.opp_model.decision.context.ContextDomain;
import ao.ai.opp_model.decision.context.ActionContext;
import ao.ai.opp_model.decision.context.ActionExampleSet;
import ao.ai.opp_model.decision.data.DataSet;
import ao.ai.opp_model.decision.data.Histogram;
import ao.ai.opp_model.decision.domain.HoldemHandParser;
import ao.ai.opp_model.decision.tree.DecisionTreeLearner;
import ao.ai.opp_model.mix.MixedAction;
import ao.holdem.model.act.SimpleAction;
import ao.persist.HandHistory;
import ao.persist.PlayerHandle;
import ao.util.rand.Rand;

import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

/**
 *
 */
public class ModelPool
{
    //-------------------------------------------------------------------
    private HoldemHandParser                                parser;
    private Set<Serializable>                               changed;
    private Map<Serializable, ActionExampleSet>             data;
    private Map<Serializable, DecisionLearner<SimpleAction>> firstActs;
    private Map<Serializable, DecisionLearner<SimpleAction>> preFlops;
    private Map<Serializable, DecisionLearner<SimpleAction>> postFlops;


    //-------------------------------------------------------------------
    public ModelPool()
    {
        parser    = new HoldemHandParser();
        changed   = new LinkedHashSet<Serializable>();
        data      = new HashMap<Serializable, ActionExampleSet>();
        firstActs = new HashMap<Serializable,
                                DecisionLearner<SimpleAction>>();
        preFlops  = new HashMap<Serializable,
                                DecisionLearner<SimpleAction>>();
        postFlops = new HashMap<Serializable,
                                DecisionLearner<SimpleAction>>();
    }



    //-------------------------------------------------------------------
    public synchronized void add(HandHistory hand)
    {
        for (PlayerHandle p : hand.getPlayers())
        {
            Serializable     key      = p.getId();
            ActionExampleSet examples = parser.examples(hand, p);
            ActionExampleSet existing = data.get(key);
            if (existing == null)
            {
                data.put(key, examples);
            }
            else
            {
                existing.addAll(examples);
            }
            changed.add( key );

            if (Rand.nextBoolean(20)) updateOne();
        }
    }


    //-------------------------------------------------------------------
    private void updateOne()
    {
        Serializable         key;
        DataSet<SimpleAction> newFirstActs = new DataSet<SimpleAction>();
        DataSet<SimpleAction> newPreFlops  = new DataSet<SimpleAction>();
        DataSet<SimpleAction> newPostFlops = new DataSet<SimpleAction>();
        synchronized (this)
        {
            if (changed.isEmpty()) return;
            key = changed.iterator().next();

            ActionExampleSet newData = data.get(key);
            newFirstActs.addAll( newData.firstActs() );
            newPreFlops.addAll(  newData.preFlops()  );
            newPostFlops.addAll( newData.postFlops() );

            changed.remove( key );
        }

        DecisionLearner<SimpleAction> firstActLearner = null;
        if (! newFirstActs.isEmpty())
        {
            firstActLearner = new DecisionTreeLearner<SimpleAction>();
            firstActLearner.train( newFirstActs );
        }

        DecisionLearner<SimpleAction> preFlopLearner = null;
        if (! newPreFlops.isEmpty())
        {
            preFlopLearner = new DecisionTreeLearner<SimpleAction>();
            preFlopLearner.train( newPreFlops );
        }

        DecisionLearner<SimpleAction> postFlopLearner = null;
        if (! newPostFlops.isEmpty())
        {
            postFlopLearner = new DecisionTreeLearner<SimpleAction>();
            postFlopLearner.train( newPostFlops );
        }

        synchronized (this)
        {
            firstActs.put(key, firstActLearner);
            preFlops.put( key, preFlopLearner );
            postFlops.put(key, postFlopLearner);
        }
    }


    //-------------------------------------------------------------------
    public synchronized MixedAction predict(
            PlayerHandle player, ActionContext ctx)
    {
        Predictor<SimpleAction> predictor =
                predictor(player.getId(), ctx.domain());
        if (predictor == null) return MixedAction.randomInstance();

        Histogram<SimpleAction> prediction = predictor.predict( ctx );
//        if (prediction.total() == 0) return MixedAction.randomInstance();

        return MixedAction.fromHistogram(prediction);
    }

    private Predictor<SimpleAction> predictor(
            Serializable key, ContextDomain forDomain)
    {
        switch (forDomain)
        {
            case FIRST_ACT: return firstActs.get(key);
            case PRE_FLOP:  return preFlops.get( key);
            case POST_FLOP: return postFlops.get(key);
            default: throw new Error("unknown domain " + forDomain);
        }
    }

}
