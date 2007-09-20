package ao.ai.opp_model.neural.joone;

import ao.ai.opp_model.mix.MixedAction;
import ao.ai.opp_model.neural.def.context.PredictionContext;
import ao.ai.opp_model.neural.def.learn.Predictor;
import ao.ai.opp_model.neural.def.learn.SupervisedLearner;
import ao.ai.opp_model.neural.def.observation.BooleanObservation;
import ao.ai.opp_model.neural.def.observation.Observation;
import ao.ai.opp_model.neural.def.observation.HoldemObservation;
import ao.ai.opp_model.neural.def.retro.RetroSet;
import ao.ai.opp_model.neural.def.retro.Retrodiction;
import ao.holdem.model.act.SimpleAction;

/**
 *
 */
public class HoldemLearner<C extends PredictionContext>
        implements SupervisedLearner<C>
{
    //--------------------------------------------------------------------
    private SupervisedLearner<C> foldLearner;
    private SupervisedLearner<C> callLearner;
    private SupervisedLearner<C> raiseLearner;


    //--------------------------------------------------------------------
    public HoldemLearner()
    {
        foldLearner  = new BackpropLearner<C>(1);
        callLearner  = new BackpropLearner<C>(1);
        raiseLearner = new BackpropLearner<C>(1);
    }


    //--------------------------------------------------------------------
    public void add(RetroSet<C> data)
    {
        RetroSet<C> foldData  = new RetroSet<C>();
        RetroSet<C> callData  = new RetroSet<C>();
        RetroSet<C> raiseData = new RetroSet<C>();

        for (Retrodiction<C> retrodiction : data.cases())
        {
            foldData.add(retrodiction.asObserved(
                    isAct(retrodiction, SimpleAction.FOLD)));
            callData.add(retrodiction.asObserved(
                    isAct(retrodiction, SimpleAction.CALL)));
            raiseData.add(retrodiction.asObserved(
                    isAct(retrodiction, SimpleAction.RAISE)));
        }

        foldLearner.add(foldData);
        callLearner.add(callData);
        raiseLearner.add(raiseData);
    }

    private BooleanObservation isAct(
            Retrodiction<C> of,
            SimpleAction equalTo)
    {
        return new BooleanObservation(
                new MixedAction(of.neuralOutput())
                        .mostProbable() == equalTo);
    }


    //--------------------------------------------------------------------
    public void learn(int iterations, int timeoutMillis)
    {
        foldLearner.learn(iterations,  timeoutMillis/3 );
        callLearner.learn(iterations,  timeoutMillis/3 );
        raiseLearner.learn(iterations,  timeoutMillis/3 );
    }


    //--------------------------------------------------------------------
    public Predictor<C> predictor()
    {
        final Predictor<C> foldPredictor  = foldLearner.predictor();
        final Predictor<C> callPredictor  = callLearner.predictor();
        final Predictor<C> raisePredictor = raiseLearner.predictor();

        return new Predictor<C>() {
            public Observation predict(C context) {
                MixedAction prediction =
                    new MixedAction(
                        foldPredictor.predict(context).neuralOutput()[0],
                        callPredictor.predict(context).neuralOutput()[0],
                        raisePredictor.predict(context).neuralOutput()[0]);
                return new HoldemObservation(prediction);
            }
        };
    }
}
