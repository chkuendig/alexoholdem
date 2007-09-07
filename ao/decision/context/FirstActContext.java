package ao.decision.context;

import ao.decision.attr.AttributePool;
import ao.decision.data.Context;
import ao.decision.domain.*;
import ao.holdem.bots.opp_model.predict.def.context.GenericContext;

/**
 *
 */
public class FirstActContext extends Context
{
    public FirstActContext(AttributePool pool, GenericContext ctx)
    {
//        add(pool.fromUntyped(
//                "Committed This Round",
//                ctx.committedThisRound()));

        add(pool.fromEnum(
                BetsToCall.fromBets(ctx.betsToCall())));

//        add(pool.fromEnum( ctx.round() ));

//        add(pool.fromUntyped(
//                "Last Bets Called > 0",
//                ctx.lastBetsToCall() > 0));

//        add(pool.fromUntyped(
//                "Last Act: Bet/Raise",
//                ctx.lastAct() == TakenAction.RAISE));

        add(pool.fromEnum(
                ActivePosition.fromPosition(
                        ctx.numOpps(), ctx.activePosition())));

        add(pool.fromEnum(
                ActiveOpponents.fromActiveOpps(
                        ctx.numActiveOpps())));

        add(pool.fromEnum(
                BetRatio.fromBetRatio(
                        ctx.betRatio())));
        add(pool.fromEnum(
                PotOdds.fromPotOdds(
                        ctx.immedatePotOdds())));
        add(pool.fromEnum(
                PotRatio.fromPotRatio(
                        ctx.potRatio())));

//        add(pool.fromEnum(
//                Heat.fromHeat(
//                        thermostat.heat(ctx.community()))));
    }
}
