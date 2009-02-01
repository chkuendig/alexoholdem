package ao.holdem.engine.state;

import ao.holdem.model.Avatar;
import ao.holdem.model.act.AbstractAction;
import ao.util.io.Console;

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

        int[] byDepth = new int[ root.seats().length * 3 * 4 ];
        countStates(root, 0, byDepth);
        System.out.println( Arrays.toString(byDepth) );
        
        int sum = 0;
        for (int count : byDepth) sum += count;
        System.out.println("total: " + sum);
    }

    private void countStates(
            State under, int depth, int[] byDepth)
    {
        byDepth[ depth ]++;
        if (under.atEndOfHand()) return;

        for (State subState : under.viableActions().values())
        {
            countStates( subState, depth + 1, byDepth );
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

            System.out.println( state );

            System.out.println(
                    (isDealer ? dealer : dealee) + " to act...");

            Map<AbstractAction, State>
                           acts    = state.viableActions();
            AbstractAction nextAct = inputNextAct(acts);

            System.out.println("Performing " + nextAct);
            state = acts.get( nextAct );
        }

        System.out.println(
                state.headsUpStatus());
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

            int choice = Console.integer(query.toString());
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
