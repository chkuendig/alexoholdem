package ao.holdem.bots.opp_model.predict.joone;

import ao.holdem.bots.opp_model.predict.def.context.PredictionContext;
import ao.holdem.bots.opp_model.predict.def.learn.Predictor;
import ao.holdem.bots.opp_model.predict.def.observation.Observation;
import ao.holdem.bots.opp_model.predict.def.observation.ObservationImpl;
import org.joone.engine.DirectSynapse;
import org.joone.engine.Layer;
import org.joone.engine.Pattern;
import org.joone.net.NeuralNet;

/**
 *
 */
public class BackpropPredictor<C extends PredictionContext>
        implements Predictor<C>
{
    //--------------------------------------------------------------------
    private final DirectSynapse memInp;
    private final DirectSynapse memOut; 


    //--------------------------------------------------------------------
    public BackpropPredictor(NeuralNet copyNet)
    {
        NeuralNet predictor = copyNet.cloneNet();
        predictor.removeAllListeners();

        memInp      = new DirectSynapse();
        Layer input = predictor.getInputLayer();
        input.removeAllInputs();
        input.addInputSynapse(memInp);

        memOut       = new DirectSynapse();
        Layer output = predictor.getOutputLayer();
        output.removeAllOutputs();
        output.addOutputSynapse(memOut);

        predictor.getMonitor().setLearning( false );
        predictor.go();
    }


    //--------------------------------------------------------------------
    public Observation predict(C context)
    {
//        predictor.go();

        Pattern iPattern =
                new Pattern(context.neuralInput());
        iPattern.setCount(1);

        memInp.fwdPut(iPattern);
//        predictor.singleStepForward(iPattern);

        Pattern pattern = memOut.fwdGet();
        //        predictor.stop();
        return new ObservationImpl(pattern.getArray());
    }
}
