package ao.holdem.bots.opp_model.predict;

import ao.holdem.history.HandHistory;
import ao.holdem.history.PlayerHandle;
import com.anji.integration.Activator;
import com.anji.integration.ActivatorTranscriber;
import com.anji.integration.TranscriberException;
import com.anji.util.Configurable;
import com.anji.util.Properties;
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
    //--------------------------------------------------------------------
    private final PlayerHandle      player;
    private final List<HandHistory> hands;

    private ActivatorTranscriber factory;


    //--------------------------------------------------------------------
    public AnjiPredictFitness(PlayerHandle forPlayer)
    {
        player = forPlayer;
        hands  = new ArrayList<HandHistory>();
    }

    public void addHand(HandHistory hand)
    {
        hands.add( hand );
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
        Activator activator = null;
        try
        {
            activator = factory.newActivator( c );
        }
        catch (TranscriberException e)
        {
            e.printStackTrace();
        }

        // calculate fitness, sum of multiple trials

//        int fitness = 0;
//        for ( int i = 0; i < numTrials; i++ )
//            fitness += singleTrial( activator );
//        c.setFitnessValue( fitness );
    }


    //--------------------------------------------------------------------
    public int getMaxFitnessValue()
    {
        return 0;
    }

    public void init(Properties props) throws Exception
    {
        factory = (ActivatorTranscriber) props.singletonObjectProperty(
                        ActivatorTranscriber.class );

        
    }
}
