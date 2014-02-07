package ao.holdem.ai.ai.monte_carlo;

/**
 *
 */
public class Simulation
{
//    //--------------------------------------------------------------------
//    private final StateManager                    start;
//    private final Map<PlayerHandle, BotPredictor> players;
//    private final PlayerHandle                    main;
//
//
//    //--------------------------------------------------------------------
//    public Simulation(StateManager                   startFrom,
//                      Map<PlayerHandle, BotPredictor> brains,
//                      PlayerHandle                    mainPlayer)
//    {
//        start   = startFrom;
//        players = brains;
//        main    = mainPlayer;
//    }
//
//
//    //--------------------------------------------------------------------
//    public Rollout playOutHand()
//    {
//        List<Event>  events     = new ArrayList<Event>();
//        Money        mainStakes = Money.ZERO;
//        StateManager state      = start.prototype( false );
//        do
//        {
//            PlayerHandle player    = state.nextToAct();
//            BotPredictor predictor = players.get( player );
//            MixedAction  mixedAct  = predictor.act( state );
//            SimpleAction act       = mixedAct.weightedRandom();
//
//            RealAction realAct =
//                    act.toEasyAction().toRealAction( state.head() );
//            predictor.took( realAct.toSimpleAction() );
//
//            events.add(new Event(player, state.head().round(), realAct));
//            PlayerState afterAction = state.advance( realAct );
//            if (main.equals( player ))
//            {
//                mainStakes = afterAction.commitment();
//
//                if (realAct.isFold())
//                {
//                    return new Rollout(events,
//                                       mainStakes,
//                                       state.head().pot(),
//                                       false);
//                }
//            }
//        }
//        while ( !state.atEndOfHand() );
//
//        return new Rollout(events,
//                           mainStakes,
//                           state.head().pot(),
//                           true);
//    }
}
