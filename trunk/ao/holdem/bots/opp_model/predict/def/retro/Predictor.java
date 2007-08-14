package ao.holdem.bots.opp_model.predict.def.retro;

import ao.holdem.bots.opp_model.predict.def.context.PredictionContext;
import ao.holdem.bots.opp_model.predict.def.context.firstact.HoleAwareFirstact;
import ao.holdem.bots.opp_model.predict.def.context.firstact.HoleBlindFirstact;
import ao.holdem.bots.opp_model.predict.def.context.postflop.HoleAwarePostflop;
import ao.holdem.bots.opp_model.predict.def.context.postflop.HoleBlindPostflop;
import ao.holdem.bots.opp_model.predict.def.context.preflop.HoleAwarePreflop;
import ao.holdem.bots.opp_model.predict.def.context.preflop.HoleBlindPreflop;
import ao.holdem.bots.opp_model.predict.def.learn.SupervisedLearner;
import ao.holdem.bots.opp_model.predict.def.observation.HoldemObservation;
import ao.holdem.bots.opp_model.predict.joone.BackpropLearner;

import java.util.List;

/**
 *
 */
public class Predictor
{
    //--------------------------------------------------------------------
    private SupervisedLearner<HoleAwareFirstact, HoldemObservation>
            holeAwareFirstactLearner;
    private SupervisedLearner<HoleBlindFirstact, HoldemObservation>
            holeBlindFirstactLearner;

    private SupervisedLearner<HoleAwarePreflop, HoldemObservation>
            holeAwarePreflopLearner;
    private SupervisedLearner<HoleBlindPreflop, HoldemObservation>
            holeBlindPreflopLearner;

    private SupervisedLearner<HoleAwarePostflop, HoldemObservation>
            holeAwarePostflopLearner;
    private SupervisedLearner<HoleBlindPostflop, HoldemObservation>
            holeBlindPostflopLearner;


    //--------------------------------------------------------------------
    public Predictor()
    {
        holeAwareFirstactLearner =
                new BackpropLearner<HoleAwareFirstact, HoldemObservation>();
        holeBlindFirstactLearner =
                new BackpropLearner<HoleBlindFirstact, HoldemObservation>();

        holeAwarePreflopLearner =
                new BackpropLearner<HoleAwarePreflop, HoldemObservation>();
        holeBlindPreflopLearner =
                new BackpropLearner<HoleBlindPreflop, HoldemObservation>();

        holeAwarePostflopLearner =
                new BackpropLearner<HoleAwarePostflop, HoldemObservation>();
        holeBlindPostflopLearner =
                new BackpropLearner<HoleBlindPostflop, HoldemObservation>();
    }


    //--------------------------------------------------------------------
    public void trainHoleAwareFirstact(
            List<Retrodiction<HoleAwareFirstact, HoldemObservation>> data)
    {
        holeAwareFirstactLearner.add(data);
    }
    public void trainHoleBlindFirstact(
            List<Retrodiction<HoleBlindFirstact, HoldemObservation>> data)
    {
        holeBlindFirstactLearner.add(data);
    }
    public void trainHoleAwarePreflop(
            List<Retrodiction<HoleAwarePreflop, HoldemObservation>> data)
    {
        holeAwarePreflopLearner.add(data);
    }
    public void trainHoleBlindPreflop(
            List<Retrodiction<HoleBlindPreflop, HoldemObservation>> data)
    {
        holeBlindPreflopLearner.add(data);
    }
    public void trainHoleAwarePostflop(
            List<Retrodiction<HoleAwarePostflop, HoldemObservation>> data)
    {
        holeAwarePostflopLearner.add(data);
    }
    public void trainHoleBlindPostflop(
            List<Retrodiction<HoleBlindPostflop, HoldemObservation>> data)
    {
        holeBlindPostflopLearner.add(data);
    }


    //--------------------------------------------------------------------
    public <C extends PredictionContext>
            HoldemObservation predict(C context)
    {
        
        return null;
    }
}
