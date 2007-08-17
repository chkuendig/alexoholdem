package ao.holdem.bots.opp_model.predict.joone;

import ao.holdem.bots.opp_model.predict.def.context.PredictionContext;
import ao.holdem.bots.opp_model.predict.def.learn.Predictor;
import ao.holdem.bots.opp_model.predict.def.learn.RandomPredictor;
import ao.holdem.bots.opp_model.predict.def.learn.SupervisedLearner;
import ao.holdem.bots.opp_model.predict.def.retro.RetroSet;
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
    private RetroSet<C> DATA;
    private NeuralNet   nnet;
    private boolean     dataAdded;


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

        DATA.add(       data );
        DATA.removeOld( 1000 );

        dataAdded = true;
    }


    //--------------------------------------------------------------------
    public synchronized void learn(int iterations, int timeoutMillis)
    {
        if (nnet == null || (!dataAdded)) return;

        updateTrainingData(DATA);

        nnet.getMonitor().setTotCicles( iterations );
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

        nnet.getMonitor().setTrainingPatterns( data.size() );
    }


    //--------------------------------------------------------------------
    private void feedInput(RetroSet<C> data)
    {
        double inputCases[][]  =
                new double[ data.size() ]
                          [ data.caseInputSize() ];
        for (int i = 0; i < data.size(); i++)
        {
            inputCases[ i ] = data.get(i).neuralInput();
        }

        MemoryInputSynapse in;
        if (nnet.getInputLayer().getAllInputs() == null ||
                nnet.getInputLayer().getAllInputs().isEmpty())
        {
            in = new MemoryInputSynapse();
            in.setAdvancedColumnSelector("1-" + data.caseInputSize());

            nnet.getInputLayer().addInputSynapse(in);
        }
        else
        {
            in = (MemoryInputSynapse)
                    nnet.getInputLayer().getAllInputs().get(0);
        }
        in.setInputArray( inputCases );
    }
    private void feedOutput(RetroSet<C> data)
    {
        double outputCases[][] =
                new double[ data.size() ]
                          [ data.caseOutputSize() ];
        for (int i = 0; i < data.size(); i++)
        {
            outputCases[ i ] = data.get(i).neuralOutput();
        }

        MemoryInputSynapse out;
        if (nnet.getOutputLayer().getAllOutputs() == null ||
                nnet.getOutputLayer().getAllOutputs().isEmpty())
        {
            out = new MemoryInputSynapse();
            out.setAdvancedColumnSelector("1-" + data.caseOutputSize());

            TeachingSynapse teacher = new TeachingSynapse();
            teacher.setDesired( out );
            nnet.setTeacher( teacher );

            nnet.getOutputLayer().addOutputSynapse( teacher );
        }
        else
        {
            out = (MemoryInputSynapse)((TeachingSynapse)
                    nnet.getOutputLayer().getAllOutputs().get(0))
                        .getDesired();
        }
        out.setInputArray(outputCases);
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
//        return (nnet == null)
//                ? new RandomPredictor<C>()
//                : new BackpropPredictor<C>(nnet);
        return new RandomPredictor<C>();
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
    public void netStoppedError(NeuralNetEvent event, String s)
    {
        System.out.println("net stopped: " + s);
    }
}
