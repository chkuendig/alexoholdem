package ao.holdem.bots.opp_model.predict;

import org.joone.engine.*;
import org.joone.engine.learning.TeachingSynapse;
import org.joone.io.FileInputSynapse;
import org.joone.io.FileOutputSynapse;
import org.joone.io.MemoryInputSynapse;
import org.joone.net.NeuralNet;

import java.io.File;

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

        input.setRows(19);
        hidden.setRows(4);
        output.setRows(3);

        FullSynapse synapse_IH = new FullSynapse(); /* Input  -> Hidden conn. */
	    FullSynapse synapse_HO = new FullSynapse(); /* Hidden -> Output conn. */
        synapse_IH.setName("IH");
        synapse_HO.setName("HO");
        
        input.addOutputSynapse(synapse_IH);
	    hidden.addInputSynapse(synapse_IH);

        hidden.addOutputSynapse(synapse_HO);
	    output.addInputSynapse(synapse_HO);

        //------------------------------------------------------
        double inputCases[][]  = new double[ predictions.cases().size() ][ 19 ];
        double outputCases[][] = new double[ predictions.cases().size() ][ 3  ];
        for (int i = 0; i < predictions.cases().size(); i++)
        {
            PredictionCase c = predictions.cases().get(i);
            inputCases[  i ] = c.asNeuralInput();
            outputCases[ i ] = c.neuralOutput();
        }

        MemoryInputSynapse in = new MemoryInputSynapse();
        in.setInputArray( inputCases );
        in.setAdvancedColumnSelector("1-19");
        input.addInputSynapse(in);

        TeachingSynapse trainer = new TeachingSynapse();
        MemoryInputSynapse out  = new MemoryInputSynapse();
        out.setAdvancedColumnSelector("1-3");
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

    public void trainXor()
    {
        LinearLayer input = new LinearLayer();
        SigmoidLayer hidden = new SigmoidLayer();
        SigmoidLayer output = new SigmoidLayer();
        input.setLayerName("input");
        hidden.setLayerName("hidden");
        output.setLayerName("output");
        /* sets their dimensions */
        input.setRows(2);
        hidden.setRows(3);
        output.setRows(1);

        /*
         * Now create the two Synapses
         */
        FullSynapse synapse_IH = new FullSynapse(); /* input -> hidden conn. */
        FullSynapse synapse_HO = new FullSynapse(); /* hidden -> output conn. */

        synapse_IH.setName("IH");
        synapse_HO.setName("HO");
        /*
         * Connect the input layer whit the hidden layer
         */
        input.addOutputSynapse(synapse_IH);
        hidden.addInputSynapse(synapse_IH);
        /*
         * Connect the hidden layer whit the output layer
         */
        hidden.addOutputSynapse(synapse_HO);
        output.addInputSynapse(synapse_HO);

        //------------------------------------------------------
        MemoryInputSynapse in = new MemoryInputSynapse();
        in.setInputArray( new double[][]{
                {0,0}, {0,1}, {1,0}, {1,1} } );
        in.setAdvancedColumnSelector("1,2");

//        FileInputSynapse inputStream = new FileInputSynapse();
//        inputStream.setAdvancedColumnSelector("1,2");
//        inputStream.setInputFile(new File("C:\\alex\\dev\\workspace_idea\\holdem\\xor.txt"));
//        input.addInputSynapse(inputStream);
        input.addInputSynapse(in);


        //------------------------------------------------------
        TeachingSynapse trainer = new TeachingSynapse();

        FileInputSynapse samples = new FileInputSynapse();
        samples.setInputFile(new File("C:\\alex\\dev\\workspace_idea\\holdem\\xor.txt"));
        samples.setAdvancedColumnSelector("3");
        trainer.setDesired(samples);

        /* Creates the error output file */
        FileOutputSynapse error = new FileOutputSynapse();
        error.setFileName("C:\\alex\\dev\\workspace_idea\\holdem\\xor_errors.txt");
        //error.setBuffered(false);
        trainer.addResultSynapse(error);

        /* Connects the Teacher to the last layer of the net */
        output.addOutputSynapse(trainer);
        NeuralNet nnet = new NeuralNet();
        nnet.addLayer(input, NeuralNet.INPUT_LAYER);
        nnet.addLayer(hidden, NeuralNet.HIDDEN_LAYER);
        nnet.addLayer(output, NeuralNet.OUTPUT_LAYER);
        nnet.setTeacher(trainer);
        // Gets the Monitor object and set the learning parameters
        Monitor monitor = nnet.getMonitor();
        monitor.setLearningRate(0.8);
        monitor.setMomentum(0.3);

        /* The application registers itself as monitor's listener
         * so it can receive the notifications of termination from
         * the net.
         */
        monitor.addNeuralNetListener(this);

        monitor.setTrainingPatterns(4); /* # of rows (patterns) contained in the input file */
        monitor.setTotCicles(10000); /* How many times the net must be trained on the input patterns */
        monitor.setLearning(true); /* The net must be trained */
        nnet.go(); /* The net starts the training job */
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
//        ((TeachingSynapse) event.getNeuralNet().getTeacher()).
    }

    public void netStoppedError(NeuralNetEvent event, String s)
    {

    }
}
