package ao.holdem.engine.state;


import ao.holdem.engine.eval.EvalBy5;
import ao.holdem.model.ChipStack;
import ao.holdem.model.Round;
import ao.holdem.model.card.Card;
import ao.holdem.model.card.Community;
import ao.holdem.model.card.Hole;
import ao.holdem.model.card.chance.ChanceCards;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public enum HandStateUtils {;

    public static List<ChipStack> terminalOutcome(ActionState state, ChanceCards cards)
    {
        if (! state.atEndOfHand()) {
            throw new IllegalArgumentException("Must be at end of hand");
        }

        List<Integer> winners = winners(state, cards);
        ChipStack[] deltas = new ChipStack[state.seats().length];

        ChipStack totalCommit = ChipStack.ZERO;
        for (Seat seat : state.seats())
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

    private static List<Integer> winners(ActionState state, ChanceCards cards)
    {
        List<Integer> winners = new ArrayList<>();
        List<Seat> finalists = state.unfolded();

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
