package ao.holdem.abs.bucket;

import ao.holdem.abs.CompoundStateAbstraction;
import ao.holdem.abs.StateAbstraction;
import ao.holdem.abs.act.ActionAbstraction;
import ao.holdem.abs.act.ActionStateUniverse;
import ao.holdem.abs.act.BasicActionView;
import ao.holdem.abs.bucket.v2.PercentileImperfectAbstractionBuilder;
import ao.holdem.abs.bucket.v2.ViewActionAbstraction;
import ao.holdem.abs.card.CardAbstraction;
import ao.holdem.engine.eval.odds.OddsBy5;
import ao.holdem.engine.state.ActionState;
import ao.holdem.model.card.Community;
import ao.holdem.model.card.Hole;
import ao.holdem.model.card.chance.Deck;
import ao.holdem.model.card.sequence.CardSequence;
import com.google.common.collect.Maps;
import org.junit.Test;

import java.util.Map;

public class StateAbstractionTest
{
    @Test
    public void basicAbstraction()
    {
        CardAbstraction cardAbstraction = PercentileImperfectAbstractionBuilder.loadOrBuildAndSave(
                20, 30, 30, 50);

        ActionAbstraction actionAbstraction = ViewActionAbstraction.build(
                BasicActionView.VIEW);

        StateAbstraction stateAbstraction = new CompoundStateAbstraction(
                cardAbstraction, actionAbstraction,
                OddsBy5.INSTANCE);

        System.out.println(stateAbstraction.size());

        Map<Integer, Integer> roundStateActs = Maps.newTreeMap();
        for (int i = 0; i < 10000; i++) {
            for (ActionState state : ActionStateUniverse.headsUpActionDecisionStates()) {
                int actCount = state.actions(false).size();

                Deck deck = new Deck();

                Hole hole = Hole.valueOf(deck.nextCard(), deck.nextCard());

                Community community;
                switch (state.round()) {
                    case PREFLOP: community = Community.PREFLOP; break;
                    case FLOP: community = deck.nextFlop(); break;
                    case TURN: community = deck.nextFlop().addTurn(deck.nextCard()); break;
                    case RIVER: community = deck.nextFlop().addTurn(deck.nextCard()).addRiver(deck.nextCard()); break;
                    default: throw new Error();
                }

                int index = stateAbstraction.indexOf(state, new CardSequence(hole, community));
//                if (index == 129) {
//                    stateAbstraction.indexOf(state, new CardSequence(hole, community));
//                }

                Integer existingActCount = roundStateActs.get(index);
                if (existingActCount != null && ! existingActCount.equals(actCount)) {
                    stateAbstraction.indexOf(state, new CardSequence(hole, community));
                    System.out.println("yo");
                }

                roundStateActs.put(index, actCount);
            }
        }
    }
}
