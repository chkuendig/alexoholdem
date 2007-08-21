package ao.holdem.bots.opp_model.predict.def.retro;

import ao.holdem.bots.opp_model.predict.def.context.firstact.HoleAwareFirstact;
import ao.holdem.bots.opp_model.predict.def.context.firstact.HoleBlindFirstact;
import ao.holdem.bots.opp_model.predict.def.context.postflop.HoleAwarePostflop;
import ao.holdem.bots.opp_model.predict.def.context.postflop.HoleBlindPostflop;
import ao.holdem.bots.opp_model.predict.def.context.preflop.HoleAwarePreflop;
import ao.holdem.bots.opp_model.predict.def.context.preflop.HoleBlindPreflop;
import ao.holdem.bots.opp_model.predict.def.observation.HoldemObservation;

/**
 *
 */
public class HoldemRetroSet
{
    //--------------------------------------------------------------------
    private RetroSet<HoleAwareFirstact> holeAwareFirstact;
    private RetroSet<HoleBlindFirstact> holeBlindFirstact;
    private RetroSet<HoleAwarePreflop>  holeAwarePreflop;
    private RetroSet<HoleBlindPreflop>  holeBlindPreflop;
    private RetroSet<HoleAwarePostflop> holeAwarePostflop;
    private RetroSet<HoleBlindPostflop> holeBlindPostflop;


    //--------------------------------------------------------------------
    public HoldemRetroSet()
    {
        holeAwareFirstact = new RetroSet<HoleAwareFirstact>();
        holeBlindFirstact = new RetroSet<HoleBlindFirstact>();
        holeAwarePreflop  = new RetroSet<HoleAwarePreflop>();
        holeBlindPreflop  = new RetroSet<HoleBlindPreflop>();
        holeAwarePostflop = new RetroSet<HoleAwarePostflop>();
        holeBlindPostflop = new RetroSet<HoleBlindPostflop>();
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
    public void add(HoldemRetroSet addend)
    {
        holeAwareFirstact.add( addend.holeAwareFirstact );
        holeBlindFirstact.add( addend.holeBlindFirstact );
        holeAwarePreflop.add(  addend.holeAwarePreflop );
        holeBlindPreflop.add(  addend.holeBlindPreflop );
        holeAwarePostflop.add( addend.holeAwarePostflop );
        holeBlindPostflop.add( addend.holeBlindPostflop );
    }


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
//        System.out.println("training holeAwareFirstact");
//        predictor.trainHoleAwareFirstact(
//                holeAwareFirstact, interations, timeout);
//        holeAwareFirstact.clear();
//
//        System.out.println("training holeBlindFirstact");
//        predictor.trainHoleBlindFirstact(
//                holeBlindFirstact, interations, timeout);
//        holeBlindFirstact.clear();
//
//        System.out.println("training holeAwarePreflop");
//        predictor.trainHoleAwarePreflop(
//                holeAwarePreflop, interations, timeout);
//        holeAwarePreflop.clear();
//
//        System.out.println("training holeBlindPreflop");
//        predictor.trainHoleBlindPreflop(
//                holeBlindPreflop, interations, timeout);
//        holeBlindPreflop.clear();
//
//        System.out.println("training holeAwarePostflop");
//        predictor.trainHoleAwarePostflop(
//                holeAwarePostflop, interations, timeout);
//        holeAwarePostflop.clear();

//        System.out.println("training holeBlindPostflop");
        predictor.trainHoleBlindPostflop(
                holeBlindPostflop, interations, timeout);
//        holeBlindPostflop.clear();
    }

    //--------------------------------------------------------------------
    @SuppressWarnings("unchecked")
    public RetroSet<?> holeAware()
    {
        RetroSet holeAware = new RetroSet();
        holeAware.add(holeAwareFirstact);
        holeAware.add(holeAwarePreflop);
        holeAware.add(holeAwarePostflop);
        return holeAware;
    }

    @SuppressWarnings("unchecked")
    public RetroSet<?> holeBlind()
    {
        RetroSet holeBlind = new RetroSet();
//        holeBlind.add(holeBlindFirstact);
//        holeBlind.add(holeBlindPreflop);
        holeBlind.add(holeBlindPostflop);
        return holeBlind;
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
