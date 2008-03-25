package ao.ai.monte_carlo;

/**
 * move in according to how we predict a player to move.
 */
public class BotPredictor
{
//    //--------------------------------------------------------------------
//    private PlayerHandle     handle;
//    private PredictorService predictor;
//    private List<Choice>     choices;
////    private List<Context>    contexts;
//    private boolean          isUnfolded;
//    private boolean          hasActed;
//
//    private HandState                   currState;
//    private RealHistogram<SimpleAction> pendingAction;
//
//
//    //--------------------------------------------------------------------
//    public BotPredictor(PlayerHandle     playerHandle,
//                        PredictorService predictorService)
//    {
//        handle    = playerHandle;
//        predictor = predictorService;
//
//        choices    = new ArrayList<Choice>();
////        contexts   = new ArrayList<Context>();
//        isUnfolded = true;
//        hasActed   = false;
//    }
//
//
//    //--------------------------------------------------------------------
//    public MixedAction act(StateManager env)
//    {
//        assert pendingAction == null;
//
//        Statistic stat = env.stats().forPlayer(handle.getId());
//        Context   ctx  = stat.nextActContext();
////        contexts.add(ctx);
//
//        RealHistogram<SimpleAction> hist =
//                predictor.predictAction(handle, ctx);
//
//        pendingAction = hist;
//        currState     = env.head();
//
//        hasActed = true;
//        return MixedAction.fromHistogram( hist );
//    }
//
//    public void took(SimpleAction action)
//    {
//        if (action == SimpleAction.FOLD)
//        {
//            isUnfolded = false;
//        }
//
//        choices.add(new Choice(pendingAction, action, currState));
//        currState     = null;
//        pendingAction = null;
//    }
//
//
//    //--------------------------------------------------------------------
//    public boolean isUnfolded()
//    {
//        return isUnfolded;
//    }
//
//    public boolean hasActed()
//    {
//        return hasActed;
//    }
//
//    public List<Choice> choices()
//    {
//        return choices;
//    }
//    public Choice lastChoice()
//    {
//        return choices.get( choices.size() - 1 );
//    }
//
////    public List<Context> contexts()
////    {
////        return contexts;
////    }
//
////    public Context lastContext()
////    {
////        return contexts.get( contexts.size() - 1 );
////    }
}
