package ao.ai.opp_model.predict.def.retro;

import ao.ai.opp_model.predict.def.context.firstact.HoleAwareFirstact;
import ao.ai.opp_model.predict.def.context.firstact.HoleBlindFirstact;
import ao.ai.opp_model.predict.def.context.postflop.HoleAwarePostflop;
import ao.ai.opp_model.predict.def.context.postflop.HoleBlindPostflop;
import ao.ai.opp_model.predict.def.context.preflop.HoleAwarePreflop;
import ao.ai.opp_model.predict.def.context.preflop.HoleBlindPreflop;
import ao.ai.opp_model.predict.def.learn.SupervisedLearner;
import ao.ai.opp_model.predict.joone.BackpropLearner;
import ao.holdem.history.persist.Base;

import javax.persistence.Entity;

/**
 *
 */
@Entity
public class LearnerSet extends Base
{
    //--------------------------------------------------------------------
    private SupervisedLearner<HoleAwareFirstact>
            holeAwareFirstact;
    private SupervisedLearner<HoleBlindFirstact>
            holeBlindFirstact;

    private SupervisedLearner<HoleAwarePreflop>
            holeAwarePreflop;
    private SupervisedLearner<HoleBlindPreflop>
            holeBlindPreflop;

    private SupervisedLearner<HoleAwarePostflop>
            holeAwarePostflop;
    private SupervisedLearner<HoleBlindPostflop>
            holeBlindPostflop;


    //--------------------------------------------------------------------
    public LearnerSet()
    {
        holeAwareFirstact = new BackpropLearner<HoleAwareFirstact>(3);
        holeBlindFirstact = new BackpropLearner<HoleBlindFirstact>(3);
        holeAwarePreflop  = new BackpropLearner<HoleAwarePreflop>(3);
        holeBlindPreflop  = new BackpropLearner<HoleBlindPreflop>(3);
        holeAwarePostflop = new BackpropLearner<HoleAwarePostflop>(3);
        holeBlindPostflop = new BackpropLearner<HoleBlindPostflop>(3);

//        holeAwareFirstact = new HoldemLearner<HoleAwareFirstact>();
//        holeBlindFirstact = new HoldemLearner<HoleBlindFirstact>();
//        holeAwarePreflop  = new HoldemLearner<HoleAwarePreflop>();
//        holeBlindPreflop  = new HoldemLearner<HoleBlindPreflop>();
//        holeAwarePostflop = new HoldemLearner<HoleAwarePostflop>();
//        holeBlindPostflop = new HoldemLearner<HoleBlindPostflop>();
    }


    //--------------------------------------------------------------------
    // used for persistance via hybernate
    public SupervisedLearner<HoleAwareFirstact> getHoleAwareFirstact() {
        return holeAwareFirstact;
    }
    public void setHoleAwareFirstact
            (SupervisedLearner<HoleAwareFirstact> holeAwareFirstact) {
        this.holeAwareFirstact = holeAwareFirstact;
    }

    public SupervisedLearner<HoleBlindFirstact> getHoleBlindFirstact() {
        return holeBlindFirstact;
    }
    public void setHoleBlindFirstact(
            SupervisedLearner<HoleBlindFirstact> holeBlindFirstact) {
        this.holeBlindFirstact = holeBlindFirstact;
    }

    public SupervisedLearner<HoleAwarePreflop> getHoleAwarePreflop() {
        return holeAwarePreflop;
    }
    public void setHoleAwarePreflop(
            SupervisedLearner<HoleAwarePreflop> holeAwarePreflop) {
        this.holeAwarePreflop = holeAwarePreflop;
    }

    public SupervisedLearner<HoleBlindPreflop> getHoleBlindPreflop() {
        return holeBlindPreflop;
    }
    public void setHoleBlindPreflop(
            SupervisedLearner<HoleBlindPreflop> holeBlindPreflop) {
        this.holeBlindPreflop = holeBlindPreflop;
    }

    public SupervisedLearner<HoleAwarePostflop> getHoleAwarePostflop() {
        return holeAwarePostflop;
    }
    public void setHoleAwarePostflop(
            SupervisedLearner<HoleAwarePostflop> holeAwarePostflop) {
        this.holeAwarePostflop = holeAwarePostflop;
    }

    public SupervisedLearner<HoleBlindPostflop> getHoleBlindPostflop() {
        return holeBlindPostflop;
    }
    public void setHoleBlindPostflop(
            SupervisedLearner<HoleBlindPostflop> holeBlindPostflop) {
        this.holeBlindPostflop = holeBlindPostflop;
    }


    //--------------------------------------------------------------------
    public void trainHoleAwareFirstact(
            RetroSet<HoleAwareFirstact> data,
            int iterations, int millis)
    {
        getHoleAwareFirstact().add(data);
        getHoleAwareFirstact().learn(iterations, millis);
    }
    public void trainHoleBlindFirstact(
            RetroSet<HoleBlindFirstact> data,
            int iterations, int millis)
    {
        getHoleBlindFirstact().add(data);
        getHoleBlindFirstact().learn(iterations, millis);
    }
    public void trainHoleAwarePreflop(
            RetroSet<HoleAwarePreflop> data,
            int iterations, int millis)
    {
        getHoleAwarePreflop().add(data);
        getHoleAwarePreflop().learn(iterations, millis);
    }
    public void trainHoleBlindPreflop(
            RetroSet<HoleBlindPreflop> data,
            int iterations, int millis)
    {
        getHoleBlindPreflop().add(data);
        getHoleBlindPreflop().learn(iterations, millis);
    }
    public void trainHoleAwarePostflop(
            RetroSet<HoleAwarePostflop> data,
            int iterations, int millis)
    {
        getHoleAwarePostflop().add(data);
        getHoleAwarePostflop().learn(iterations, millis);
    }
    public void trainHoleBlindPostflop(
            RetroSet<HoleBlindPostflop> data,
            int iterations, int millis)
    {
        getHoleBlindPostflop().add(data);
        getHoleBlindPostflop().learn(iterations, millis);
    }


    //--------------------------------------------------------------------
    public PredictorSet predictors()
    {
        return new PredictorSet(
                getHoleAwareFirstact().predictor(),
                getHoleBlindFirstact().predictor(),
                getHoleAwarePreflop().predictor(),
                getHoleBlindPreflop().predictor(),
                getHoleAwarePostflop().predictor(),
                getHoleBlindPostflop().predictor());
    }



    //--------------------------------------------------------------------
//    public SupervisedLearner<?>
//            predict(C context)
//    {
//
//        return null;
//    }
}
