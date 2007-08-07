package ao.holdem.bots.opp_model.predict.def.retro;

import ao.holdem.bots.opp_model.predict.def.context.PredictionContext;
import ao.holdem.bots.opp_model.predict.def.context.firstact.HoleAwareFirstact;
import ao.holdem.bots.opp_model.predict.def.context.firstact.HoleBlindFirstact;
import ao.holdem.bots.opp_model.predict.def.context.postflop.HoleAwarePostflop;
import ao.holdem.bots.opp_model.predict.def.context.postflop.HoleBlindPostflop;
import ao.holdem.bots.opp_model.predict.def.context.preflop.HoleAwarePreflop;
import ao.holdem.bots.opp_model.predict.def.context.preflop.HoleBlindPreflop;
import ao.holdem.bots.opp_model.predict.def.observation.Observation;

import java.util.Collections;
import java.util.List;
import java.util.ArrayList;

/**
 *
 */
public class RetroSet
{
    //--------------------------------------------------------------------
    private List<Retrodiction> holeAwareFirstact;
    private List<Retrodiction> holeBlindFirstact;

    private List<Retrodiction> holeAwarePreflop;
    private List<Retrodiction> holeBlindPreflop;

    private List<Retrodiction> holeAwarePostflop;
    private List<Retrodiction> holeBlindPostflop;


    //--------------------------------------------------------------------
    public RetroSet()
    {
        holeAwareFirstact = new ArrayList<Retrodiction>();
        holeBlindFirstact = new ArrayList<Retrodiction>();

        holeAwarePreflop  = new ArrayList<Retrodiction>();
        holeBlindPreflop  = new ArrayList<Retrodiction>();

        holeAwarePostflop = new ArrayList<Retrodiction>();
        holeBlindPostflop = new ArrayList<Retrodiction>();
    }


    //--------------------------------------------------------------------
    public void add(
            List<Retrodiction> retrodictions)
    {
        for (Retrodiction retrodiction : retrodictions)
        {
            add(retrodiction);
        }
    }

    public void add(
            PredictionContext context,
            Observation       observation)
    {
        add(new Retrodiction(context, observation));
    }

    public void add(
            Retrodiction retrodiction)
    {
        mutableCases(retrodiction.contextClass())
                .add(retrodiction);
    }


    //--------------------------------------------------------------------
    public void addHoleAwareFirstact(
            HoleAwareFirstact context,
            Observation       observation)
    {
        holeAwareFirstact.add(
                new Retrodiction(context, observation));
    }
    public void addHoleBlindFirstact(
            HoleBlindFirstact context,
            Observation       observation)
    {
        holeBlindFirstact.add(
                new Retrodiction(context, observation));
    }

    public void addHoleAwarePreflop(
            HoleAwarePreflop context,
            Observation       observation)
    {
        holeAwarePreflop.add(
                new Retrodiction(context, observation));
    }
    public void addHoleBlindPreflop(
            HoleBlindPreflop context,
            Observation       observation)
    {
        holeBlindPreflop.add(
                new Retrodiction(context, observation));
    }

    public void addHoleAwarePostflop(
            HoleAwarePostflop context,
            Observation       observation)
    {
        holeAwarePostflop.add(
                new Retrodiction(context, observation));
    }
    public void addHoleBlindPostflop(
            HoleBlindPostflop context,
            Observation       observation)
    {
        holeBlindPostflop.add(
                new Retrodiction(context, observation));
    }


    //--------------------------------------------------------------------
    public List<Retrodiction> cases(
            Class<? extends PredictionContext> type)
    {
        return Collections.unmodifiableList(mutableCases(type));
    }

    public List<Retrodiction> mutableCases(
            Class<? extends PredictionContext> type)
    {
        return  (type == HoleAwareFirstact.class)
                ? holeAwareFirstact :

                (type == HoleBlindFirstact.class)
                ? holeBlindFirstact :

                (type == HoleAwarePreflop.class)
                ? holeAwarePreflop :

                (type == HoleBlindPreflop.class)
                ? holeBlindPreflop :

                (type == HoleAwarePostflop.class)
                ? holeAwarePostflop :

                (type == HoleBlindPostflop.class)
                ? holeBlindPostflop :

                null;
    }
}
