package ao.holdem.bot.simple;

import ao.holdem.bot.AbstractPlayer;
import ao.holdem.bot.simple.starting_hands.Sklansky;
import ao.holdem.engine.state.State;
import ao.holdem.model.Avatar;
import ao.holdem.model.ChipStack;
import ao.holdem.model.Round;
import ao.holdem.model.act.Action;
import ao.holdem.model.act.FallbackAction;
import ao.holdem.model.card.sequence.CardSequence;

import java.util.Map;

/**
 * 22/02/14 8:34 PM
 */
public class OptSklanskyBot extends AbstractPlayer
{
    //--------------------------------------------------------------------
    public void handEnded(Map<Avatar, ChipStack> deltas) {}


    //--------------------------------------------------------------------
    public Action act(State state, CardSequence cards)
    {
        return state.reify(sampleAction(state, cards));
    }

    private FallbackAction sampleAction(State state, CardSequence cards) {
        //    P	11	329	658
        //    S1	29	267	703
        //    S2	26	279	693
        //    S3	21	301	676
        //    S4	41	270	688
        //    S5	2	660	337
        //    S6	3	743	253
        //    S7	10	452	537
        //    S8	7	570	421
        //    S9	7	444	548


        if (state.round() != Round.PREFLOP) {
            return sample(11, 329, 658);
        }

        int group = Sklansky.groupOf(cards.hole());

        switch (group)
        {
            case 1: return sample(29, 267, 703);
            case 2: return sample(26, 279, 693);
            case 3: return sample(21, 301, 676);
            case 4: return sample(41, 270, 688);
            case 5: return sample(2 , 660, 337);
            case 6: return sample(3 , 743, 253);
            case 7: return sample(10, 452, 537);
            case 8: return sample(7 , 570, 421);
            case 9: return sample(7	, 444, 548);
        }

        throw new Error();
    }


    //--------------------------------------------------------------------
    private FallbackAction sample(double fold, double call, double raise) {
        double foldScore  = Math.random() * fold;
        double callScore  = Math.random() * call;
        double raiseScore = Math.random() * raise;

        double maxScore = Math.max(foldScore, Math.max(callScore, raiseScore));

        if (maxScore == foldScore) {
            return FallbackAction.CHECK_OR_FOLD;
        } else if (maxScore == callScore) {
            return FallbackAction.CHECK_OR_CALL;
        } else if (maxScore == raiseScore) {
            return FallbackAction.RAISE_OR_CALL;
        } else {
            throw new Error();
        }
    }
}