package ao.holdem.bots.opp_model.predict;

import ao.holdem.bots.opp_model.mix.MixedAction;
import ao.holdem.def.model.cards.Community;
import ao.holdem.def.state.domain.BettingRound;
import ao.holdem.def.state.env.TakenAction;
import ao.holdem.history.Event;
import ao.holdem.history.HandHistory;
import ao.holdem.history.PlayerHandle;
import ao.holdem.history.Snapshot;
import com.anji.integration.Activator;
import com.anji.integration.ActivatorTranscriber;
import com.anji.integration.TranscriberException;
import com.anji.util.Configurable;
import com.anji.util.Properties;
import org.apache.log4j.Logger;
import org.jgap.BulkFitnessFunction;
import org.jgap.Chromosome;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 */
public class AnjiPredictFitness
        implements BulkFitnessFunction,
                   Configurable
{
    //-------------------------------------------------------------------
    private final static Logger log = Logger.getLogger(Snapshot.class);

    private static final int ACTION_WEIGHT = 1000;
//    private static int count = 0;


    //--------------------------------------------------------------------
    private final PlayerHandle         player;
    private final List<PredictionCase> cases;

    private ActivatorTranscriber factory;


    //--------------------------------------------------------------------
    public AnjiPredictFitness(PlayerHandle forPlayer)
    {
        player = forPlayer;
        cases  = new ArrayList<PredictionCase>();
    }

    public void addHand(HandHistory hand)
    {
//        System.out.println(++count);
        cases.addAll( casesFor(hand) );
    }

    private List<PredictionCase> casesFor(HandHistory hand)
    {
        List<PredictionCase> handCases = new ArrayList<PredictionCase>();

        Snapshot prev = null;
        TakenAction prevAct = null;

        Snapshot cursor = hand.snapshot();
        for (Event e : hand.getEvents())
        {
            if (e.getPlayer().equals( player ))
            {
                assert cursor.nextToActLookahead().equals( player );
                Snapshot curr = cursor.prototype();

                if (prev != null &&
                        e.getRound() != BettingRound.PREFLOP)
                {
                    TakenAction currAct = e.getAction();
                    Community community = hand.getCommunity().asOf( e.getRound() );

                    handCases.add( new PredictionCase(prev, prevAct,
                                                      curr, currAct,
                                                      community) );
                }

                prev    = curr;
                prevAct = e.getAction();
            }

            try
            {
                cursor.addNextEvent( e );
            }
            catch (Error ex)
            {
                log.warn(ex);
                return new ArrayList<PredictionCase>();
            }
        }

        return handCases;
    }


    //--------------------------------------------------------------------
    public void evaluate(List genotypes)
    {
        for (Object genotype : genotypes)
        {
            Chromosome c = (Chromosome) genotype;
            evaluate(c);
        }
    }

    private void evaluate(Chromosome c)
    {
        Activator activator;
        try
        {
            activator = factory.newActivator( c );
        }
        catch (TranscriberException e)
        {
            throw new Error(e);
        }

        int fitness = 0;
        for (PredictionCase predictionCase : cases)
        {
            fitness += accuracyOf( activator, predictionCase );
        }
        c.setFitnessValue( fitness );
    }

    private int accuracyOf(Activator activator, PredictionCase c)
    {
        MixedAction prediction =
                new MixedAction(activator.next( c.asNeatInput() ));

//        return (int) Math.round(
//                prediction.probabilityOf( c.outputAction() )
//                    * ACTION_WEIGHT);
        return prediction.mostProbable() == c.outputAction()
                ? ACTION_WEIGHT : 0;
    }


    //--------------------------------------------------------------------
    public int getMaxFitnessValue()
    {
        return cases.size() * ACTION_WEIGHT;
    }

    public void init(Properties props) throws Exception
    {
//        factory = (ActivatorTranscriber) props.singletonObjectProperty(
//                        ActivatorTranscriber.class );
        factory = new ActivatorTranscriber();
        factory.init( props );
    }
}
