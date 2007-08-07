package ao.holdem.bots.opp_model.predict.def.retro;

import ao.holdem.bots.opp_model.predict.def.context.PredictionContext;
import ao.holdem.bots.opp_model.predict.def.context.firstact.HoleAwareFirstact;
import ao.holdem.bots.opp_model.predict.def.context.firstact.HoleBlindFirstact;
import ao.holdem.bots.opp_model.predict.def.context.postflop.HoleAwarePostflop;
import ao.holdem.bots.opp_model.predict.def.context.postflop.HoleBlindPostflop;
import ao.holdem.bots.opp_model.predict.def.context.preflop.HoleAwarePreflop;
import ao.holdem.bots.opp_model.predict.def.context.preflop.HoleBlindPreflop;
import ao.holdem.bots.opp_model.predict.def.observation.HoldemObservation;
import ao.holdem.bots.opp_model.predict.def.observation.Observation;
import ao.holdem.def.model.cards.Community;
import ao.holdem.def.model.cards.Hole;
import ao.holdem.def.state.domain.BettingRound;
import ao.holdem.def.state.env.TakenAction;
import ao.holdem.history.Event;
import ao.holdem.history.HandHistory;
import ao.holdem.history.PlayerHandle;
import ao.holdem.history.Snapshot;
import org.apache.log4j.Logger;


/**
 *
 */
public class HandParser
{
    //-------------------------------------------------------------------
    private final static Logger log = Logger.getLogger(Snapshot.class);


    //-------------------------------------------------------------------
    public RetroSet casesFor(
            HandHistory  hand,
            PlayerHandle player)
    {
        RetroSet cases = new RetroSet();
        Hole     hole  = extractHole(hand, player);

        Snapshot    prev    = null;
        TakenAction prevAct = null;

        Snapshot cursor = hand.snapshot();
        for (Event e : hand.getEvents())
        {
            if (e.getPlayer().equals( player ))
            {
                assert cursor.nextToActLookahead().equals( player );
                Snapshot curr = cursor.prototype();

                BettingRound round       = e.getRound();
                Observation  observation =
                        new HoldemObservation(e.getAction());

                addCase(cases,
                        prev,
                        prevAct,
                        curr,
                        hand.getCommunity().asOf( round ),
                        hole,
                        round,
                        observation);

                prev    = curr;
                prevAct = e.getAction();
            }

            if (! tryAddEvent(cursor, e)) return new RetroSet();
        }

        return cases;
    }

    private void addCase(
            RetroSet     cases,
            Snapshot     prev,
            TakenAction  prevAct,
            Snapshot     curr,
            Community    community,
            Hole         hole,
            BettingRound round,
            Observation  observation)
    {
        if (hole == null)
        {
            // hole blind

            if (round == BettingRound.PREFLOP)
            {
                if (prev == null)
                {
                    cases.addHoleBlindFirstact(
                            new HoleBlindFirstact(curr),
                            observation);
                }
                else
                {
                    cases.addHoleBlindPreflop(
                            new HoleBlindPreflop(prev, prevAct, curr),
                            observation);
                }
            }
            else
            {
                cases.addHoleBlindPostflop(
                        new HoleBlindPostflop(
                                prev, prevAct, curr, community),
                        observation);
            }
        }
        else
        {
            // hole aware

            if (round == BettingRound.PREFLOP)
            {
                if (prev == null)
                {
                    cases.addHoleAwareFirstact(
                        new HoleAwareFirstact(curr, hole),
                        observation);
                }
                else
                {
                    cases.addHoleAwarePreflop(
                            new HoleAwarePreflop(
                                    prev, prevAct, curr, hole),
                            observation);
                }
            }
            else
            {
                cases.addHoleAwarePostflop(
                        new HoleAwarePostflop(
                                prev, prevAct, curr, community, hole),
                        observation);
            }
        }
    }


    //-------------------------------------------------------------------
    public PredictionContext
            nextToActContext(
                HandHistory  hand,
                PlayerHandle nextToAct)
    {
        Snapshot    prev    = null;
        TakenAction prevAct = null;

        Snapshot cursor = hand.snapshot();
        for (Event e : hand.getEvents())
        {
            if (e.getPlayer().equals( nextToAct ))
            {
                assert cursor.nextToActLookahead().equals( nextToAct );

                prev    = cursor.prototype();
                prevAct = e.getAction();
            }

            if (! tryAddEvent(cursor, e)) return null;
        }

        assert cursor.nextToActLookahead().equals( nextToAct );
        return nextToActContext(
                prev, prevAct, cursor,
                hand.getCommunity());
    }

    private PredictionContext nextToActContext(
            Snapshot    prev,
            TakenAction prevAct,
            Snapshot    curr,
            Community   community)
    {
        if (curr.round() == BettingRound.PREFLOP)
        {
            if (prev == null)
            {
                return new HoleBlindFirstact(curr);
            }
            else
            {
                return new HoleBlindPreflop(
                        prev, prevAct, curr);
            }
        }
        else
        {
            return new HoleBlindPostflop(
                    prev, prevAct, curr, community);
        }
    }

    
    //-------------------------------------------------------------------
    private boolean tryAddEvent(
            Snapshot cursor,
            Event    e)
    {
        try
        {
            cursor.addNextEvent( e );
            return true;
        }
        catch (Error ex)
        {
            log.warn(ex);
            return false;
        }
    }

    //-------------------------------------------------------------------
    private Hole extractHole(HandHistory hand, PlayerHandle player)
    {
        Hole hole = hand.getHoles().get(player);

        if (hole != null && !hole.bothCardsVisible())
        {
            hole = null;
        }

        return hole;
    }
}
