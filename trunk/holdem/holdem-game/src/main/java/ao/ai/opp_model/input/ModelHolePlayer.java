package ao.ai.opp_model.input;

/**
 * 
 */
public class ModelHolePlayer extends LearningPlayer
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
//                Predictor predictWith)
//        {
//            return new ModelHolePlayer(
//                    publishActions, history, player, addTo, predictWith);
//        }
//    }
//
//
//    //--------------------------------------------------------------------
//    private BlindOddFinder expectedOdds = new ApproxBlindOddFinder();
//
//
//    //--------------------------------------------------------------------
//    public ModelHolePlayer(
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
//            RealAction   action)
//    {
//        if (! env.holeVisible( env.nextToAct() )) return null;
//
//        HandState state = env.head();
//        Hole      hole  = env.cards().holeFor(
//                                state.nextToAct().handle() );
//
//        // showdowns only
//        if (!(actsLeft() == 0 && !action.isFold())) return null;
//
//        Community community = env.cards().community();
//
//        HandStrength actualDelta =
//                HandStrength.fromState(state, community, hole);
//
////        Prediction prediction = predict(ctx);
////        if (prediction != null)
////        {
//////            System.out.println(playerId()                + "\t" +
//////                               ctx.data().size() + "\t" +
//////                               prediction                + "\t" +
//////                               actualDelta);
////        }
//        return ctx.withTarget(new Datum( actualDelta ));
//    }
}

