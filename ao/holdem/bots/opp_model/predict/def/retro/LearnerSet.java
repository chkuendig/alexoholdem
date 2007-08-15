package ao.holdem.bots.opp_model.predict.def.retro;

import ao.holdem.bots.opp_model.predict.def.context.firstact.HoleAwareFirstact;
import ao.holdem.bots.opp_model.predict.def.context.firstact.HoleBlindFirstact;
import ao.holdem.bots.opp_model.predict.def.context.postflop.HoleAwarePostflop;
import ao.holdem.bots.opp_model.predict.def.context.postflop.HoleBlindPostflop;
import ao.holdem.bots.opp_model.predict.def.context.preflop.HoleAwarePreflop;
import ao.holdem.bots.opp_model.predict.def.context.preflop.HoleBlindPreflop;
import ao.holdem.bots.opp_model.predict.def.learn.SupervisedLearner;
import ao.holdem.bots.opp_model.predict.joone.BackpropLearner;
import ao.holdem.history.persist.Base;

import javax.persistence.Entity;
import java.util.List;

/**
 *
 */
@Entity
public class LearnerSet extends Base
{
    //--------------------------------------------------------------------
    private SupervisedLearner<HoleAwareFirstact>
            holeAwareFirstactLearner;
    private SupervisedLearner<HoleBlindFirstact>
            holeBlindFirstactLearner;

    private SupervisedLearner<HoleAwarePreflop>
            holeAwarePreflopLearner;
    private SupervisedLearner<HoleBlindPreflop>
            holeBlindPreflopLearner;

    private SupervisedLearner<HoleAwarePostflop>
            holeAwarePostflopLearner;
    private SupervisedLearner<HoleBlindPostflop>
            holeBlindPostflopLearner;


    //--------------------------------------------------------------------
    public LearnerSet()
    {
        holeAwareFirstactLearner =
                new BackpropLearner<HoleAwareFirstact>();
        holeBlindFirstactLearner =
                new BackpropLearner<HoleBlindFirstact>();

        holeAwarePreflopLearner =
                new BackpropLearner<HoleAwarePreflop>();
        holeBlindPreflopLearner =
                new BackpropLearner<HoleBlindPreflop>();

        holeAwarePostflopLearner =
                new BackpropLearner<HoleAwarePostflop>();
        holeBlindPostflopLearner =
                new BackpropLearner<HoleBlindPostflop>();
    }


    //--------------------------------------------------------------------
    // used for persistance via hybernate
    public SupervisedLearner<HoleAwareFirstact> getHoleAwareFirstactLearner() {
        return holeAwareFirstactLearner;
    }
    public void setHoleAwareFirstactLearner
            (SupervisedLearner<HoleAwareFirstact> holeAwareFirstactLearner) {
        this.holeAwareFirstactLearner = holeAwareFirstactLearner;
    }

    public SupervisedLearner<HoleBlindFirstact> getHoleBlindFirstactLearner() {
        return holeBlindFirstactLearner;
    }
    public void setHoleBlindFirstactLearner(
            SupervisedLearner<HoleBlindFirstact> holeBlindFirstactLearner) {
        this.holeBlindFirstactLearner = holeBlindFirstactLearner;
    }

    public SupervisedLearner<HoleAwarePreflop> getHoleAwarePreflopLearner() {
        return holeAwarePreflopLearner;
    }
    public void setHoleAwarePreflopLearner(
            SupervisedLearner<HoleAwarePreflop> holeAwarePreflopLearner) {
        this.holeAwarePreflopLearner = holeAwarePreflopLearner;
    }

    public SupervisedLearner<HoleBlindPreflop> getHoleBlindPreflopLearner() {
        return holeBlindPreflopLearner;
    }
    public void setHoleBlindPreflopLearner(
            SupervisedLearner<HoleBlindPreflop> holeBlindPreflopLearner) {
        this.holeBlindPreflopLearner = holeBlindPreflopLearner;
    }

    public SupervisedLearner<HoleAwarePostflop> getHoleAwarePostflopLearner() {
        return holeAwarePostflopLearner;
    }
    public void setHoleAwarePostflopLearner(
            SupervisedLearner<HoleAwarePostflop> holeAwarePostflopLearner) {
        this.holeAwarePostflopLearner = holeAwarePostflopLearner;
    }

    public SupervisedLearner<HoleBlindPostflop> getHoleBlindPostflopLearner() {
        return holeBlindPostflopLearner;
    }
    public void setHoleBlindPostflopLearner(
            SupervisedLearner<HoleBlindPostflop> holeBlindPostflopLearner) {
        this.holeBlindPostflopLearner = holeBlindPostflopLearner;
    }


    //--------------------------------------------------------------------
    public void trainHoleAwareFirstact(
            List<Retrodiction<HoleAwareFirstact>> data,
            int iterations, int millis)
    {
        getHoleAwareFirstactLearner().add(data);
        getHoleAwareFirstactLearner().learn(iterations, millis);
    }
    public void trainHoleBlindFirstact(
            List<Retrodiction<HoleBlindFirstact>> data,
            int iterations, int millis)
    {
        getHoleBlindFirstactLearner().add(data);
        getHoleBlindFirstactLearner().learn(iterations, millis);
    }
    public void trainHoleAwarePreflop(
            List<Retrodiction<HoleAwarePreflop>> data,
            int iterations, int millis)
    {
        getHoleAwarePreflopLearner().add(data);
        getHoleAwarePreflopLearner().learn(iterations, millis);
    }
    public void trainHoleBlindPreflop(
            List<Retrodiction<HoleBlindPreflop>> data,
            int iterations, int millis)
    {
        getHoleBlindPreflopLearner().add(data);
        getHoleBlindPreflopLearner().learn(iterations, millis);
    }
    public void trainHoleAwarePostflop(
            List<Retrodiction<HoleAwarePostflop>> data,
            int iterations, int millis)
    {
        getHoleAwarePostflopLearner().add(data);
        getHoleAwarePostflopLearner().learn(iterations, millis);
    }
    public void trainHoleBlindPostflop(
            List<Retrodiction<HoleBlindPostflop>> data,
            int iterations, int millis)
    {
        getHoleBlindPostflopLearner().add(data);
        getHoleBlindPostflopLearner().learn(iterations, millis);
    }


    //--------------------------------------------------------------------
//    public SupervisedLearner<?>
//            predict(C context)
//    {
//
//        return null;
//    }
}
