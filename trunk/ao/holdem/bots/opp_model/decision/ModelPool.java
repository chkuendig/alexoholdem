package ao.holdem.bots.opp_model.decision;

import ao.decision.DecisionLearner;
import ao.decision.Predictor;
import ao.decision.context.ContextDomain;
import ao.decision.context.HoldemContext;
import ao.decision.context.HoldemExampleSet;
import ao.decision.data.DataSet;
import ao.decision.data.Histogram;
import ao.decision.domain.HoldemHandParser;
import ao.decision.tree.DecisionTreeLearner;
import ao.holdem.bots.opp_model.mix.MixedAction;
import ao.holdem.def.state.env.TakenAction;
import ao.holdem.history.HandHistory;
import ao.holdem.history.PlayerHandle;
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
    private Map<Serializable, HoldemExampleSet>             data;
    private Map<Serializable, DecisionLearner<TakenAction>> firstActs;
    private Map<Serializable, DecisionLearner<TakenAction>> preFlops;
    private Map<Serializable, DecisionLearner<TakenAction>> postFlops;


    //-------------------------------------------------------------------
    public ModelPool()
    {
        parser    = new HoldemHandParser();
        changed   = new LinkedHashSet<Serializable>();
        data      = new HashMap<Serializable, HoldemExampleSet>();
        firstActs = new HashMap<Serializable,
                                DecisionLearner<TakenAction>>();
        preFlops  = new HashMap<Serializable,
                                DecisionLearner<TakenAction>>();
        postFlops = new HashMap<Serializable,
                                DecisionLearner<TakenAction>>();
    }



    //-------------------------------------------------------------------
    public synchronized void add(HandHistory hand)
    {
        for (PlayerHandle p : hand.getPlayers())
        {
            Serializable     key      = p.getId();
            HoldemExampleSet examples = parser.examples(hand, p);
            HoldemExampleSet existing = data.get(key);
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
        DataSet<TakenAction> newFirstActs = new DataSet<TakenAction>();
        DataSet<TakenAction> newPreFlops  = new DataSet<TakenAction>();
        DataSet<TakenAction> newPostFlops = new DataSet<TakenAction>();
        synchronized (this)
        {
            if (changed.isEmpty()) return;
            key = changed.iterator().next();

            HoldemExampleSet newData = data.get(key);
            newFirstActs.addAll( newData.firstActs() );
            newPreFlops.addAll(  newData.preFlops()  );
            newPostFlops.addAll( newData.postFlops() );

            changed.remove( key );
        }

        DecisionLearner<TakenAction> firstActLearner = null;
        if (! newFirstActs.isEmpty())
        {
            firstActLearner = new DecisionTreeLearner<TakenAction>();
            firstActLearner.train( newFirstActs );
        }

        DecisionLearner<TakenAction> preFlopLearner = null;
        if (! newPreFlops.isEmpty())
        {
            preFlopLearner = new DecisionTreeLearner<TakenAction>();
            preFlopLearner.train( newPreFlops );
        }

        DecisionLearner<TakenAction> postFlopLearner = null;
        if (! newPostFlops.isEmpty())
        {
            postFlopLearner = new DecisionTreeLearner<TakenAction>();
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
            PlayerHandle player, HoldemContext ctx)
    {
        Predictor<TakenAction> predictor =
                predictor(player.getId(), ctx.domain());
        if (predictor == null) return MixedAction.randomInstance();

        Histogram<TakenAction> prediction = predictor.predict( ctx );
//        if (prediction.total() == 0) return MixedAction.randomInstance();

        return MixedAction.fromHistogram(prediction);
    }

    private Predictor<TakenAction> predictor(
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
