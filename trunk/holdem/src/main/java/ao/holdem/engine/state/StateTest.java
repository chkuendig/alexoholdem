package ao.holdem.engine.state;

import ao.holdem.model.Avatar;
import ao.holdem.model.act.AbstractAction;
import ao.util.ui.AoConsole;
import org.apache.log4j.Logger;

import java.util.Arrays;
import java.util.Map;

/**
 * Date: Jan 8, 2009
 * Time: 6:40:39 PM
 *
 * expand the entire game tree for heads-up,
 *  let user interactively traverse it.
 *
 */
public class StateTest
{
    //--------------------------------------------------------------------
    private static final Logger LOG =
            Logger.getLogger(StateTest.class);


    public static void main(String[] args)
    {
//        new StateTest().interactiveHeadsUp();
        new StateTest().countStates();
    }


    //--------------------------------------------------------------------
    public void countStates()
    {
        Avatar dealee = Avatar.local("Dealee");
        Avatar dealer = Avatar.local("Dealer");

        State root = State.autoBlindInstance(Arrays.asList(
                        dealee, dealer));

        int[][] byRoundDepth = new int[4][ root.seats().length * 3 * 4 ];
        countStates(root, 0, byRoundDepth);

        for (int[] byDepth : byRoundDepth) {

            int sum = 0;
            for (int count : byDepth) sum += count;

            LOG.info(Arrays.toString(byDepth) + " = " + sum);
        }
    }

    private void countStates(
            State under, int depth, int[][] byRoundDepth)
    {
        if (under.atEndOfHand()) {
            //byRoundDepth[ 4 ][ depth ]++;
            return;
        }
        byRoundDepth[under.round().ordinal()][ depth ]++;

        for (State subState : under.viableActions().values())
        {
            countStates( subState, depth + 1, byRoundDepth );
        }
    }



    //--------------------------------------------------------------------
    public void interactiveHeadsUp()
    {
        Avatar dealee = Avatar.local("Dealee");
        Avatar dealer = Avatar.local("Dealer");

        State state = State.autoBlindInstance(Arrays.asList(
                        dealee, dealer));

        while (state.headsUpStatus() == HeadsUpStatus.IN_PROGRESS)
        {
            boolean isDealer = (state.dealerIsNext());

            LOG.info( state );
            LOG.info((isDealer ? dealer : dealee) + " to act...");

            Map<AbstractAction, State>
                           acts    = state.viableActions();
            AbstractAction nextAct = inputNextAct(acts);

            LOG.info("Performing " + nextAct);
            state = acts.get( nextAct );
        }

        LOG.info(state.headsUpStatus());
    }

    private AbstractAction inputNextAct(
            Map<AbstractAction, State> acts)
    {
        AbstractAction nextAct = null;
        do
        {
            StringBuilder query = new StringBuilder();

            if (acts.containsKey(AbstractAction.QUIT_FOLD))
                query.append("(1) Quit/Fold  ");

            if (acts.containsKey(AbstractAction.CHECK_CALL))
                query.append("(2) Check/Call  ");

            if (acts.containsKey(AbstractAction.BET_RAISE))
                query.append("(3) Bet/Raise  ");

            int choice = AoConsole.integer(query.toString());
            if (1 <= choice && choice <= 3)
            {
                AbstractAction act =
                        AbstractAction.VALUES[ choice - 1 ];

                if (acts.containsKey( act ))
                {
                    nextAct = act;
                }
            }
        }
        while (nextAct == null);
        return nextAct;
    }
}
