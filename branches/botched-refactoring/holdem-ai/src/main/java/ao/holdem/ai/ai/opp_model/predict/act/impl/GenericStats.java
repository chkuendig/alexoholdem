package ao.ai.opp_model.predict.act.impl;

/**
 * betting round
 * community heat
 * 
 */
public class GenericStats// implements CumulativeStatistic<GenericStats>
{
//    //--------------------------------------------------------------------
//    private HandState       forefront;
//    //private CommunitySource community;
//
//    private int checks;
//    private int calls;
//    private int raises;
//
//
//    //--------------------------------------------------------------------
//    public GenericStats(/*CommunitySource communitySource*/)
//    {
//        //community = communitySource;
//    }
//
//    private GenericStats(HandState       copyForefront/*,
//                         CommunitySource copyCommunity*/)
//    {
//        forefront = copyForefront;
//        //community = copyCommunity;
//    }
//
//
//    //--------------------------------------------------------------------
//    public void advance(HandState    stateBeforeAct,
//                        PlayerHandle actor,
//                        RealAction   act,
//                        HandState    stateAfterAct)
//    {
//        if (! act.isBlind())
//        {
//                 if (act == RealAction.CHECK) checks++;
//            else if (act.isCheckCall())       calls++;
//            else if (act.isBetRaise())        raises++;
//        }
//
//        forefront = stateAfterAct;
//    }
//
//
//    //--------------------------------------------------------------------
//    public Context nextActContext()
//    {
//        Context ctx = new ContextImpl();
//
//        BettingRound round = forefront.round();
//        ctx.add(new Datum( round ));
//
////        if (round != BettingRound.PREFLOP)
////        {
////            ctx.add(new Datum("Community Heat",
////                        CommunityMeasure.measure(
////                                community.community().asOf(round))));
////        }
//
//        // betting stats
//        int numActs = checks + calls + raises;
//        if (numActs != 0)
//        {
//            ctx.add(new Datum("Total Bet Ratio",
//                            (double) raises / numActs));
////            ctx.add(new Datum("Total Call Ratio",
////                            (double) calls / numActs));
//        }
//
//        ctx.add(new Datum("Players",
//                        forefront.players().length));
//        ctx.add(new Datum("Active Players",
//                        (double) forefront.numActivePlayers()
//                                 / forefront.players().length));
//
//        return ctx;
//    }
//
////    public EnumSet<ContextDomain> nextActDomains()
////    {
////        return EnumSet.allOf( ContextDomain.class );
////    }
//
//
//    //--------------------------------------------------------------------
//    public GenericStats prototype()
//    {
//        return new GenericStats(forefront/*, community*/);
//    }
}
