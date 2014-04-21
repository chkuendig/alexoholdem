package ao.holdem.engine.state;

import ao.holdem.engine.eval.EvalBy5;
import ao.holdem.model.Avatar;
import ao.holdem.model.ChipStack;
import ao.holdem.model.Round;
import ao.holdem.model.act.Action;
import ao.holdem.model.card.Card;
import ao.holdem.model.card.Community;
import ao.holdem.model.card.Hole;
import ao.holdem.model.card.chance.ChanceCards;

import java.util.*;

/**
 *
 */
public class StateFlow
{
    //--------------------------------------------------------------------
    private State              head;
    private Round              lastActRound;
    private List<List<Action>> actions;


    //--------------------------------------------------------------------
    public StateFlow(int playerCount, boolean autoPostBlinds)
    {
        head = new State( playerCount );

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
    public State head()
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
        List<Integer> winners = winners(cards);
        ChipStack[] deltas = new ChipStack[playerCount()];

        ChipStack totalCommit = ChipStack.ZERO;
        for (Seat seat : head().seats())
        {
            ChipStack commit = seat.commitment();
            totalCommit = totalCommit.plus(commit);
            deltas[seat.player()] = commit.negate();
        }

        ChipStack winnings = totalCommit.split( winners.size() );
        ChipStack remainder = totalCommit.remainder( winners.size() );
        for (int i = 0; i < winners.size(); i++)
        {
            int winner = winners.get(i);
            ChipStack total  = (i == 0)
                             ? winnings.plus( remainder )
                             : winnings;
            deltas[winner] = deltas[winner].plus(total);
        }

        return Arrays.asList(deltas);
    }

    private List<Integer> winners(ChanceCards cards)
    {
        assert head.atEndOfHand();

        List<Integer> winners = new ArrayList<>();
        List<Seat> finalists = head().unfolded();

        if (finalists.size() == 1)
        {
            winners.add( finalists.get(0).player() );
        }
        else if (finalists.size() > 1)
        {
            Community community = cards.community( Round.RIVER );
            Card[] eval = {
                    community.flopA(), community.flopB(), community.flopC(),
                    community.turn(), community.river(), null, null};

            short topHandRank = -1;
            for (Seat seat : finalists)
            {
                Hole hole = cards.hole(seat.player());
                assert hole != null : "hole must be visible at showdown";

                eval[5] = hole.a();
                eval[6] = hole.b();
                short handRank = EvalBy5.valueOf(eval);

                if (handRank > topHandRank)
                {
                    winners.clear();
                    topHandRank = handRank;
                }
                if (handRank == topHandRank)
                {
                    winners.add( seat.player() );
                }
            }
        }
        return winners;
    }
}
