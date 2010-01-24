package ao.ai.opp_model.predict.act;

/**
 * 
 */
public class HandStats //implements CumulativeState
{
//    //--------------------------------------------------------------------
//    private Map<Serializable, SpecificStats> stats =
//                    new HashMap<Serializable, SpecificStats>();
//    private GenericStats common;
//
//
//    //--------------------------------------------------------------------
//    /**
//     * @param playerStates players to track
//     */
//    //public HandStats(StateManager state)
//    public HandStats(PlayerState playerStates[])
//    {
//        common = new GenericStats( /*state.cards()*/ );
//
//        //for (PlayerState pState : state.head().players())
//        for (PlayerState pState : playerStates)
//        {
//            stats.put(pState.handle().getId(),
//                      new SpecificStats( pState.handle() ));
//        }
//    }
//
//    private HandStats(GenericStats                     copyCommon,
//                      Map<Serializable, SpecificStats> shallowStats)
//    {
//        common = copyCommon;
//
//        for (Map.Entry<Serializable, SpecificStats> stat :
//                shallowStats.entrySet())
//        {
//            stats.put( stat.getKey(), stat.getValue().prototype() );
//        }
//    }
//
//
//    //--------------------------------------------------------------------
//    public void advance(HandState    stateBeforeAct,
//                        PlayerHandle actor,
//                        RealAction   act,
//                        HandState    stateAfterAct)
//    {
//        common.advance(stateBeforeAct,
//                       actor,
//                       act,
//                       stateAfterAct );
//
//        for (SpecificStats pStats : stats.values())
//        {
//            pStats.advance(stateBeforeAct,
//                           actor,
//                           act,
//                           stateAfterAct);
//        }
//    }
//
//
//    //--------------------------------------------------------------------
//    public Statistic forPlayer(Serializable playerId)
//    {
//        return new MultiStatistic(common, stats.get( playerId ));
//    }
//
//
//    //--------------------------------------------------------------------
//    public HandStats prototype()
//    {
//        return new HandStats(common.prototype(), stats);
//    }
}
