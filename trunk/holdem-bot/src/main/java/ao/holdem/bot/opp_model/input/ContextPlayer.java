package ao.holdem.bot.opp_model.input;

/**
 *
 */
public class ContextPlayer //implements Player
{
//    //--------------------------------------------------------------------
//    private LinkedList<RealAction> acts;
//    private Serializable           playerId;
//    private List<Context>          contexts;
//    private boolean                finishedUnfolded;
//    private List<HandState>        states;
//
//
//    //--------------------------------------------------------------------
//    public ContextPlayer(HandHistory  history,
//                         PlayerHandle player)
//    {
//        acts     = new LinkedList<RealAction>();
//        contexts = new ArrayList<Context>();
//        states   = new ArrayList<HandState>();
//        playerId = player.getId();
//
//        for (Event event : history.getEvents( player ))
//        {
//            acts.add( event.getAction() );
//        }
//    }
//
//
//    //--------------------------------------------------------------------
//    public void handEnded(HandHistory history) {}
//
//
//    //--------------------------------------------------------------------
//    @SuppressWarnings("unchecked")
//    public RealAction act(final StateManager env)
//    {
//        checkPlayer(env);
//
//        RealAction act = shiftAction();
//        if (act == null) return null;
//
//        if (!act.isBlind())
//        {
//            Statistic stat = env.stats().forPlayer(playerId);
//            Context   ctx  = stat.nextActContext();
//            contexts.add( ctx );
//            states.add( env.head() );
//
//            if (acts.isEmpty() && !act.isFold())
//            {
//                finishedUnfolded = true;
//            }
//        }
//        return act;
//    }
//
//
//    //--------------------------------------------------------------------
//    public List<Context> contexts()
//    {
//        return contexts;
//    }
//
//    public List<HandState> states()
//    {
//        return states;
//    }
//
//    public boolean finishedUnfolded()
//    {
//        return finishedUnfolded;
//    }
//
//
//    //--------------------------------------------------------------------
//    public boolean shiftQuitAction()
//    {
//        boolean isQuit = !acts.isEmpty() &&
//                          acts.getFirst().equals( RealAction.QUIT );
//        if (isQuit) shiftAction();
//        return isQuit;
//    }
//
//
//    //--------------------------------------------------------------------
//    private void checkPlayer(StateManager env)
//    {
//        assert playerId.equals( env.nextToAct().getId() );
//    }
//    private RealAction shiftAction()
//    {
//        return (acts.isEmpty() ? null : acts.removeFirst());
//    }
}
