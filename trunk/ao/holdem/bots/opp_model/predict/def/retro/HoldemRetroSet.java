package ao.holdem.bots.opp_model.predict.def.retro;

import ao.holdem.bots.opp_model.predict.def.context.firstact.HoleAwareFirstact;
import ao.holdem.bots.opp_model.predict.def.context.firstact.HoleBlindFirstact;
import ao.holdem.bots.opp_model.predict.def.context.postflop.HoleAwarePostflop;
import ao.holdem.bots.opp_model.predict.def.context.postflop.HoleBlindPostflop;
import ao.holdem.bots.opp_model.predict.def.context.preflop.HoleAwarePreflop;
import ao.holdem.bots.opp_model.predict.def.context.preflop.HoleBlindPreflop;
import ao.holdem.bots.opp_model.predict.def.observation.HoldemObservation;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class HoldemRetroSet
{
    //--------------------------------------------------------------------
    private List<Retrodiction<HoleAwareFirstact>>
            holeAwareFirstact;
    private List<Retrodiction<HoleBlindFirstact>>
            holeBlindFirstact;

    private List<Retrodiction<HoleAwarePreflop>>
            holeAwarePreflop;
    private List<Retrodiction<HoleBlindPreflop>>
            holeBlindPreflop;

    private List<Retrodiction<HoleAwarePostflop>>
            holeAwarePostflop;
    private List<Retrodiction<HoleBlindPostflop>>
            holeBlindPostflop;


    //--------------------------------------------------------------------
    public HoldemRetroSet()
    {
        holeAwareFirstact =
                new ArrayList<Retrodiction<HoleAwareFirstact>>();
        holeBlindFirstact =
                new ArrayList<Retrodiction<HoleBlindFirstact>>();

        holeAwarePreflop  =
                new ArrayList<Retrodiction<HoleAwarePreflop>>();
        holeBlindPreflop  =
                new ArrayList<Retrodiction<HoleBlindPreflop>>();

        holeAwarePostflop =
                new ArrayList<Retrodiction<HoleAwarePostflop>>();
        holeBlindPostflop =
                new ArrayList<Retrodiction<HoleBlindPostflop>>();
    }


    //--------------------------------------------------------------------
//    public <T extends PredictionContext>
//            void add(List<Retrodiction<T, HoldemObservation>>
//                            retrodictions)
//    {
//        for (Retrodiction<T, HoldemObservation> retrodiction : retrodictions)
//        {
//            add(retrodiction);
//        }
//    }

//    public void add(
//            PredictionContext context,
//            HoldemObservation observation)
//    {
//        add(new Retrodiction<PredictionContext, HoldemObservation>
//                (context, observation));
//    }

//    public <T extends PredictionContext>
//            void add(
//                    Retrodiction<T, HoldemObservation>
//                            retrodiction)
//    {
//        mutableCases(retrodiction.contextClass())
//                .add(retrodiction);
//    }


    //--------------------------------------------------------------------
    public void addHoleAwareFirstact(
            HoleAwareFirstact context,
            HoldemObservation observation)
    {
        holeAwareFirstact.add(
                new Retrodiction<HoleAwareFirstact>(context, observation));
    }
    public void addHoleBlindFirstact(
            HoleBlindFirstact context,
            HoldemObservation observation)
    {
        holeBlindFirstact.add(
                new Retrodiction<HoleBlindFirstact>(context, observation));
    }

    public void addHoleAwarePreflop(
            HoleAwarePreflop  context,
            HoldemObservation observation)
    {
        holeAwarePreflop.add(
                new Retrodiction<HoleAwarePreflop>(context, observation));
    }
    public void addHoleBlindPreflop(
            HoleBlindPreflop  context,
            HoldemObservation observation)
    {
        holeBlindPreflop.add(
                new Retrodiction<HoleBlindPreflop>(context, observation));
    }

    public void addHoleAwarePostflop(
            HoleAwarePostflop context,
            HoldemObservation observation)
    {
        holeAwarePostflop.add(
                new Retrodiction<HoleAwarePostflop>(context, observation));
    }
    public void addHoleBlindPostflop(
            HoleBlindPostflop context,
            HoldemObservation observation)
    {
        holeBlindPostflop.add(
                new Retrodiction<HoleBlindPostflop>(context, observation));
    }


    //--------------------------------------------------------------------
//    public <T extends PredictionContext>
//            List<Retrodiction<T, HoldemObservation>> cases(
//                    Class<T> type)
//    {
//        return Collections.unmodifiableList(mutableCases(type));
//    }

//    public List<Class<? extends PredictionContext>>
//            contexts()
//    {
//        return new ArrayList<Class<? extends PredictionContext>>() {{
//            add(HoleAwareFirstact.class);
//            add(HoleBlindFirstact.class);
//            add(HoleAwarePreflop.class);
//            add(HoleBlindPreflop.class);
//            add(HoleAwarePostflop.class);
//            add(HoleBlindPostflop.class);
//        }};
//    }


    //--------------------------------------------------------------------
    public void train(LearnerSet predictor,
                      int interations, int timeout)
    {
        predictor.trainHoleAwareFirstact(
                holeAwareFirstact, interations, timeout);
        predictor.trainHoleBlindFirstact(
                holeBlindFirstact, interations, timeout);
        predictor.trainHoleAwarePreflop(
                holeAwarePreflop, interations, timeout);
        predictor.trainHoleBlindPreflop(
                holeBlindPreflop, interations, timeout);
        predictor.trainHoleAwarePostflop(
                holeAwarePostflop, interations, timeout);
        predictor.trainHoleBlindPostflop(
                holeBlindPostflop, interations, timeout);
    }


    //--------------------------------------------------------------------
//    @SuppressWarnings("unchecked")
//    private <T extends PredictionContext>
//            List<Retrodiction<T, HoldemObservation>> mutableCases(
//                    Class<T> type)
//    {
//        return  (List<Retrodiction<T, HoldemObservation>>)(
//                (type == HoleAwareFirstact.class)
//                ? holeAwareFirstact :
//
//                (type == HoleBlindFirstact.class)
//                ? holeBlindFirstact :
//
//                (type == HoleAwarePreflop.class)
//                ? holeAwarePreflop :
//
//                (type == HoleBlindPreflop.class)
//                ? holeBlindPreflop :
//
//                (type == HoleAwarePostflop.class)
//                ? holeAwarePostflop :
//
//                (type == HoleBlindPostflop.class)
//                ? holeBlindPostflop :
//
//                null);
//    }
}
