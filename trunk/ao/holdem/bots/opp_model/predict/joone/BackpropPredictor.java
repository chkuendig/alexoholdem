package ao.holdem.bots.opp_model.predict.joone;

import ao.holdem.bots.opp_model.predict.def.context.PredictionContext;
import ao.holdem.bots.opp_model.predict.def.learn.Predictor;
import ao.holdem.bots.opp_model.predict.def.observation.HoldemObservation;
import ao.holdem.bots.opp_model.mix.MixedAction;
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
    private final NeuralNet     predictor;
    private final DirectSynapse memInp;
    private final DirectSynapse memOut; 


    //--------------------------------------------------------------------
    public BackpropPredictor(NeuralNet copyNet)
    {
        predictor = copyNet.cloneNet();
        //predictor.removeAllListeners();

        Layer input = predictor.getInputLayer();
        input.removeAllInputs();

        Layer output = predictor.getOutputLayer();
        output.removeAllOutputs();

        memOut = new DirectSynapse();
        output.addOutputSynapse(memOut);
        
        memInp = new DirectSynapse();
        input.addInputSynapse(memInp);

        predictor.getMonitor().setLearning( false );
    }


    //--------------------------------------------------------------------
    public HoldemObservation predict(C context)
    {
        predictor.go();

        Pattern iPattern =
                new Pattern(context.neuralInput());
        iPattern.setCount(1);

        memInp.fwdPut(iPattern);
//        predictor.singleStepForward(iPattern);

        Pattern pattern = memOut.fwdGet();
        HoldemObservation prediction =
                new HoldemObservation(
                        new MixedAction(
                                pattern.getArray()));
        predictor.stop();
        return prediction;
    }
}
