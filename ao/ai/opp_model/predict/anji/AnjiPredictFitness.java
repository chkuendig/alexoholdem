package ao.ai.opp_model.predict.anji;

import ao.ai.opp_model.mix.MixedAction;
import ao.ai.opp_model.predict.PredictionCase;
import ao.ai.opp_model.predict.PredictionSet;
import com.anji.integration.Activator;
import com.anji.integration.ActivatorTranscriber;
import com.anji.integration.TranscriberException;
import com.anji.util.Configurable;
import com.anji.util.Properties;
import org.jgap.BulkFitnessFunction;
import org.jgap.Chromosome;

import java.util.List;

/**
 * 
 */
public class AnjiPredictFitness
        implements BulkFitnessFunction,
                   Configurable
{
    //-------------------------------------------------------------------
//    private final static Logger log = Logger.getLogger(Snapshot.class);

    private static final int ACTION_WEIGHT = 1000;
//    private static int count = 0;


    //--------------------------------------------------------------------
    private final PredictionSet predictions;

    private ActivatorTranscriber factory;


    //--------------------------------------------------------------------
    public AnjiPredictFitness(PredictionSet predictions)
    {
        this.predictions = predictions;
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
        for (PredictionCase predictionCase : predictions.cases())
        {
            fitness += accuracyOf( activator, predictionCase );
        }
        c.setFitnessValue( fitness );
    }

    private int accuracyOf(Activator activator, PredictionCase c)
    {
        MixedAction prediction =
                new MixedAction(activator.next( c.asNeuralInput() ));

//        return (int) Math.round(
//                prediction.probabilityOf( c.outputAction() )
//                    * ACTION_WEIGHT);
        return prediction.mostProbable() == c.outputAction()
                ? ACTION_WEIGHT : 0;
    }


    //--------------------------------------------------------------------
    public int getMaxFitnessValue()
    {
        return predictions.cases().size() * ACTION_WEIGHT;
    }

    public void init(Properties props) throws Exception
    {
//        factory = (ActivatorTranscriber) props.singletonObjectProperty(
//                        ActivatorTranscriber.class );
        factory = new ActivatorTranscriber();
        factory.init( props );
    }
}
