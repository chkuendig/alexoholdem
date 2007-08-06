package ao.holdem.bots.opp_model.predict;

import ao.holdem.def.model.cards.Community;
import ao.holdem.def.state.domain.BettingRound;
import ao.holdem.def.state.env.TakenAction;
import ao.holdem.history.Event;
import ao.holdem.history.HandHistory;
import ao.holdem.history.PlayerHandle;
import ao.holdem.history.Snapshot;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class PredictionSet
{
    //-------------------------------------------------------------------
    private final static Logger log = Logger.getLogger(Snapshot.class);


    //--------------------------------------------------------------------
    private final List<PredictionCase> cases;


    //--------------------------------------------------------------------
    public PredictionSet()
    {
        cases = new ArrayList<PredictionCase>();
    }

    
    //--------------------------------------------------------------------
    public void addPlayerHands(PlayerHandle p)
    {
        for (HandHistory h : p.getHands())
        {
            cases.addAll( casesFor(p, h) );
        }
    }

    private List<PredictionCase> casesFor(
            PlayerHandle player,
            HandHistory  hand)
    {
        List<PredictionCase> handCases = new ArrayList<PredictionCase>();

        Snapshot prev = null;
        TakenAction prevAct = null;

        Snapshot cursor = hand.snapshot();
        for (Event e : hand.getEvents())
        {
            if (e.getPlayer().equals( player ))
            {
                assert cursor.nextToActLookahead().equals( player );
                Snapshot curr = cursor.prototype();

                if (prev != null &&
                        e.getRound() != BettingRound.PREFLOP)
                {
                    TakenAction currAct = e.getAction();
                    Community community = hand.getCommunity().asOf( e.getRound() );

                    handCases.add( new PredictionCase(prev, prevAct,
                                                      curr, currAct,
                                                      community) );
                }

                prev    = curr;
                prevAct = e.getAction();
            }

            try
            {
                cursor.addNextEvent( e );
            }
            catch (Error ex)
            {
                log.warn(ex);
                return new ArrayList<PredictionCase>();
            }
        }

        return handCases;
    }


    //--------------------------------------------------------------------
    public List<PredictionCase> cases()
    {
        return cases;
    }
}
