package ao.ai.opp_model.predict;

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
            Hole hole = h.getHoles().get(p);
            if (hole == null || !hole.bothCardsVisible()) continue;
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
                assert cursor.nextToAct().equals( player );
                Snapshot curr = cursor.prototype();

                if (prev != null &&
                        e.getRound() != BettingRound.PREFLOP)
                {
                    TakenAction currAct = e.takenAction();
                    Community community = hand.getCommunity().asOf( e.getRound() );

                    handCases.add(
                            new PredictionCase(
                                    prev, prevAct,
                                    curr, currAct,
                                    community,
                                    hand.getHoles().get(player)) );
                }

                prev    = curr;
                prevAct = e.takenAction();
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


    //--------------------------------------------------------------------
    public int numCases()
    {
        return cases.size();
    }

    public int caseInputSize()
    {
        return cases.get(0).asNeuralInput().length;
    }

    public int caseOutputSize()
    {
        return cases.get(0).neuralOutput().length;
    }
}
