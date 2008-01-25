package ao.ai.monte_carlo;

import ao.ai.opp_model.mix.MixedAction;
import ao.holdem.model.Money;
import ao.holdem.model.act.RealAction;
import ao.holdem.model.act.SimpleAction;
import ao.persist.Event;
import ao.persist.PlayerHandle;
import ao.state.PlayerState;
import ao.state.StateManager;

import java.util.List;
import java.util.Map;
import java.util.ArrayList;

/**
 *
 */
public class Simulator
{
    //--------------------------------------------------------------------
    private final StateManager                    start;
    private final Map<PlayerHandle, BotPredictor> players;
    private final PlayerHandle                    main;


    //--------------------------------------------------------------------
    public Simulator(StateManager                    startFrom,
                     Map<PlayerHandle, BotPredictor> brains,
                     PlayerHandle                    mainPlayer)
    {
        start   = startFrom;
        players = brains;
        main    = mainPlayer;
    }


    //--------------------------------------------------------------------
    public Outcome playOutHand()
    {
        boolean      isFirstAct       = true;
        List<Event>  events           = new ArrayList<Event>();
        PlayerHandle firstToAct       = start.nextToAct();
        Money        firstToActStakes = Money.ZERO;
        double       probability      = 1;
        StateManager state            = start.prototype( false );
        do
        {
            PlayerHandle player    = state.nextToAct();
            BotPredictor predictor = players.get( player );
            MixedAction  mixedAct  = predictor.act( state );
            SimpleAction act       = mixedAct.weightedRandom();

            RealAction realAct =
                    act.toEasyAction().toRealAction( state.head() );
            predictor.took( realAct.toSimpleAction() );

            if (! isFirstAct)
            {
                probability *= mixedAct.probabilityOf( act );
            }
            isFirstAct = false;

            events.add(new Event(player, state.head().round(), realAct));
            PlayerState afterAction = state.advance( realAct );
            if (firstToAct.equals( player ))
            {
                firstToActStakes = afterAction.commitment();

                if (realAct.isFold())
                {
                    return new Outcome(events,
                                       firstToActStakes,
                                       state.head().pot(),
                                       probability,
                                       false);
                }
            }
        }
        while ( !state.atEndOfHand() );

        return new Outcome(events,
                           firstToActStakes,
                           state.head().pot(),
                           probability,
                           true);
    }


    //--------------------------------------------------------------------
    public static class Outcome
    {
        //----------------------------------------------------------------
        private final List<Event> events;
        private final Money       lastActStakes;
        private final Money       totalCommit;
        private final double      probability;
        private final boolean     mainReachedShowdown;


        //----------------------------------------------------------------
        public Outcome(List<Event> events,
                       Money       lastActStakes,
                       Money       totalCommit,
                       double      probability,
                       boolean     mainReachedShowdown)
        {
            this.events              = events;
            this.lastActStakes       = lastActStakes;
            this.totalCommit         = totalCommit;
            this.probability         = probability;
            this.mainReachedShowdown = mainReachedShowdown;
        }


        //----------------------------------------------------------------
        public List<Event> events()
        {
            return events;
        }

        public Money lastActStakes()
        {
            return lastActStakes;
        }

        public Money totalCommit()
        {
            return totalCommit;
        }

        public double probability()
        {
            return probability;
        }

        public boolean mainReachedShowdown()
        {
            return mainReachedShowdown;
        }
    }
}
