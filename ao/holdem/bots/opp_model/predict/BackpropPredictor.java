package ao.holdem.bots.opp_model.predict;

import org.joone.engine.*;
import org.joone.engine.learning.TeachingSynapse;
import org.joone.io.MemoryInputSynapse;
import org.joone.net.NeuralNet;

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
        LinearLayer  input  = new LinearLayer();
        SigmoidLayer hidden = new SigmoidLayer();
        SigmoidLayer output = new SigmoidLayer(); // SoftmaxLayer

        input.setLayerName("input");
        hidden.setLayerName("hidden");
        output.setLayerName("output");

        input.setRows(predictions.caseInputSize());
        hidden.setRows(48);
        output.setRows(predictions.caseOutputSize());

        FullSynapse synapse_IH = new FullSynapse(); /* Input  -> Hidden conn. */
	    FullSynapse synapse_HO = new FullSynapse(); /* Hidden -> Output conn. */
        synapse_IH.setName("IH");
        synapse_HO.setName("HO");
        
        input.addOutputSynapse(synapse_IH);
	    hidden.addInputSynapse(synapse_IH);

        hidden.addOutputSynapse(synapse_HO);
	    output.addInputSynapse(synapse_HO);

        //------------------------------------------------------
        double inputCases[][]  = new double[ predictions.cases().size() ][ predictions.caseInputSize() ];
        double outputCases[][] = new double[ predictions.cases().size() ][ predictions.caseOutputSize() ];
        for (int i = 0; i < predictions.cases().size(); i++)
        {
            PredictionCase c = predictions.cases().get(i);
            inputCases[  i ] = c.asNeuralInput();
            outputCases[ i ] = c.neuralOutput();
        }

        MemoryInputSynapse in = new MemoryInputSynapse();
        in.setInputArray( inputCases );
        in.setAdvancedColumnSelector("1-" + predictions.caseInputSize());
        input.addInputSynapse(in);

        TeachingSynapse trainer = new TeachingSynapse();
        MemoryInputSynapse out  = new MemoryInputSynapse();
        out.setAdvancedColumnSelector("1-" + predictions.caseOutputSize());
        out.setInputArray(outputCases);
        trainer.setDesired( out );

//        FileOutputSynapse error = new FileOutputSynapse();
//        error.setFileName("C:\\alex\\dev\\workspace_idea\\holdem\\joone_errors.txt");
//        //error.setBuffered(false);
//        trainer.addResultSynapse( error );

        output.addOutputSynapse( trainer );
        
        //------------------------------------------------------
        NeuralNet nnet = new NeuralNet();

	    nnet.addLayer(input,  NeuralNet.INPUT_LAYER);
	    nnet.addLayer(hidden, NeuralNet.HIDDEN_LAYER);
	    nnet.addLayer(output, NeuralNet.OUTPUT_LAYER);
        nnet.setTeacher( trainer );

        Monitor monitor = nnet.getMonitor();
	    monitor.setLearningRate(0.4);
	    monitor.setMomentum(0.5);

        monitor.addNeuralNetListener(this);

        monitor.setTrainingPatterns( inputCases.length );
        monitor.setTotCicles( 10000 );
        monitor.setLearning(  true  );
        nnet.go();
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
