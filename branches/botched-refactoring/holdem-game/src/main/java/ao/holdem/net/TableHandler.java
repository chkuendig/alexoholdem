package ao.holdem.net;

import ao.holdem.engine.Player;
import ao.holdem.engine.state.Seat;
import ao.holdem.engine.state.StateFlow;
import ao.holdem.model.Avatar;
import ao.holdem.model.act.AbstractAction;
import ao.holdem.model.act.Action;
import ao.holdem.model.card.Community;
import ao.holdem.model.card.Hole;
import ao.holdem.model.card.sequence.LiteralCardSequence;

import java.util.Arrays;

/**
 * 
 */
public class TableHandler
{
    //--------------------------------------------------------------------
    private final Avatar PRO;
    private final Avatar OPP;
    private final Player BRAIN;

    private StateFlow stateFlow = null;


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

        stateFlow = new StateFlow(
                Arrays.asList(OPP, PRO), true);

        Action act = BRAIN.act(
                stateFlow.head(),
                new LiteralCardSequence(hole)/*,
                stateFlow.analysis()*/);

        stateFlow.advance(act);
        return act.abstraction();
    }

    public void initDealee()
    {
        BRAIN.handEnded(null);

        stateFlow = new StateFlow(
                Arrays.asList(PRO, OPP), true);
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
                new LiteralCardSequence(hole, community)/*,
                stateFlow.analysis()*/);

        stateFlow.advance(act);
        return act.abstraction();
    }
}
