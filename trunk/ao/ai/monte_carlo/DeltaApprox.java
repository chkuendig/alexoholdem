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
import ao.ai.opp_model.model.domain.HandStrength;
import ao.holdem.engine.Dealer;
import ao.holdem.engine.LiteralCardSource;
import ao.holdem.model.BettingRound;
import ao.holdem.model.Money;
import ao.persist.Event;
import ao.persist.HandHistory;
import ao.persist.PlayerHandle;
import ao.state.StateManager;

import java.util.*;

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
    public void examine(HandHistory history)
    {
        RealHistogram<PlayerHandle> approx = approximate(history);
        if (approx == null) return;

        Money        win    = Money.ZERO;
        PlayerHandle winner = null;
        for (Map.Entry<PlayerHandle, Money> delta :
                history.getDeltas().entrySet())
        {
            if (delta.getValue().compareTo( win ) >= 0)
            {
                winner = delta.getKey();
                win    = delta.getValue();
            }
        }
        assert winner != null;
        
        PlayerHandle probable = approx.mostProbable();
        boolean      isWin    = winner.equals(probable);
        System.out.println((isWin ? 1 : 0)     + "\t" +
                           winner              + "\t" +
                           probable            + "\t" +
                           approx.sampleSize() + "\t" +
                           approx);
    }


    //--------------------------------------------------------------------
    public RealHistogram<PlayerHandle> approximate(HandHistory history)
    {
        return approximate(extractChoices(history, null));
    }

    public RealHistogram<PlayerHandle>
            approximate( Map<PlayerHandle, List<Choice>> choices )
    {
        if (choices.size() != 2) return null;

        int sample = Integer.MAX_VALUE;
        Map<PlayerHandle, RealHistogram<HandStrength>> hands =
                new HashMap<PlayerHandle, RealHistogram<HandStrength>>();
        for (Map.Entry<PlayerHandle, List<Choice>> c :
                choices.entrySet())
        {
            RealHistogram<HandStrength> handStrength =
                    approximate(c.getValue());
            sample = Math.min(sample, handStrength.sampleSize());
            hands.put(c.getKey(), handStrength);

//            double strength = 0;
//            for (HandStrength estimate : HandStrength.values())
//            {
//                strength += handStrength.probabilityOf( estimate ) *
//                            estimate.averageValue();
//            }
//            approx.add( c.getKey(), strength );


        }

        Iterator<PlayerHandle>      players = hands.keySet().iterator();
        PlayerHandle                playerA = players.next();
        RealHistogram<HandStrength> handA   = hands.get( playerA );
        PlayerHandle                playerB = players.next();
        RealHistogram<HandStrength> handB   = hands.get( playerB );

        double tieProb   = 0;
        double aWinsProb = 0;
        for (int i = HandStrength.values().length - 1; i >=0; i--)
        {
            HandStrength hs    = HandStrength.values()[ i ];
            double       probA = handA.probabilityOf(hs);

            //tieProb += probA * handB.probabilityOf(hs);

            double probLessThanA = 0;
            for (int j = 0; j < i; j++)
            {
                probLessThanA +=
                    handB.probabilityOf(HandStrength.values()[ j ]);
            }
            aWinsProb += (probA * probLessThanA);
        }

        RealHistogram<PlayerHandle> approx =
                new RealHistogram<PlayerHandle>();
        approx.add(playerA, aWinsProb);
        approx.add(playerB, 1.0 - aWinsProb - tieProb);
        approx.setSampleSize(sample);
        return approx;
    }


    //--------------------------------------------------------------------
    public void learn(HandHistory history)
    {
        learn(history, null);
    }
    public void learn(HandHistory history, PlayerHandle onlyFor)
    {
        for (Map.Entry<PlayerHandle, List<Choice>> choice :
                extractChoices(history, onlyFor).entrySet())
        {
            List<Choice> choiceList = choice.getValue();
            Choice       lastChoice =
                            choiceList.get( choiceList.size()-1 );

            HandStrength strength = HandStrength.fromState(
                    lastChoice.state(),
                    history.getCommunity(),
                    history.getHoles().get( choice.getKey() ));
            learn( choiceList, strength );
        }
    }

    private void learn(List<Choice> surprises, HandStrength actual)
    {
        Context      ctx       = contextFor(surprises);
        Prediction   p         = deltas.classify(ctx);
        HandStrength predicted =
                (HandStrength) p.toRealHistogram().mostProbable();
//        System.out.println((actual.equals(predicted) ? 1 : 0)
//                                          + "\t" +
//                           actual         + "\t" +
//                           predicted      + "\t" +
//                           p.sampleSize() + "\t" +
//                           p);
        confusion.add(actual, predicted);

        deltas.add( ctx.withTarget(new Datum(actual)) );
    }

    private Map<PlayerHandle, List<Choice>>
            extractChoices(HandHistory history, PlayerHandle onlyFor)
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

        Map<PlayerHandle, List<Choice>> choices =
                new HashMap<PlayerHandle, List<Choice>>();
        for (Map.Entry<PlayerHandle, ContextPlayer> playerEntry :
                brains.entrySet())
        {
            PlayerHandle  player    = playerEntry.getKey();
            ContextPlayer ctxPlayer = playerEntry.getValue();
            if (! ctxPlayer.reachedEndOfHand() ||
                    ! history.holesVisible( player )) continue;
            if (onlyFor != null && !player.equals( onlyFor )) continue;

            List<Choice>   s   = new ArrayList<Choice>();
            int            i   = 0;
            List<Context>  ctx = ctxPlayer.contexts();
            for (Event e : history.getEvents( player ))
            {
                if (e.getAction().isBlind()) continue;

                s.add(new Choice(
                            actPredictor.predictAction(
                                        player, ctx.get(i)),
                            e.getAction().toSimpleAction(),
                            e.getRound(),
                            ctxPlayer.states().get( i )));
                i++;
            }

            choices.put(player, s);
        }
        return choices;
    }


    //--------------------------------------------------------------------
    @SuppressWarnings("unchecked")
    public RealHistogram<HandStrength>
            approximate( List<Choice> surprises )
    {
        return (RealHistogram<HandStrength>)
                deltas.classify( contextFor(surprises) )
                        .toRealHistogram();
    }


    //--------------------------------------------------------------------
    private Context contextFor(List<Choice> surprises)
    {
        int          count     = 0;
        BettingRound prevRound = null;

        Context ctx = new ContextImpl();
        for (Choice s : surprises)
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

