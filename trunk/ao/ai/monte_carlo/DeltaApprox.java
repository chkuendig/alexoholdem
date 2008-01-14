package ao.ai.monte_carlo;

import ao.ai.opp_model.classifier.raw.Classifier;
import ao.ai.opp_model.classifier.raw.DomainedClassifier;
import ao.ai.opp_model.decision.classification.ConfusionMatrix;
import ao.ai.opp_model.decision.classification.RealHistogram;
import ao.ai.opp_model.decision.classification.raw.Prediction;
import ao.ai.opp_model.decision.input.raw.example.Context;
import ao.ai.opp_model.decision.input.raw.example.ContextImpl;
import ao.ai.opp_model.decision.input.raw.example.Datum;
import ao.ai.opp_model.decision.random.RandomLearner;
import ao.ai.opp_model.input.ContextPlayer;
import ao.ai.opp_model.mix.MixedAction;
import ao.ai.opp_model.model.domain.HandStrength;
import ao.holdem.engine.Dealer;
import ao.holdem.engine.LiteralCardSource;
import ao.holdem.model.BettingRound;
import ao.persist.Event;
import ao.persist.HandHistory;
import ao.persist.PlayerHandle;
import ao.state.StateManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Approximates the difference between the average
 *  expected hand strength, and the hand strength
 *  of a particular player.
 */
public class DeltaApprox
{
    //--------------------------------------------------------------------
    private Classifier deltas =
                new DomainedClassifier(new RandomLearner.Factory());
    private PredictorService actPredictor;
    private ConfusionMatrix<HandStrength> confusion =
                new ConfusionMatrix<HandStrength>();


    //--------------------------------------------------------------------
    public DeltaApprox(PredictorService actPredictor)
    {
        this.actPredictor = actPredictor;
    }


    //--------------------------------------------------------------------
//    public RealHistogram<PlayerHandle> approximate(HandHistory history)
//    {
//        return null;
//    }

    public RealHistogram<PlayerHandle>
            approximate( Map<PlayerHandle, List<Surprise>> acts )
    {

        return null;
    }


    //--------------------------------------------------------------------
    public void learn(HandHistory history)
    {
        StateManager start =
                new StateManager(history.getPlayers(),
                                 new LiteralCardSource(history));

        Map<PlayerHandle, ContextPlayer> brains =
                new HashMap<PlayerHandle, ContextPlayer>();
        for (PlayerHandle player : history.getPlayers())
        {
            brains.put(player,
                       new ContextPlayer(history, player));
        }

        new Dealer(start, brains).playOutHand();


        for (Map.Entry<PlayerHandle, ContextPlayer> playerEntry :
                brains.entrySet())
        {
            PlayerHandle  player    = playerEntry.getKey();
            ContextPlayer ctxPlayer = playerEntry.getValue();
            if (! ctxPlayer.reachedEndOfHand() ||
                    ! history.holesVisible( player )) continue;

            List<Surprise> s   = new ArrayList<Surprise>();
            int            i   = 0;
            List<Context>  ctx = ctxPlayer.contexts();
            for (Event e : history.getEvents( player ))
            {
                if (e.getAction().isBlind()) continue;
                
                MixedAction act =
                        MixedAction.fromHistogram(
                                actPredictor.predictAction(
                                        player, ctx.get(i)));
                s.add(new Surprise(
                            act,
                            e.getAction().toSimpleAction(),
                            e.getRound(),
                            ctxPlayer.states()
                                    .get( i ).nextToActCanRaise()));
                i++;
            }

            HandStrength strength = HandStrength.fromState(
                    ctxPlayer.states().get(i-1),
                    history.getCommunity(),
                    history.getHoles().get( player ));
            learn( s, strength );
        }
    }

    private void learn(List<Surprise> surprises, HandStrength actual)
    {
        Context      ctx       = contextFor(surprises);
        Prediction   p         = deltas.classify(ctx);
        HandStrength predicted =
                (HandStrength) p.toRealHistogram().mostProbable();
        System.out.println((actual.equals(predicted) ? 1 : 0)
                                          + "\t" +
                           actual         + "\t" +
                           predicted      + "\t" +
                           p.sampleSize() + "\t" +
                           p);
        confusion.add(actual, predicted);

        deltas.add( ctx.withTarget(new Datum(actual)) );
    }


    //--------------------------------------------------------------------
    @SuppressWarnings("unchecked")
    public RealHistogram<HandStrength>
            approximate( List<Surprise> surprises )
    {
        return (RealHistogram<HandStrength>)
                deltas.classify( contextFor(surprises) )
                        .toRealHistogram();
    }


    //--------------------------------------------------------------------
    private Context contextFor(List<Surprise> surprises)
    {
        int          count     = 0;
        BettingRound prevRound = null;

        Context ctx = new ContextImpl();
        for (Surprise s : surprises)
        {
            if (prevRound != s.round())
            {
                count = 0;
            }

            ctx.add( new Datum(s.round().toString() + " " + count,
                               s.surprise()) );

            prevRound = s.round();
            count++;
        }

        return ctx;
    }

    
    //--------------------------------------------------------------------
    public String toString()
    {
        return confusion.toString();
    }
}

