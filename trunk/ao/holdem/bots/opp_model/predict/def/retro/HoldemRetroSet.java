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
    private List<Retrodiction<HoleAwareFirstact, HoldemObservation>>
            holeAwareFirstact;
    private List<Retrodiction<HoleBlindFirstact, HoldemObservation>>
            holeBlindFirstact;

    private List<Retrodiction<HoleAwarePreflop, HoldemObservation>>
            holeAwarePreflop;
    private List<Retrodiction<HoleBlindPreflop, HoldemObservation>>
            holeBlindPreflop;

    private List<Retrodiction<HoleAwarePostflop, HoldemObservation>>
            holeAwarePostflop;
    private List<Retrodiction<HoleBlindPostflop, HoldemObservation>>
            holeBlindPostflop;


    //--------------------------------------------------------------------
    public HoldemRetroSet()
    {
        holeAwareFirstact =
                new ArrayList<Retrodiction
                        <HoleAwareFirstact, HoldemObservation>>();
        holeBlindFirstact =
                new ArrayList<Retrodiction
                        <HoleBlindFirstact, HoldemObservation>>();

        holeAwarePreflop  =
                new ArrayList<Retrodiction
                        <HoleAwarePreflop, HoldemObservation>>();
        holeBlindPreflop  =
                new ArrayList<Retrodiction
                        <HoleBlindPreflop, HoldemObservation>>();

        holeAwarePostflop =
                new ArrayList<Retrodiction
                        <HoleAwarePostflop, HoldemObservation>>();
        holeBlindPostflop =
                new ArrayList<Retrodiction
                        <HoleBlindPostflop, HoldemObservation>>();
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
                new Retrodiction<HoleAwareFirstact, HoldemObservation>
                        (context, observation));
    }
    public void addHoleBlindFirstact(
            HoleBlindFirstact context,
            HoldemObservation observation)
    {
        holeBlindFirstact.add(
                new Retrodiction<HoleBlindFirstact, HoldemObservation>
                        (context, observation));
    }

    public void addHoleAwarePreflop(
            HoleAwarePreflop  context,
            HoldemObservation observation)
    {
        holeAwarePreflop.add(
                new Retrodiction<HoleAwarePreflop, HoldemObservation>
                        (context, observation));
    }
    public void addHoleBlindPreflop(
            HoleBlindPreflop  context,
            HoldemObservation observation)
    {
        holeBlindPreflop.add(
                new Retrodiction<HoleBlindPreflop, HoldemObservation>
                        (context, observation));
    }

    public void addHoleAwarePostflop(
            HoleAwarePostflop context,
            HoldemObservation observation)
    {
        holeAwarePostflop.add(
                new Retrodiction<HoleAwarePostflop, HoldemObservation>
                        (context, observation));
    }
    public void addHoleBlindPostflop(
            HoleBlindPostflop context,
            HoldemObservation observation)
    {
        holeBlindPostflop.add(
                new Retrodiction<HoleBlindPostflop, HoldemObservation>
                        (context, observation));
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
    public void train(Predictor predictor)
    {
        predictor.trainHoleAwareFirstact(holeAwareFirstact);
        predictor.trainHoleBlindFirstact(holeBlindFirstact);
        predictor.trainHoleAwarePreflop(holeAwarePreflop);
        predictor.trainHoleBlindPreflop(holeBlindPreflop);
        predictor.trainHoleAwarePostflop(holeAwarePostflop);
        predictor.trainHoleBlindPostflop(holeBlindPostflop);
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
