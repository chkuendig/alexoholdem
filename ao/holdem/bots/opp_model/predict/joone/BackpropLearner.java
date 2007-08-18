package ao.holdem.bots.opp_model.predict.joone;

import ao.holdem.bots.opp_model.predict.def.context.PredictionContext;
import ao.holdem.bots.opp_model.predict.def.learn.Predictor;
import ao.holdem.bots.opp_model.predict.def.learn.RandomPredictor;
import ao.holdem.bots.opp_model.predict.def.learn.SupervisedLearner;
import ao.holdem.bots.opp_model.predict.def.retro.RetroSet;
import ao.util.rand.Rand;
import org.joone.engine.*;
import org.joone.engine.learning.TeachingSynapse;
import org.joone.io.MemoryInputSynapse;
import org.joone.net.NeuralNet;

import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 *
 */
//@Entity
public class BackpropLearner<C extends PredictionContext>
        implements SupervisedLearner<C>,
                   NeuralNetListener
{
    //--------------------------------------------------------------------
    private static final int HORIZON = 200;


    //--------------------------------------------------------------------
    private RetroSet<C> DATA;
    private NeuralNet   nnet;
    private boolean     dataAdded;

    private double inputCases[][];
    private double outputCases[][];


    //--------------------------------------------------------------------
    public BackpropLearner()
    {
        DATA = new RetroSet<C>();
    }


    //--------------------------------------------------------------------
    public synchronized void add(RetroSet<C> data)
    {
        if (data.isEmpty()) return;
        if (nnet == null) initNet(data);

        DATA.add(       data    );
        DATA.removeOld( HORIZON );

        dataAdded = true;
    }


    //--------------------------------------------------------------------
    public synchronized void learn(int iterations, int timeoutMillis)
    {
        if (nnet == null || (!dataAdded)) return;

        nnet.stop();
        updateTrainingData(DATA);

        int realItr = (int)(((double)iterations) * DATA.size() / HORIZON + 1);
        nnet.getMonitor().setTotCicles( realItr );
        try
        {
            Executors.newSingleThreadExecutor().submit(new Runnable() {
                public void run() {
                    nnet.go(true, true);
                }
            }).get(timeoutMillis, TimeUnit.MILLISECONDS);
        }
        catch (TimeoutException e)
        {
            System.out.println("cutting short due to timeout.");
//            nnet.terminate(true);
            nnet.stop();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        dataAdded = false;
    }


    //--------------------------------------------------------------------
    private void updateTrainingData(RetroSet<C> data)
    {
        if (data.isEmpty()) return;
        if (nnet == null) initNet(data);

        feedInput(data);
        feedOutput(data);
        padIoCases(inputCases, outputCases, data.size());

//        System.out.println("setting " + data.size() + " patterns.");
//        nnet.getMonitor().setTrainingPatterns( HORIZON );
    }


    //--------------------------------------------------------------------
    private void feedInput(RetroSet<C> data)
    {
        if (inputCases == null)
        {
            inputCases =
                new double[ HORIZON              ]
                          [ data.caseInputSize() ];

            MemoryInputSynapse in = new MemoryInputSynapse();
            in.setAdvancedColumnSelector("1-" + data.caseInputSize());
            nnet.getInputLayer().addInputSynapse(in);
            in.setInputArray( inputCases );
        }

        for (int i = 0; i < data.size(); i++)
        {
            inputCases[ i ] = data.get(i).neuralInput();
        }
    }
    private void feedOutput(RetroSet<C> data)
    {
        if (outputCases == null)
        {
            outputCases =
                new double[ HORIZON               ]
                          [ data.caseOutputSize() ];

            MemoryInputSynapse out = new MemoryInputSynapse();
            out.setAdvancedColumnSelector("1-" + data.caseOutputSize());

            TeachingSynapse teacher = new TeachingSynapse();
            teacher.setDesired( out );
            nnet.setTeacher( teacher );
            nnet.getOutputLayer().addOutputSynapse( teacher );
            out.setInputArray(outputCases);
        }

        for (int i = 0; i < data.size(); i++)
        {
            outputCases[ i ] = data.get(i).neuralOutput();
        }
    }
    private void padIoCases(
            double in[][],
            double out[][],
            int    startingAt)
    {
        for (int index = startingAt; index < HORIZON; index++)
        {
            int    padIndex = Rand.nextInt(startingAt);
            double inPad[]  = in[padIndex];
            double outPad[] = out[padIndex];
            
            in[index]  = inPad;
            out[index] = outPad;
        }
    }


    //--------------------------------------------------------------------
    private void initNet(RetroSet<C> data)
    {
        Layer input  = inputLayer(data.caseInputSize());
        Layer hidden = hiddenLayer(48);
        Layer output = outputLayer(data.caseOutputSize());

        connectLayers(input, hidden, output);

        nnet = setupNet(input, hidden, output);
        setupMonitor(nnet.getMonitor());
    }


    //--------------------------------------------------------------------
    private void setupMonitor(Monitor monitor)
    {
	    monitor.setLearningRate(0.4);
	    monitor.setMomentum(0.5);
        monitor.addNeuralNetListener(this);

        monitor.setLearning(true);
        nnet.getMonitor().setTrainingPatterns( HORIZON );
    }

    private NeuralNet setupNet(
            Layer input, Layer hidden, Layer output)
    {
        NeuralNet nnet = new NeuralNet();

	    nnet.addLayer(input,  NeuralNet.INPUT_LAYER);
	    nnet.addLayer(hidden, NeuralNet.HIDDEN_LAYER);
	    nnet.addLayer(output, NeuralNet.OUTPUT_LAYER);

        return nnet;
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
    public synchronized Predictor<C> predictor()
    {
        return (nnet == null || DATA.isEmpty())
                ? new RandomPredictor<C>()
                : new BackpropPredictor<C>(nnet);
//        return new RandomPredictor<C>();
    }


    //--------------------------------------------------------------------
    public void netStarted(NeuralNetEvent event)
    {
//        System.out.println("Training started");
    }
    public void cicleTerminated(NeuralNetEvent event)
    {
//        Monitor mon = (Monitor)event.getSource();
//        long c = mon.getCurrentCicle();
//
//        if (c % 20 == 0)
//        {
//            System.out.println(c + " epochs remaining - RMSE = " +
//                                    mon.getGlobalError());
//        }
    }
    public void netStopped(NeuralNetEvent event)
    {
//        System.out.println("Training finished");
    }
    public void errorChanged(NeuralNetEvent event) {}
    public void netStoppedError(NeuralNetEvent event, String s)
    {
        System.out.println("net stopped: " + s);
    }
}
