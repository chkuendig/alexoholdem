package ao.holdem.engine.state;

import ao.holdem.model.ChipStack;
import ao.holdem.model.Round;
import ao.holdem.model.act.Action;
import ao.holdem.model.card.chance.ChanceCards;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class ActionStateFlow
{
    //--------------------------------------------------------------------
    private ActionState head;
    private Round              lastActRound;
    private List<List<Action>> actions;


    //--------------------------------------------------------------------
    public ActionStateFlow(int playerCount, boolean autoPostBlinds)
    {
        head = new ActionState( playerCount );

        actions = new ArrayList<>(playerCount);
        for (int i = 0; i < playerCount; i++) {
            actions.add(new ArrayList<Action>());
        }

        if (autoPostBlinds)
        {
            advance(Action.SMALL_BLIND);
            advance(Action.BIG_BLIND);
        }
    }


    //--------------------------------------------------------------------
    public ActionState head()
    {
        return head;
    }

    public int playerCount() {
        return actions.size();
    }

    public Round lastActRound() {
        return lastActRound;
    }

    public List<List<Action>> actions() {
        return actions;
    }


    //--------------------------------------------------------------------
    /**
     * Advanced the hand to the next state.
     *
     * @param act action taken by nextToAct
     * @return seat state of next-to-act after this action is taken
     */
    public Seat advance(Action act)
    {
        int nextToActIndex = head.nextToActIndex();

        lastActRound = head.round();
        head         = head.advance(act);

        actions.get(nextToActIndex).add( act );

        return head.seats()[ nextToActIndex ];
    }

    public void advanceQuitter(int quitterIndex)
    {
        assert !head.atEndOfHand() : "can't quit after hand is over.";

        lastActRound = head.round();
        head         = head.advanceQuitter( quitterIndex );
    }


    //--------------------------------------------------------------------
    public List<ChipStack> deltas(ChanceCards cards)
    {
        return HandStateUtils.terminalOutcome(head(), cards);
    }
}
