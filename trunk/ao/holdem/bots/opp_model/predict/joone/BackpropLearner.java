package ao.holdem.bots.opp_model.predict.joone;

import ao.holdem.bots.opp_model.predict.def.context.PredictionContext;
import ao.holdem.bots.opp_model.predict.def.learn.Predictor;
import ao.holdem.bots.opp_model.predict.def.learn.RandomPredictor;
import ao.holdem.bots.opp_model.predict.def.learn.SupervisedLearner;
import ao.holdem.bots.opp_model.predict.def.retro.Retrodiction;
import org.joone.engine.*;
import org.joone.engine.learning.TeachingSynapse;
import org.joone.io.MemoryInputSynapse;
import org.joone.net.NeuralNet;

import javax.persistence.Entity;
import java.util.LinkedList;
import java.util.List;

/**
 *
 */
@Entity
public class BackpropLearner<C extends PredictionContext>
        implements SupervisedLearner<C>,
                   NeuralNetListener
{
    //--------------------------------------------------------------------
    private List<Retrodiction<C>> DATA;
//    private int                   HORIZON;

    private transient MemoryInputSynapse in;
    private transient MemoryInputSynapse out;
    private           NeuralNet          nnet;
    private transient Monitor            monitor;
    private transient int                updatedDataCount;
    private transient boolean            dataAdded;


    //--------------------------------------------------------------------
//    public BackpropLearner()
//    {
////        this(1000);
//    }
    public BackpropLearner(/*int horizon*/)
    {
        DATA    = new LinkedList<Retrodiction<C>>();
//        HORIZON = horizon;
    }


    //--------------------------------------------------------------------
    public List<Retrodiction<C>> getData()
    {
        return DATA;
    }
    public void setDATA(List<Retrodiction<C>> data)
    {
        this.DATA = data;
    }

    public NeuralNet getNeuralNet()
    {
        return nnet;
    }
    public void setNeuralNet(NeuralNet net)
    {
        this.nnet = net;
    }


    //--------------------------------------------------------------------
    private void assembleNet()
    {
        Retrodiction<C> first = DATA.get(0);

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

//        nnet.

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
                          [ DATA.get(0).neuralInputSize() ];

        double outputCases[][] =
                new double[ DATA.size() ]
                          [ DATA.get(0).neuralOutputSize() ];

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
        if (data.isEmpty()) return;
        boolean assembel = (DATA.isEmpty());

        DATA.addAll( data );
        while (DATA.size() > 1000)
        {
//            DATA.subList(DATA.size() - 1001, DATA.size() - 1);
            DATA.remove(0);
        }

        if (assembel)
        {
            assembleNet();
        }
        else
        {
            updateData();
        }
        dataAdded = true;
    }


    //--------------------------------------------------------------------
    public synchronized void learn(int iterations, int timeoutMillis)
    {
        if (nnet == null || (!dataAdded)) return;

        nnet.stop();
        monitor.setTrainingPatterns( updatedDataCount );
        monitor.setTotCicles( iterations );
        monitor.setLearning(  true      );
        nnet.go();
        monitor.Go();
        dataAdded = false;
    }


    //--------------------------------------------------------------------
    public synchronized Predictor<C> predictor()
    {
        return (nnet == null)
                ? new RandomPredictor<C>()
                : new BackpropPredictor<C>(nnet);
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
    public void errorChanged(NeuralNetEvent event) {}
    public void netStoppedError(NeuralNetEvent event, String s) {}
}
