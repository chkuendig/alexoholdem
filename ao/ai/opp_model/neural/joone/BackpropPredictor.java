package ao.ai.opp_model.neural.joone;

import ao.ai.opp_model.neural.def.context.PredictionContext;
import ao.ai.opp_model.neural.def.learn.Predictor;
import ao.ai.opp_model.neural.def.observation.Observation;
import ao.ai.opp_model.neural.def.observation.ObservationImpl;
import org.joone.engine.Layer;
import org.joone.io.MemoryInputSynapse;
import org.joone.io.MemoryOutputSynapse;
import org.joone.net.NeuralNet;

/**
 *
 */
public class BackpropPredictor<C extends PredictionContext>
        implements Predictor<C>
{
    //--------------------------------------------------------------------
    private double input[][];

    private NeuralNet           predictor;
    private MemoryInputSynapse  memInp;
    private MemoryOutputSynapse memOut;


    //--------------------------------------------------------------------
    public BackpropPredictor(NeuralNet copyNet)
    {
        predictor = copyNet.cloneNet();
        predictor.removeAllListeners();
    }


    //--------------------------------------------------------------------
    private void initPredictor(C context)
    {
        input = new double[][]{ context.neuralInput() };

        memInp = new MemoryInputSynapse();
        memInp.setFirstRow(1);
        memInp.setAdvancedColumnSelector(
                "1-" + context.neuralInputSize());
        memInp.setInputArray( input );

        Layer input = predictor.getInputLayer();
        input.removeAllInputs();
        input.addInputSynapse(memInp);

        memOut       = new MemoryOutputSynapse();
        Layer output = predictor.getOutputLayer();
        output.removeAllOutputs();
        output.addOutputSynapse(memOut);

        predictor.getMonitor().setTotCicles( 1 );
        predictor.getMonitor().setTrainingPatterns( 1 );
        predictor.getMonitor().setLearning( false );
    }


    //--------------------------------------------------------------------
    public synchronized Observation predict(C context)
    {
//        if (input == null)
//        {
            initPredictor(context);
//        }
//        else
//        {
//            input[0] = context.neuralInput();
//        }

        predictor.go(true, true);
        Observation prediction =
                new ObservationImpl(memOut.getNextPattern());
        predictor.stop();
        return prediction;
    }
}
