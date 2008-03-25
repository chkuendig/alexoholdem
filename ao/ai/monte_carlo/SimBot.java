package ao.ai.monte_carlo;

/**
 *
 */
public class SimBot // extends AbstractPlayer
{
    //--------------------------------------------------------------------
//    private static final int SIM_COUNT = 32 + 1;
//
//
//    //--------------------------------------------------------------------
//    @Inject PredictorService predictor;
////    private PredictorService predictor;
//
//
//    //--------------------------------------------------------------------
//    public SimBot()
//    {
//        //predictor = new PredictorService();
//    }
//
//
//    //--------------------------------------------------------------------
//    protected EasyAction act(
//            StateManager env,
//            HandState    state,
//            Hole hole)
//    {
//        Map<SimpleAction, int[]>    counts      = initCounts();
//        Map<SimpleAction, double[]> expectation = initExpectations();
//
//        Simulator sim = new Simulator(predictor, env);
//        for (int i = 0; i < SIM_COUNT; i++)
//        {
//            ProbableRollout out = sim.rollout();
//            SimpleAction    act = out.firstAct();
//
//            counts     .get( act )[0]++;
//            expectation.get( act )[0] += out.expectedReward();
//
////            if ((i+1) % 64 == 0)
////            {
////                System.out.println(expectations(counts, expectation));
////            }
//        }
//
//        SimpleAction bestAct    = SimpleAction.FOLD;
//        double       mostExpect = Long.MIN_VALUE;
//        for (Map.Entry<SimpleAction, Double> exp :
//                expectations(counts, expectation).entrySet())
//        {
//            if (exp.getValue() > mostExpect)
//            {
//                bestAct    = exp.getKey();
//                mostExpect = exp.getValue();
//            }
//        }
//
//        return bestAct.toEasyAction();
//    }
//
//    private Map<SimpleAction, Double> expectations(
//            Map<SimpleAction, int[]>    counts,
//            Map<SimpleAction, double[]> expectation)
//    {
//        Map<SimpleAction, Double> exp =
//                new EnumMap<SimpleAction, Double>( SimpleAction.class );
//
//        for (SimpleAction act : SimpleAction.values())
//        {
//            if (counts.get(act)[0] == 0) continue;
//
//            double expect = expectation.get( act )[0] /
//                                counts.get( act )[0];
//            exp.put(act, expect);
//        }
//
//        return exp;
//    }
//
//
//    //--------------------------------------------------------------------
//    private List<PlayerHandle> initBrains(
//            HandState                       state,
//            Map<PlayerHandle, BotPredictor> brains)
//    {
//        List<PlayerHandle> atShowdown = new ArrayList<PlayerHandle>();
//
//        for (PlayerState pState : state.unfolded())
//        {
//            if (! pState.isAllIn())
//            {
//                brains.put(pState.handle(),
//                           new BotPredictor(pState.handle(),
//                                            predictor));
//            }
//            else
//            {
//                atShowdown.add( pState.handle() );
//            }
//        }
//
//        return atShowdown;
//    }
//
//    private Map<SimpleAction, int[]> initCounts()
//    {
//        Map<SimpleAction, int[]> counts =
//                new EnumMap<SimpleAction, int[]>( SimpleAction.class );
//        for (SimpleAction act : SimpleAction.values())
//        {
//            counts.put(act, new int[1]);
//        }
//        return counts;
//    }
//
//    private Map<SimpleAction, double[]> initExpectations()
//    {
//        Map<SimpleAction, double[]> expectation =
//                new EnumMap<SimpleAction, double[]>( SimpleAction.class );
//        for (SimpleAction act : SimpleAction.values())
//        {
//            expectation.put(act, new double[1]);
//        }
//        return expectation;
//    }
//
//
//    //--------------------------------------------------------------------
//    public void handEnded(HandHistory history)
//    {
//        predictor.add( history );
//    }
}
