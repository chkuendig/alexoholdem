package ao.holdem.bots.opp_model.predict.joone;

import ao.holdem.bots.opp_model.mix.MixedAction;
import ao.holdem.bots.opp_model.predict.def.context.PredictionContext;
import ao.holdem.bots.opp_model.predict.def.learn.Predictor;
import ao.holdem.bots.opp_model.predict.def.learn.SupervisedLearner;
import ao.holdem.bots.opp_model.predict.def.observation.BooleanObservation;
import ao.holdem.bots.opp_model.predict.def.observation.Observation;
import ao.holdem.bots.opp_model.predict.def.observation.HoldemObservation;
import ao.holdem.bots.opp_model.predict.def.retro.RetroSet;
import ao.holdem.bots.opp_model.predict.def.retro.Retrodiction;
import ao.holdem.def.state.env.TakenAction;

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
                    isAct(retrodiction, TakenAction.FOLD)));
            callData.add(retrodiction.asObserved(
                    isAct(retrodiction, TakenAction.CALL)));
            raiseData.add(retrodiction.asObserved(
                    isAct(retrodiction, TakenAction.RAISE)));
        }

        foldLearner.add(foldData);
        callLearner.add(callData);
        raiseLearner.add(raiseData);
    }

    private BooleanObservation isAct(
            Retrodiction<C> of,
            TakenAction     equalTo)
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
