package ao.holdem.abs.bucket;

import ao.holdem.abs.bucket.v2.PercentileImperfectAbstractionBuilder;
import ao.holdem.abs.ViewActionAbstraction;
import ao.holdem.abs.card.CentroidStrengthAbstraction;
import ao.holdem.ai.abs.CompoundStateAbstraction;
import ao.holdem.ai.abs.StateAbstraction;
import ao.holdem.ai.abs.act.ActionAbstraction;
import ao.holdem.ai.abs.act.ActionStateUniverse;
import ao.holdem.ai.abs.act.BasicActionView;
import ao.holdem.ai.odds.OddsBy5;
import ao.holdem.engine.state.ActionState;
import ao.holdem.model.card.Community;
import ao.holdem.model.card.Hole;
import ao.holdem.model.card.chance.Deck;
import ao.holdem.model.card.sequence.CardSequence;
import com.google.common.collect.Maps;
import org.junit.Test;

import java.io.IOException;
import java.util.Map;

public class StateAbstractionTest
{
    @Test
    public void basicAbstraction() throws IOException {
        CentroidStrengthAbstraction cardAbstraction = PercentileImperfectAbstractionBuilder.loadOrBuildAndSave(
                20, 30, 30, 50);

        ActionAbstraction actionAbstraction = ViewActionAbstraction.build(
                BasicActionView.VIEW);

        StateAbstraction stateAbstraction = new CompoundStateAbstraction(
                cardAbstraction, actionAbstraction,
                OddsBy5.INSTANCE);

        System.out.println(stateAbstraction.size());

        Map<Integer, Integer> roundStateActs = Maps.newTreeMap();
        for (int i = 0; i < 10; i++) {
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
