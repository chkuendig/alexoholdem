package ao.ai.opp_model.input;

/**
 *
 */
public abstract class LearningPlayer
        //implements Player
{
//    //--------------------------------------------------------------------
//    public static interface Factory
//    {
//        public LearningPlayer newInstance(
//                boolean      publishActions,
//                HandHistory  history,
//                PlayerHandle player,
//                Classifier   addTo,
//                Predictor    predictWith);
//    }
//
//
//    //--------------------------------------------------------------------
//    private LinkedList<RealAction> acts;
//    private Classifier             examples;
//    private Predictor              predictor;
//    private Serializable           playerId;
//    private boolean                publish;
//
//    private ConfusionMatrix confusion = new ConfusionMatrix();
//
//
//    //--------------------------------------------------------------------
//    public LearningPlayer(boolean      publishActions,
//                          HandHistory  history,
//                          PlayerHandle player,
//                          Classifier   addTo,
//                          Predictor    predictWith)
//    {
//        acts      = new LinkedList<RealAction>();
//        playerId  = player.getId();
//        examples  = addTo;
//        publish   = publishActions;
//        predictor = predictWith;
//
//        for (Event event : history.getEvents( player ))
//        {
//            acts.add( event.getAction() );
//        }
//    }
//
//    protected Serializable playerId()
//    {
//        return playerId;
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
//        RealAction act  = shiftAction();
//        if (act.isBlind()) return act;
//
//        Statistic stat = env.stats().forPlayer(playerId);
//        Context    ctx  = stat.nextActContext();
//
//        if (publish)
//        {
//            Example addend = makeExampleOf(env, ctx, act);
//
//            if (addend != null)
//            {
//                // record prediction accuracy
//                if (predictor != null)
//                {
//                    confusion.add(
//                        addend.target().state(),
//                        predict(ctx).toRealHistogram().mostProbable());
//                }
//
//                examples.add( addend );
//            }
//        }
//        return act;
//    }
//
//    protected abstract Example
//            makeExampleOf(StateManager env,
//                          Context      ctx,
//                          RealAction   act);
//
//
//    //--------------------------------------------------------------------
//    protected Prediction predict(Context ctx)
//    {
//        //return examples.classify(ctx);
//        return (predictor == null)
//                ? null
//                : predictor.classify( ctx );
//    }
//
//    protected int actsLeft()
//    {
//        return acts.size();
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
//        return acts.removeFirst();
//    }
//
//
//    //--------------------------------------------------------------------
//    @SuppressWarnings("unchecked")
//    public void addTo(ConfusionMatrix confusionMatrix)
//    {
//        confusionMatrix.addAll( confusion );
//    }
//
//    public String toString()
//    {
//        return "confusion for player: " + playerId + "\n" +
//                confusion.toString();
//    }
}
