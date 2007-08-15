package ao.holdem.bots.opp_model.predict.joone;

import ao.holdem.bots.opp_model.predict.def.context.PredictionContext;
import ao.holdem.bots.opp_model.predict.def.learn.Predictor;
import ao.holdem.bots.opp_model.predict.def.learn.SupervisedLearner;
import ao.holdem.bots.opp_model.predict.def.retro.Retrodiction;
import org.joone.engine.*;
import org.joone.engine.learning.TeachingSynapse;
import org.joone.io.MemoryInputSynapse;
import org.joone.net.NeuralNet;

import java.util.LinkedList;
import java.util.List;

/**
 *
 */
public class BackpropLearner<C extends PredictionContext>
        implements SupervisedLearner<C>,
                   NeuralNetListener
{
    //--------------------------------------------------------------------
    private final LinkedList<Retrodiction<C>> DATA;
    private final int                         HORIZON;

    private MemoryInputSynapse in;
    private MemoryInputSynapse out;
    private NeuralNet          nnet;
    private Monitor            monitor;
    private int                updatedDataCount;


    //--------------------------------------------------------------------
    public BackpropLearner()
    {
        this(1000);
    }
    public BackpropLearner(int horizon)
    {
        DATA    = new LinkedList<Retrodiction<C>>();
        HORIZON = horizon;
    }


    //--------------------------------------------------------------------
    private void assembleNet()
    {
        Retrodiction<C> first = DATA.getFirst();

        LinearLayer  input  = new LinearLayer();
        SigmoidLayer hidden = new SigmoidLayer();
        SigmoidLayer output = new SigmoidLayer(); // SoftmaxLayer

        input.setLayerName("input");
        hidden.setLayerName("hidden");
        output.setLayerName("output");

        input.setRows(first.neuralInputSize());
        hidden.setRows(48);
        output.setRows(first.neuralOutputSize());

        FullSynapse synapse_IH = new FullSynapse(); /* Input  -> Hidden conn. */
	    FullSynapse synapse_HO = new FullSynapse(); /* Hidden -> Output conn. */
        synapse_IH.setName("IH");
        synapse_HO.setName("HO");

        input.addOutputSynapse(synapse_IH);
	    hidden.addInputSynapse(synapse_IH);

        hidden.addOutputSynapse(synapse_HO);
	    output.addInputSynapse(synapse_HO);

        //------------------------------------------------------
        in  = new MemoryInputSynapse();
        out = new MemoryInputSynapse();

        in.setAdvancedColumnSelector("1-" + first.neuralInputSize());
        out.setAdvancedColumnSelector("1-" + first.neuralOutputSize());

        TeachingSynapse trainer = new TeachingSynapse();
        trainer.setDesired( out );

        input.addInputSynapse(in);
        output.addOutputSynapse( trainer );

        updateData();


        //------------------------------------------------------
        nnet = new NeuralNet();

	    nnet.addLayer(input,  NeuralNet.INPUT_LAYER);
	    nnet.addLayer(hidden, NeuralNet.HIDDEN_LAYER);
	    nnet.addLayer(output, NeuralNet.OUTPUT_LAYER);
        nnet.setTeacher( trainer );

        monitor = nnet.getMonitor();
	    monitor.setLearningRate(0.4);
	    monitor.setMomentum(0.5);

        monitor.addNeuralNetListener(this);
    }

    private void updateData()
    {
        if (DATA.isEmpty()) return;

        double inputCases[][]  =
                new double[ DATA.size() ]
                          [ DATA.getFirst().neuralInputSize() ];

        double outputCases[][] =
                new double[ DATA.size() ]
                          [ DATA.getFirst().neuralOutputSize() ];

        int index = 0;
        for (Retrodiction c : DATA)
        {
            inputCases[  index ] = c.neuralInput();
            outputCases[ index ] = c.neuralOutput();
            index++;
        }

        in.setInputArray( inputCases );
        out.setInputArray(outputCases);

        updatedDataCount = inputCases.length;
    }


    //--------------------------------------------------------------------
    public synchronized void add(List<Retrodiction<C>> data)
    {
        boolean assembel = (DATA.isEmpty());

        DATA.addAll( data );
        while (DATA.size() > HORIZON)
        {
            DATA.removeFirst();
        }

        if (assembel)
        {
            assembleNet();
        }
        updateData();
    }

    //--------------------------------------------------------------------
    public synchronized void learn(int iterations, int timeoutMillis)
    {
        nnet.stop();
        monitor.setTrainingPatterns( updatedDataCount );
        monitor.setTotCicles( iterations );
        monitor.setLearning(  true      );
        nnet.go();
    }


    //--------------------------------------------------------------------
    public synchronized Predictor<C> predictorInstance()
    {
        return null;
    }


    //--------------------------------------------------------------------
    public void netStarted(NeuralNetEvent event) {}
    public void cicleTerminated(NeuralNetEvent event) {}
    public void netStopped(NeuralNetEvent event) {}
    public void errorChanged(NeuralNetEvent event) {}
    public void netStoppedError(NeuralNetEvent event, String s) {}
}
