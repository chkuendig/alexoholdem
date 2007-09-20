package ao.ai.opp_model.neural;

import org.joone.engine.*;
import org.joone.engine.learning.TeachingSynapse;
import org.joone.io.MemoryInputSynapse;
import org.joone.net.NeuralNet;

import java.util.List;

/**
 *
 */
public class BackpropPredictor implements NeuralNetListener
{
    //--------------------------------------------------------------------
    public BackpropPredictor()
    {

    }


    //--------------------------------------------------------------------
    public void trainOn(PredictionSet predictions)
    {
        Layer input  = inputLayer(predictions.caseInputSize());
        Layer hidden = hiddenLayer(48);
        Layer output = outputLayer(predictions.caseOutputSize());

        connectLayers(input, hidden, output);

        feedInput(predictions, input);
        TeachingSynapse teacher =
                feedOutput(predictions, output);

        NeuralNet nnet = setupNet(input, hidden, output, teacher);
        setupMonitor(nnet.getMonitor(), predictions);

        //------------------------------------------------------
        for (int i = 0; i < 20; i++)
        {
            nnet.go(true, true);
        }
    }


    //--------------------------------------------------------------------
    private NeuralNet setupNet(
            Layer input, Layer hidden, Layer output,
            TeachingSynapse teacher)
    {
        NeuralNet nnet = new NeuralNet();

	    nnet.addLayer(input,  NeuralNet.INPUT_LAYER);
	    nnet.addLayer(hidden, NeuralNet.HIDDEN_LAYER);
	    nnet.addLayer(output, NeuralNet.OUTPUT_LAYER);
        nnet.setTeacher( teacher );

        return nnet;
    }

    private void setupMonitor(
            Monitor       monitor,
            PredictionSet predictions)
    {
	    monitor.setLearningRate(0.4);
	    monitor.setMomentum(0.5);

        monitor.removeAllListeners();
        monitor.addNeuralNetListener(this);

        monitor.setTrainingPatterns( predictions.cases().size() );
        monitor.setTotCicles( 200 );
        monitor.setLearning(  true  );
    }


    //--------------------------------------------------------------------
    private void feedInput(PredictionSet predictions, Layer input)
    {
        List<PredictionCase> cases = predictions.cases();
        double inputCases[][]  =
                new double[ predictions.numCases() ]
                          [ predictions.caseInputSize() ];

        for (int i = 0; i < predictions.numCases(); i++)
        {
            inputCases[ i ] = cases.get(i).asNeuralInput();
        }

        MemoryInputSynapse in = new MemoryInputSynapse();
        in.setInputArray( inputCases );
        in.setAdvancedColumnSelector("1-" + predictions.caseInputSize());
        input.addInputSynapse(in);
    }
    private TeachingSynapse feedOutput(
            PredictionSet predictions, Layer output)
    {
        List<PredictionCase> cases = predictions.cases();
        double outputCases[][] =
                new double[ predictions.numCases() ]
                          [ predictions.caseOutputSize() ];

        for (int i = 0; i < predictions.numCases(); i++)
        {
            PredictionCase c = cases.get(i);
            outputCases[ i ] = c.neuralOutput();
        }

        MemoryInputSynapse out  = new MemoryInputSynapse();
        out.setAdvancedColumnSelector("1-" + predictions.caseOutputSize());
        out.setInputArray(outputCases);

        TeachingSynapse trainer = new TeachingSynapse();
        trainer.setDesired( out );
        output.addOutputSynapse( trainer );

        return trainer;
    }


    //--------------------------------------------------------------------
    private void connectLayers(
            Layer input, Layer hidden, Layer output)
    {
        FullSynapse synapse_IH = new FullSynapse();
	    FullSynapse synapse_HO = new FullSynapse();

        synapse_IH.setName("input2hidden");
        synapse_HO.setName("hidden2output");

        input.addOutputSynapse(synapse_IH);
	    hidden.addInputSynapse(synapse_IH);

        hidden.addOutputSynapse(synapse_HO);
	    output.addInputSynapse(synapse_HO);
    }


    //--------------------------------------------------------------------
    private Layer inputLayer( int numRows )
    {
        LinearLayer input = new LinearLayer();
        input.setLayerName("input");
        input.setRows(numRows);
        return input;
    }
    private Layer hiddenLayer( int numRows )
    {
        SigmoidLayer hidden = new SigmoidLayer();
        hidden.setLayerName("hidden");
        hidden.setRows(numRows);
        return hidden;
    }
    private Layer outputLayer( int numRows )
    {
        SigmoidLayer output = new SigmoidLayer(); // SoftmaxLayer
        output.setLayerName("output");
        output.setRows(numRows);
        return output;
    }

    
    //--------------------------------------------------------------------
    public void netStarted(NeuralNetEvent event)
    {
        System.out.println("Training started");
    }

    public void cicleTerminated(NeuralNetEvent event)
    {
        Monitor mon = (Monitor)event.getSource();
        long c = mon.getCurrentCicle();

        if (c % 100 == 0)
        {
            System.out.println(c + " epochs remaining - RMSE = " +
                                    mon.getGlobalError());
        }
    }

    public void netStopped(NeuralNetEvent event)
    {
        System.out.println("Training finished");
    }

    public void errorChanged(NeuralNetEvent event)
    {

    }

    public void netStoppedError(NeuralNetEvent event, String s)
    {

    }
}
