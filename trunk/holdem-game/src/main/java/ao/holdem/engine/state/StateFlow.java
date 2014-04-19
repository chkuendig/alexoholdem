package ao.holdem.engine.state;

import ao.holdem.engine.state.eval.EvalBy5;
import ao.holdem.model.Avatar;
import ao.holdem.model.ChipStack;
import ao.holdem.model.Round;
import ao.holdem.model.act.Action;
import ao.holdem.model.card.Card;
import ao.holdem.model.card.Community;
import ao.holdem.model.card.Hole;
import ao.holdem.model.card.chance.ChanceCards;
import ao.holdem.model.replay.Replay;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 */
public class StateFlow
{
    //--------------------------------------------------------------------
    private State                     head;
    private Round                     lastActRound;
    private List<Avatar>              players;
    private Map<Avatar, List<Action>> actions;


    //--------------------------------------------------------------------
    public StateFlow(
            List<Avatar> clockwiseDealerLast,
            boolean      autoPostBlinds)
    {
        players = clockwiseDealerLast;
        head    = new State( players );

        actions = new HashMap<>();
        for (Avatar avatar : clockwiseDealerLast)
        {
            actions.put(avatar, new ArrayList<Action>());
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


    //--------------------------------------------------------------------
    /**
     * Advanced the hand to the next state.
     *
     * @param act action taken by nextToAct
     * @return seat state of next-to-act after this action is taken
     */
    public Seat advance(Action act)
    {
        int    nextToActIndex = head.nextToActIndex();
        Avatar nextToAct      = head.nextToAct().player();

        lastActRound = head.round();
        head         = head.advance(act);

        actions.get(nextToAct).add( act );

        return head.seats()[ nextToActIndex ];
    }

    public void advanceQuitter(Avatar quitter)
    {
        assert !head.atEndOfHand() : "can't quit after hand is over.";

        lastActRound = head.round();
        head         = head.advanceQuitter( quitter );
    }


    //--------------------------------------------------------------------
    public Replay asReplay(ChanceCards cards)
    {
        return new Replay(players, cards, lastActRound, actions);
    }

    public Map<Avatar, ChipStack> deltas(ChanceCards cards)
    {
        List<Avatar>           winners = winners(cards);
        Map<Avatar, ChipStack> deltas  = new HashMap<Avatar, ChipStack>();

        ChipStack totalCommit = ChipStack.ZERO;
        for (Seat seat : head().seats())
        {
            ChipStack commit = seat.commitment();
            totalCommit = totalCommit.plus(commit);
            deltas.put(seat.player(), commit.negate());
        }

        ChipStack winnings  = totalCommit.split(     winners.size() );
        ChipStack remainder = totalCommit.remainder( winners.size() );
        for (int i = 0; i < winners.size(); i++)
        {
            Avatar winner = winners.get(i);
            ChipStack total  = (i == 0)
                             ? winnings.plus( remainder )
                             : winnings;
            deltas.put(winner, deltas.get(winner).plus(total));
        }

        return deltas;
    }

    private List<Avatar> winners(ChanceCards cards)
    {
        assert head.atEndOfHand();

        List<Avatar> winners   = new ArrayList<Avatar>();
        List<Seat>   finalists = head().unfolded();

        if (finalists.size() == 1)
        {
            winners.add( finalists.get(0).player() );
        }
        else if (finalists.size() > 1)
        {
            Community community = cards.community( Round.RIVER );
            Card[] eval =
                    {community.flopA(), community.flopB(),
                     community.flopC(), community.turn(),
                     community.river(), null, null};

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
