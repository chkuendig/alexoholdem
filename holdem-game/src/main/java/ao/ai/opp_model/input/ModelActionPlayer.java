package ao.ai.opp_model.input;

/**
 * A player that extracts action examples from a HandHistory
 */
public class ModelActionPlayer extends LearningPlayer
{
//    //--------------------------------------------------------------------
//    public static class Factory
//                            implements LearningPlayer.Factory
//    {
//        public LearningPlayer newInstance(
//                boolean      publishActions,
//                HandHistory  history,
//                PlayerHandle player,
//                Classifier   addTo,
//                Predictor    predictWith)
//        {
//            return new ModelActionPlayer(
//                    publishActions, history, player, addTo, predictWith);
//        }
//    }
//
//
//    //--------------------------------------------------------------------
//    public ModelActionPlayer(
//            boolean      publishActions,
//            HandHistory  history,
//            PlayerHandle player,
//            Classifier   addTo,
//            Predictor    predictWith)
//    {
//        super(publishActions, history, player, addTo, predictWith);
//    }
//
//
//    //--------------------------------------------------------------------
//    protected Example makeExampleOf(
//            StateManager env,
//            Context      ctx,
//            RealAction   act)
//    {
//        Prediction prediction = predict(ctx);
//        if (prediction != null)
//        {
////            HandState state = env.head();
////            Hole hole = env.cards().holeFor(
////                        state.nextToAct().handle() );
////            if (hole != null && hole.bothCardsVisible())
////            {
////
////            }
////
////            System.out.println(playerId()                + "\t" +
////                               ctx.data().size() + "\t" +
////                               prediction                + "\t" +
////                               act.toSimpleAction());
//        }
//
//        return ctx.withTarget(
//                new Datum(act.toSimpleAction()) );
//    }
}
