package ao.holdem.abs.bucket;


import ao.holdem.abs.act.ActionAbstraction;
import ao.holdem.abs.act.ActionStateUniverse;
import ao.holdem.abs.act.BasicActionView;
import ao.holdem.abs.bucket.v2.ViewActionAbstraction;
import ao.holdem.engine.state.ActionState;
import ao.holdem.model.Round;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import org.junit.Assert;
import org.junit.Test;

public class ActionAbstractionTest
{
    @Test
    public void basicAbstraction()
    {
        ActionAbstraction actionAbstraction = ViewActionAbstraction.build(
                BasicActionView.VIEW);

        System.out.println(actionAbstraction.count(Round.PREFLOP));
        System.out.println(actionAbstraction.count(Round.FLOP));
        System.out.println(actionAbstraction.count(Round.TURN));
        System.out.println(actionAbstraction.count(Round.RIVER));

        Table<Round, Integer, Integer> roundStateActs = HashBasedTable.create();

        for (ActionState state : ActionStateUniverse.headsUpActionDecisionStates()) {
            int actCount = state.actions(false).size();

            Round round = state.round();
            int index = actionAbstraction.indexInRound(state);

            Integer existingActCount = roundStateActs.get(round, index);
            if (existingActCount != null && ! existingActCount.equals(actCount)) {
                Assert.fail();
            }

            roundStateActs.put(round, index, actCount);
        }
    }
}
