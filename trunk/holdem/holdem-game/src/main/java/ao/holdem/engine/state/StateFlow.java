package ao.holdem.engine.state;

//import ao.holdem.engine.analysis.Analysis;
import ao.holdem.model.Avatar;
import ao.holdem.model.ChipStack;
import ao.holdem.model.Round;
import ao.holdem.model.act.Action;
import ao.holdem.model.card.Card;
import ao.holdem.model.card.Community;
import ao.holdem.model.card.Hole;
import ao.holdem.model.card.chance.ChanceCards;
import ao.holdem.model.replay.Replay;
import ao.odds.eval.eval_567.EvalSlow;

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
//    private Analysis                  analysis;
//    private List<Avatar>              allIns;


    //--------------------------------------------------------------------
    public StateFlow(
            List<Avatar> clockwiseDealerLast,
            boolean      autoPostBlinds)
    {
        players  = clockwiseDealerLast;
        head     = new State( players );

//        analysis = new Analysis();
//        analysis.analyze( head );

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

//        allIns = new ArrayList<Avatar>();
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
//        analysis.analyze( head );

        actions.get(nextToAct).add( act );

//        if (act.isAllIn())
//        {
//            allIns.add( nextToAct );
//        }

        return head.seats()[ nextToActIndex ];
    }

    public void advanceQuitter(Avatar quitter)
    {
        assert !head.atEndOfHand() : "can't quit after hand is over.";

        // this condition is needed for when
        //   a person quits *after* the hand is over.
//        lastActRound =
//                (head.round() == null)
//                 ? lastActRound
//                 : head.round();

        lastActRound = head.round();
        head         = head.advanceQuitter( quitter );
//        analysis.analyze( head );
    }


    //--------------------------------------------------------------------
//    public Analysis analysis()
//    {
//        return analysis;
//    }


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
            Card      eval[]    =
                    {community.flopA(), community.flopB(),
                     community.flopC(), community.turn(),
                     community.river(), null, null};

            short     topHandRank = -1;
            for (Seat seat : finalists)
            {
                Hole hole = cards.hole(seat.player());
                assert hole != null : "hole must be visible at showdown";

                eval[5] = hole.a();
                eval[6] = hole.b();
                short handRank = EvalSlow.valueOf(eval);

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


//    private Map<Avatar, Chips> deltas(ChanceCards cards)
//    {
//        assert head.atEndOfHand();
//
//        Map<Avatar, Chips> commits = new HashMap<Avatar, Chips>();
//        for (Seat seat : head.seats())
//        {
//            commits.put(seat.player(), seat.commitment());
//        }
//
//        Map<Avatar, Chips> deltas = new HashMap<Avatar, Chips>();
//        for (List<Avatar> pot : pots())
//        {
//            short        greatestHandRank    = -1;
//            List<Avatar> greatestHandHolders = new ArrayList<Avatar>();
//            Chips        lowest              = Chips.MAX_VALUE;
//
//            for (Avatar staker : pot)
//            {
//                Chips remainingCommitment = commits.get(staker);
//                if (remainingCommitment.compareTo(lowest) < 0)
//                {
//                    lowest = remainingCommitment;
//                }
//
//                short handRank = EvalSlow.valueOf(/* xxxx */);
//                if (handRank > greatestHandRank)
//                {
//                    greatestHandRank = handRank;
//
//                    greatestHandHolders.clear();
//                    greatestHandHolders.add(staker);
//                }
//                else if (handRank == greatestHandRank)
//                {
//                    greatestHandHolders.add(staker);
//                }
//            }
//
//            for (Avatar staker : pot)
//            {
//                if (greatestHandHolders.contains(staker))
//                {
//
//                }
//            }
//        }
//
//        return deltas;
//    }

    /**
     * In Holdem, when a player goes all in, he cannot win
     *  more than his stake in the pot.
     * So for example, if there are 4 players (A, B, C, D):
     *  A bets all-in.
     *  B re-raises all-in.
     *  C re-raises all-in.
     *  D calls.
     * In this situation,
     *  if A has the best cards, he will his share of the pot,
     *      then the rest will be up for grabs as if  he
     *      never existed.
     * The general rule is, the person with the best hand wins
     *  the pot elligible to him. Then, all players in smaller
     *  or equal pots are forgotten, and the process is repeated.
     */
//    private List<List<Avatar>> pots()
//    {
//        List<List<Avatar>> pots = new ArrayList<List<Avatar>>();
//        List<Avatar>       all  = new ArrayList<Avatar>(players);
//
//        pots.add( all );
//        for (Avatar allIn : allIns)
//        {
//            List<Avatar> sidepot = new ArrayList<Avatar>();
//            for (Avatar avatar : pots.get(pots.size() - 1))
//            {
//                if (! avatar.equals(allIn))
//                {
//                    sidepot.add( avatar );
//                }
//            }
//            pots.add( sidepot );
//        }
//
//        return pots;
//    }
}
