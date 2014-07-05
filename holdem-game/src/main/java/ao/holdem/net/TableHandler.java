package ao.holdem.net;

import ao.holdem.engine.Player;
import ao.holdem.engine.state.ActionStateFlow;
import ao.holdem.engine.state.Seat;
import ao.holdem.model.Avatar;
import ao.holdem.model.act.AbstractAction;
import ao.holdem.model.act.Action;
import ao.holdem.model.card.Community;
import ao.holdem.model.card.Hole;
import ao.holdem.model.card.sequence.CardSequence;

/**
 * 
 */
public class TableHandler
{
    //--------------------------------------------------------------------
    private final Avatar PRO;
    private final Avatar OPP;
    private final Player BRAIN;

    private ActionStateFlow stateFlow = null;


    //--------------------------------------------------------------------
    public TableHandler(
            Avatar proponent,
            Avatar opponent,
            Player proponentBrain)
    {
        PRO   = proponent;
        OPP   = opponent;
        BRAIN = proponentBrain;
    }


    //--------------------------------------------------------------------
    public void finishHand(
            Hole      proponentHole,
            Hole      opponentHole,
            Community community)
    {
        //return new StackedReplay(
        //            stateFlow.asHand( cards ),
        //            deltas);
    }


    //--------------------------------------------------------------------
    public AbstractAction initDealer(Hole hole)
    {
        BRAIN.handEnded(null);

        stateFlow = new ActionStateFlow(2, true);

        Action act = BRAIN.act(
                stateFlow.head(),
                new CardSequence(hole)/*,
                stateFlow.analysis()*/);

        stateFlow.advance(act);
        return act.abstraction();
    }

    public void initDealee()
    {
        BRAIN.handEnded(null);

        stateFlow = new ActionStateFlow(2, true);
    }


    //--------------------------------------------------------------------
    public AbstractAction respond(
            Hole           hole,
            Community      community,
            AbstractAction opponentAction)
    {
        Seat oppSeat = stateFlow.advance(
                stateFlow.head().reify(
                        opponentAction.toFallbackAction()));
        if (! oppSeat.isActive()) return null;

        Action act = BRAIN.act(
                stateFlow.head(),
                new CardSequence(hole, community)/*,
                stateFlow.analysis()*/);

        stateFlow.advance(act);
        return act.abstraction();
    }
}
