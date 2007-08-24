package ao.decision.domain;

import ao.decision.attr.Attribute;
import ao.decision.attr.AttributePool;
import ao.decision.data.Context;
import ao.decision.data.Example;
import ao.holdem.bots.opp_model.predict.def.context.GenericContext;
import ao.holdem.bots.opp_model.predict.def.retro.HandParser;
import ao.holdem.def.state.env.TakenAction;
import ao.holdem.def.state.domain.BettingRound;
import ao.holdem.history.HandHistory;
import ao.holdem.history.PlayerHandle;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 *
 */
public class DecisionSetup
{
    private AttributePool pool = new AttributePool();

    public List<Example<TakenAction>> postflopExamples(
            HandHistory hand,
            PlayerHandle player)
    {
        List<Example<TakenAction>> examples =
                new ArrayList<Example<TakenAction>>();

        HandParser parser = new HandParser();
        for (GenericContext ctx :
                parser.genericCasesFor(hand, player))
        {
            if (! ctx.isHistAware()) continue;
            if (ctx.round() == BettingRound.PREFLOP) continue;

            Context decisionContext = asDecisionContext(ctx);
            Example<TakenAction> decisionExample =
                    decisionContext.withTarget(
                            pool.fromEnum( ctx.currAct() ));
            examples.add( decisionExample );
        }

        return examples;
    }


    public Context asDecisionContext(GenericContext ctx)
    {
        Collection<Attribute> attrs = new ArrayList<Attribute>();

        attrs.add(pool.fromUntyped(
                "Committed This Round",
                ctx.committedThisRound()));

        attrs.add(pool.fromEnum(
                BetsToCall.fromBets(ctx.betsToCall())));

        attrs.add(pool.fromEnum( ctx.round() ));

        attrs.add(pool.fromUntyped(
                "Last Bets Called > 1",
                ctx.lastBetsToCalled() > 1));

        attrs.add(pool.fromUntyped(
                "Last Action", ctx.lastAct()));

        attrs.add(pool.fromEnum(
                ActivePosition.fromPosition(
                        ctx.numOpps(), ctx.activePosition())));

        attrs.add(pool.fromEnum(
                ActiveOpponents.fromActiveOpps(
                        ctx.numActiveOpps())));

        return new Context(attrs);
    }

}
