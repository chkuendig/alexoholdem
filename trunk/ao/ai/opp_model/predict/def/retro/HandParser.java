package ao.ai.opp_model.predict.def.retro;

import ao.ai.opp_model.predict.def.context.GenericContext;
import ao.ai.opp_model.predict.def.context.PredictionContext;
import ao.ai.opp_model.predict.def.context.firstact.HoleBlindFirstact;
import ao.ai.opp_model.predict.def.context.postflop.HoleBlindPostflop;
import ao.ai.opp_model.predict.def.context.preflop.HoleBlindPreflop;
import ao.ai.opp_model.predict.def.observation.HoldemObservation;
import ao.holdem.def.model.cards.Community;
import ao.holdem.def.model.cards.Hole;
import ao.holdem.def.state.domain.BettingRound;
import ao.holdem.def.state.env.TakenAction;
import ao.holdem.history.Event;
import ao.holdem.history.HandHistory;
import ao.holdem.history.PlayerHandle;
import ao.holdem.history.Snapshot;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


/**
 *
 */
public class HandParser
{
    //-------------------------------------------------------------------
    private final static Logger log = Logger.getLogger(Snapshot.class);


    //-------------------------------------------------------------------
    public HoldemRetroSet casesFor(
            HandHistory  hand,
            PlayerHandle player)
    {
        HoldemRetroSet       retros = new HoldemRetroSet();
        List<GenericContext> cases  = genericCasesFor(hand, player);

        for (GenericContext ctx : cases)
        {
            HoldemObservation observation =
                        new HoldemObservation(ctx.currAct());

            // hole blind
            if (ctx.round() == BettingRound.PREFLOP)
            {
                if (ctx.isHistAware())
                {
                    retros.addHoleBlindFirstact(
                        new HoleBlindFirstact(ctx),
                        observation);
                }
                else
                {
                    retros.addHoleBlindPreflop(
                            new HoleBlindPreflop(ctx),
                            observation);
                }
            }
            else
            {
                retros.addHoleBlindPostflop(
                        new HoleBlindPostflop(ctx),
                        observation);
            }

//            // hole aware
//            if (hole != null)
//            {
//                if (round == BettingRound.PREFLOP)
//                {
//                    if (prev == null)
//                    {
//                        retros.addHoleAwareFirstact(
//                            new HoleAwareFirstact(curr, hole),
//                            observation);
//                    }
//                    else
//                    {
//                        retros.addHoleAwarePreflop(
//                                new HoleAwarePreflop(
//                                        prev, prevAct, curr, hole),
//                                observation);
//                    }
//                }
//                else
//                {
//                    retros.addHoleAwarePostflop(
//                            new HoleAwarePostflop(
//                                    prev, prevAct, curr, community, hole),
//                            observation);
//                }
//            }
        }

        return retros;
    }


    //-------------------------------------------------------------------
    public List<GenericContext> genericCasesFor(
            HandHistory  hand,
            PlayerHandle player)
    {
        List<GenericContext> cases =
                new ArrayList<GenericContext>();
        Hole           hole  = extractHole(hand, player);

        Snapshot    prev    = null;
        TakenAction prevAct = null;

        Snapshot cursor = hand.snapshot();
        for (Event e : hand.getEvents())
        {
            if (e.getPlayer().equals( player ))
            {
                assert cursor.nextToAct().equals( player );
                Snapshot curr = cursor.prototype();

                BettingRound round = e.getRound();
                assert curr.comingRound() == round;
                addCase(cases,
                        prev,
                        prevAct,
                        curr,
                        e.takenAction(),
                        hand.getCommunity().asOf( round ),
                        hole);

                prev    = curr;
                prevAct = e.takenAction();
            }

            if (! tryAddEvent(cursor, e)) return Collections.emptyList();
        }

        return cases;
    }

    private void addCase(
            List<GenericContext> cases,
            Snapshot     prev,
            TakenAction  prevAct,
            Snapshot     curr,
            TakenAction  currAct,
            Community    community,
            Hole         hole)
    {
        cases.add(new GenericContext(
                prev, prevAct, curr, currAct, community, hole));
    }


    //-------------------------------------------------------------------
    public GenericContext
            genericNextToActContext(
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
                assert cursor.nextToAct().equals( nextToAct );

                prev    = cursor.prototype();
                prevAct = e.takenAction();
            }

            if (! tryAddEvent(cursor, e)) return null;
        }

        assert cursor.nextToAct().equals( nextToAct );
        return new GenericContext(
                prev, prevAct, cursor, null, hand.getCommunity(), null);
    }


    //-------------------------------------------------------------------
    public PredictionContext
            nextToActContext(
                HandHistory  hand,
                PlayerHandle nextToAct)
    {
        GenericContext ctx = genericNextToActContext(hand, nextToAct);
        return fromGeneric(ctx);
    }

    private PredictionContext fromGeneric(GenericContext ctx)
    {
        // hole blind
        if (ctx.round() == BettingRound.PREFLOP)
        {
            if (ctx.isHistAware())
            {
                return new HoleBlindPreflop(ctx);
            }
            else
            {
                return new HoleBlindFirstact(ctx);
            }
        }
        else
        {
            return new HoleBlindPostflop(ctx);
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
