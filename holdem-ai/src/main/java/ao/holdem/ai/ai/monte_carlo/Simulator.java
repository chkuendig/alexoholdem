package ao.holdem.ai.ai.monte_carlo;

/**
 *
 */
public class Simulator
{
//    //--------------------------------------------------------------------
//    private final PredictorService                predictor;
//    private final Map<PlayerHandle, List<Choice>> baseChoices;
//    private final HandState                       state;
//    private final StateManager                    env;
//
//
//    //--------------------------------------------------------------------
//    public Simulator(PredictorService predictorService,
//                     StateManager     startFrom)
//    {
//        env         = startFrom;
//        state       = startFrom.head();
//        predictor   = predictorService;
//        baseChoices = predictor.extractChoices( startFrom.toHistory() );
//    }
//
//
//    //--------------------------------------------------------------------
//    public ProbableRollout rollout()
//    {
//        Map<PlayerHandle, List<Choice>> choices =
//                cloneBaseChoices(baseChoices);
//        Map<PlayerHandle, BotPredictor> brains  =
//                new HashMap<PlayerHandle, BotPredictor>();
//        List<PlayerHandle> atShowdown = initBrains(state, brains);
//
//        PlayerHandle me  = env.nextToAct();
//        Simulation   sim = new Simulation(env, brains, me);
//        Rollout      out = sim.playOutHand();
//
//        int stakes = out.mainStakes().smallBlinds();
//
//        if (out.mainReachedShowdown())
//        {
//            extractSimulatedChoices(brains, choices, atShowdown);
//            double winProb = winProbability(me, choices);
//
//            int ifWin = out.potSize().smallBlinds() - stakes;
//            return out.with((         winProb  * ifWin
//                             - (1.0 - winProb) * stakes));
//        }
//        else
//        {
//            return out.with( -stakes );
//        }
//    }
//
//    public Reward expectedAtShowdown(PlayerHandle forPlayer)
//    {
//        assert env.atEndOfHand();
//
//        double winProb =
//                winProbability(forPlayer, baseChoices);
//
//        int stakes = env.head().stakes().smallBlinds();
//        int ifWin  = env.head().pot().smallBlinds() - stakes;
//        return new Reward(        winProb  * ifWin
//                          -(1.0 - winProb) * stakes);
//    }
//
//
//    //--------------------------------------------------------------------
//    private double winProbability(
//            PlayerHandle                    me,
//            Map<PlayerHandle, List<Choice>> choices)
//    {
//        if (choices.isEmpty())
//        {
//            // everybody (including me) folded, leaving some player
//            //  the winner without acting
//            return 0;
//        }
//        else if (choices.size() == 1)
//        {
//            return choices.containsKey( me ) ? 1.0 : 0.0;
//        }
//        else
//        {
//            RealHistogram<PlayerHandle> results =
//                    predictor.approximate( choices );
//            return results == null
//                    ? 1.0 / choices.size()
//                    : results.probabilityOf( me );
//        }
//    }
//
//    private void extractSimulatedChoices(
//            Map<PlayerHandle, BotPredictor> brains,
//            Map<PlayerHandle, List<Choice>> choices,
//            List<PlayerHandle>              atShowdown)
//    {
//        for (Map.Entry<PlayerHandle, BotPredictor> p :
//                brains.entrySet())
//        {
//            BotPredictor predictor = p.getValue();
//            if (predictor.isUnfolded() && predictor.hasActed())
//            {
//                List<Choice> base = choices.get( p.getKey() );
//                if (base == null)
//                {
//                    base = new ArrayList<Choice>();
//                    choices.put(p.getKey(), base);
//                }
//                base.addAll( p.getValue().choices() );
//                atShowdown.add( p.getKey() );
//            }
//        }
//        choices.keySet().retainAll( atShowdown );
//    }
//
//
//    //--------------------------------------------------------------------
//    private Map<PlayerHandle, List<Choice>> cloneBaseChoices(
//                Map<PlayerHandle, List<Choice>> baseChoices)
//    {
//        Map<PlayerHandle, List<Choice>> clone =
//                new HashMap<PlayerHandle, List<Choice>>();
//        for (Map.Entry<PlayerHandle, List<Choice>> choice :
//                baseChoices.entrySet())
//        {
//            clone.put(choice.getKey(),
//                      new ArrayList<Choice>( choice.getValue() ));
//        }
//        return clone;
//    }
//
//    private List<PlayerHandle> initBrains(
//            HandState                       state,
//            Map<PlayerHandle, BotPredictor> brains)
//    {
//        List<PlayerHandle> atShowdown = new ArrayList<PlayerHandle>();
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
//        return atShowdown;
//    }
}
