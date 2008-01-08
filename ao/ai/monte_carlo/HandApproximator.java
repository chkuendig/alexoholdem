package ao.ai.monte_carlo;

import ao.ai.opp_model.decision.classification.RealHistogram;
import ao.ai.opp_model.decision.input.raw.example.Context;
import ao.ai.opp_model.input.ContextPlayer;
import ao.ai.opp_model.model.domain.HandStrength;
import ao.holdem.engine.Dealer;
import ao.holdem.engine.LiteralCardSource;
import ao.holdem.model.Money;
import ao.persist.HandHistory;
import ao.persist.PlayerHandle;
import ao.state.StateManager;

import java.util.HashMap;
import java.util.Map;

/**
 *
 */
public class HandApproximator
{
    //--------------------------------------------------------------------
    //@Inject PredictorService predictor;
    private PredictorService predictor;


    //--------------------------------------------------------------------
    public HandApproximator(PredictorService predictorService)
    {
        predictor = predictorService;
    }


    //--------------------------------------------------------------------
    public void examine(HandHistory history)
    {
        RealHistogram<PlayerHandle> approx = approximate(history);
        if (approx == null) return;

        Money        win    = Money.ZERO;
        PlayerHandle winner = null;
        for (Map.Entry<PlayerHandle, Money> delta :
                history.getDeltas().entrySet())
        {
            if (delta.getValue().compareTo( win ) > 0)
            {
                winner = delta.getKey();
                win    = delta.getValue();
            }
        }
        
        System.out.println(winner                + "\t" +
                           approx.mostProbable() + "\t" +
                           approx.sampleSize()   + "\t" +
                           approx);
    }


    //--------------------------------------------------------------------
    public RealHistogram<PlayerHandle>
            approximate(HandHistory history)
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


        Map<PlayerHandle, Context> showdownContexts =
                new HashMap<PlayerHandle, Context>();
        for (Map.Entry<PlayerHandle, ContextPlayer> e :
                brains.entrySet())
        {
            if (e.getValue().reachedShowdown())
            {
                showdownContexts.put(
                        e.getKey(),
                        e.getValue().contexts().get(
                                e.getValue().contexts().size() - 1));
            }
        }
        return approximate(showdownContexts);
    }

    private RealHistogram<PlayerHandle>
            approximate(Map<PlayerHandle, Context> showdownContexts)
    {
        if (showdownContexts.size() < 2) return null;

        int                         totalSample = 0;
        RealHistogram<PlayerHandle> approx      =
                new RealHistogram<PlayerHandle>();
        for (Map.Entry<PlayerHandle, Context> ctx :
                showdownContexts.entrySet())
        {
            RealHistogram<HandStrength> handStrength =
                    predictor.predictHand(ctx.getKey(),
                                          ctx.getValue());
            totalSample += handStrength.sampleSize();

            double strength = 0;
            for (HandStrength estimate : HandStrength.values())
            {
                strength += handStrength.probabilityOf( estimate ) *
                            estimate.averageValue();
            }
            approx.add( ctx.getKey(), strength );
        }

        approx.setSampleSize(
                (int) Math.ceil(
                    (double)totalSample /
                        showdownContexts.size() ));
        return approx;
    }
}
