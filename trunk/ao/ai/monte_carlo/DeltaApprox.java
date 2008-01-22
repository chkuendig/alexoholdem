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
import ao.holdem.model.act.SimpleAction;
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
    private static final String[][] dataNameCache = new String[4][4];
    static
    {
        for (BettingRound r : BettingRound.values())
        {
            for (int i = 0; i < 4; i++)
            {
                dataNameCache[ r.ordinal() ][ i ] =
                        r.toString() + " " + i;
            }
        }
    }



    //--------------------------------------------------------------------
    private Classifier deltas =
                new DomainedClassifier(new RandomLearner.Factory());
    private HoldemPredictor<SimpleAction> actPredictor;
    private ConfusionMatrix<HandStrength> confusion =
                new ConfusionMatrix<HandStrength>();


    //--------------------------------------------------------------------
    public DeltaApprox(HoldemPredictor<SimpleAction> actPredictor)
    {
        this.actPredictor = actPredictor;
    }


    //--------------------------------------------------------------------
    public void examine(HandHistory history)
    {
        RealHistogram<PlayerHandle> approx = approximateShowdown(history);
        if (approx == null) return;

        Money win = Money.ZERO;
        for (Map.Entry<PlayerHandle, Money> delta :
                history.getDeltas().entrySet())
        {
            int cmp = delta.getValue().compareTo( win );
            if (cmp > 0) win = delta.getValue();
        }
        PlayerHandle winner = null;
        for (Map.Entry<PlayerHandle, Money> delta :
                history.getDeltas().entrySet())
        {
            int cmp = delta.getValue().compareTo( win );
            if (cmp == 0)
            {
                if (winner != null) return;
                winner = delta.getKey();
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
    public RealHistogram<PlayerHandle> approximateShowdown(
            HandHistory history)
    {
        return approximate(extractShowdownChoices(history, null));
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
//            if (handStrength == null   ||
//                handStrength.isEmpty() ||
//                handStrength.total() == 0) return null;
            
            hands.put(c.getKey(), handStrength);

            sample = Math.min(sample, handStrength.sampleSize());
        }

        RealHistogram<PlayerHandle> approx = doApproximate( hands );
        approx.setSampleSize(sample);
        return approx;
    }

    private RealHistogram<PlayerHandle> doApproximate(
                Map<PlayerHandle, RealHistogram<HandStrength>> hands)
    {
        return (hands.size() == 2)
                ? approxTwo(hands)
                : approxN(hands);
    }
    private RealHistogram<PlayerHandle>
            approxTwo(Map<PlayerHandle, RealHistogram<HandStrength>> hands)
    {
        Iterator<PlayerHandle>      players = hands.keySet().iterator();
        PlayerHandle                playerA = players.next();
        RealHistogram<HandStrength> handA   = hands.get( playerA );
        PlayerHandle                playerB = players.next();
        RealHistogram<HandStrength> handB   = hands.get( playerB );

        double aWin = winProbability(handA, handB);
        
        RealHistogram<PlayerHandle> approx =
                new RealHistogram<PlayerHandle>();
        approx.add(playerA, aWin);
        approx.add(playerB, 1.0 - aWin);
        return approx;
    }
    private RealHistogram<PlayerHandle>
            approxN(Map<PlayerHandle, RealHistogram<HandStrength>> hands)
    {
        RealHistogram<PlayerHandle> approx  =
                new RealHistogram<PlayerHandle>();
        List<PlayerHandle>          inOrder =
                new ArrayList<PlayerHandle>( hands.keySet() );
        for (int i = 0; i < inOrder.size(); i++)
        {
            double                      win     = 1.0;
            PlayerHandle                playerA = inOrder.get(i);
            RealHistogram<HandStrength> handA   = hands.get( playerA );

            for (int j = 0; j < inOrder.size(); j++)
            {
                if (i == j) continue;

                PlayerHandle playerB = inOrder.get( j );
                win = Math.min(win,
                               winProbability(
                                       handA, hands.get( playerB )));
            }
            approx.add(playerA, win);
        }
        return approx;
    }

    private double winProbability(
            RealHistogram<HandStrength> handA,
            RealHistogram<HandStrength> vsHandB)
    {
//        boolean aPriori = handA.isEmpty();
//        if (vsHandB.isEmpty())
//        {
//            return aPriori
//                   ? 0.5
//                   : (1.0 - notLosingProbability(vsHandB, handA));
//        }

        double tieProb = 0;
        double winProb = 0;
        for (int i = HandStrength.values().length - 1; i >=0; i--)
        {
            HandStrength hs    = HandStrength.values()[ i ];
            double       probA = handA.probabilityOf(hs);

            tieProb += probA * vsHandB.probabilityOf(hs);

            double probBltA = 0; // P(B lessThan A)
            for (int j = 0; j < i; j++)
            {
                probBltA +=
                    vsHandB.probabilityOf(HandStrength.values()[ j ]);
            }
            winProb += (probA * probBltA);
        }

        return winProb + tieProb/2;
    }


    //--------------------------------------------------------------------
    public void learn(HandHistory history)
    {
        learn(history, null);
    }
    public void learn(HandHistory history, PlayerHandle onlyFor)
    {
        for (Map.Entry<PlayerHandle, List<Choice>> choice :
                extractChoices(history, onlyFor, true).entrySet())
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

    //history.holesVisible( choice. )
    public Map<PlayerHandle, List<Choice>>
            extractShowdownChoices(
                HandHistory history, PlayerHandle onlyFor)
    {
        return extractChoices(history, onlyFor, true);
    }
    public Map<PlayerHandle, List<Choice>>
            extractChoices(HandHistory  history,
                           PlayerHandle onlyFor,
                           boolean      atShowdown)
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

        new Dealer(start, brains).playOutHand( false );

        Map<PlayerHandle, List<Choice>> choices =
                new HashMap<PlayerHandle, List<Choice>>();
        for (Map.Entry<PlayerHandle, ContextPlayer> playerEntry :
                brains.entrySet())
        {
            PlayerHandle  player    = playerEntry.getKey();
            ContextPlayer ctxPlayer = playerEntry.getValue();
            if ( !ctxPlayer.finishedUnfolded()                  ||
                 (atShowdown && !history.holesVisible( player ) ||
                 (onlyFor != null && !player.equals( onlyFor ))))
            {
                continue;
            }

            List<Choice>   s   = new ArrayList<Choice>();
            int            i   = 0;
            List<Context>  ctx = ctxPlayer.contexts();
            for (Event e : history.getEvents( player ))
            {
                if (e.getAction().isBlind()) continue;

                s.add(new Choice(
                            actPredictor.predict(
                                        player, ctx.get(i)),
                            e.getAction().toSimpleAction(),
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
            approximate( List<Choice> choices )
    {
        return (RealHistogram<HandStrength>)
                deltas.classify( contextFor(choices) )
                        .toRealHistogram();
    }


    //--------------------------------------------------------------------
    private Context contextFor(List<Choice> choices)
    {
        int          count     = 0;
        BettingRound prevRound = null;

        Context ctx = new ContextImpl();
        for (Choice s : choices)
        {
            BettingRound round = s.round();
            if (prevRound != round)
            {
                count = 0;
            }

            ctx.add(new Datum(dataNameCache[ round.ordinal() ][ count ],
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

