package ao.holdem.abs;

import ao.ai.cfr.abs.AbstractionDomain;
import ao.ai.cfr.abs.AbstractionDomainBuilder;
import ao.ai.cfr.abs.impl.DigestDomainBuilder;
import ao.holdem.ai.abs.act.ActionAbstraction;
import ao.holdem.ai.abs.act.ActionStateUniverse;
import ao.holdem.ai.abs.act.ActionView;
import ao.holdem.engine.state.ActionState;
import ao.holdem.model.Round;

import java.io.Serializable;

/**
 *
 */
public class ViewActionAbstraction<T extends Serializable> implements ActionAbstraction
{
    public static <T extends Serializable> ViewActionAbstraction build(ActionView<T> actionView) {
        AbstractionDomainBuilder<T> preflopBuilder = new DigestDomainBuilder<>();
        AbstractionDomainBuilder<T> flopBuilder = new DigestDomainBuilder<>();
        AbstractionDomainBuilder<T> turnBuilder = new DigestDomainBuilder<>();
        AbstractionDomainBuilder<T> riverBuilder = new DigestDomainBuilder<>();

        for (ActionState state : ActionStateUniverse.headsUpActionDecisionStates()) {
            AbstractionDomainBuilder<T> builder;
            switch (state.round()) {
                case PREFLOP: builder = preflopBuilder; break;
                case FLOP: builder = flopBuilder; break;
                case TURN: builder = turnBuilder; break;
                case RIVER: builder = riverBuilder; break;
                default: throw new Error();
            }

            T view = actionView.view(state);
            builder.add(view);
        }

        AbstractionDomain<T> preflopDomain = preflopBuilder.build();
        AbstractionDomain<T> flopDomain = flopBuilder.build();
        AbstractionDomain<T> turnDomain = turnBuilder.build();
        AbstractionDomain<T> riverDomain = riverBuilder.build();

        return new ViewActionAbstraction<T>(
                actionView, preflopDomain, flopDomain, turnDomain, riverDomain);
    }


    private final ActionView<T> actionView;
    private final AbstractionDomain<T> preflopDomain;
    private final AbstractionDomain<T> flopDomain;
    private final AbstractionDomain<T> turnDomain;
    private final AbstractionDomain<T> riverDomain;


    public ViewActionAbstraction(
            ActionView<T> actionView,
            AbstractionDomain<T> preflopDomain,
            AbstractionDomain<T> flopDomain,
            AbstractionDomain<T> turnDomain,
            AbstractionDomain<T> riverDomain)
    {
        this.actionView = actionView;
        this.preflopDomain = preflopDomain;
        this.flopDomain = flopDomain;
        this.turnDomain = turnDomain;
        this.riverDomain = riverDomain;
    }


    @Override
    public int indexInRound(ActionState state) {
        T view = actionView.view(state);
        AbstractionDomain<T> domain = domain(state.round());
        return domain.indexOf(view);
    }

    @Override
    public int count(Round round) {
        return domain(round).size();
    }


    private AbstractionDomain<T> domain(Round round) {
        switch (round) {
            case PREFLOP: return preflopDomain;
            case FLOP: return flopDomain;
            case TURN: return turnDomain;
            case RIVER: return riverDomain;
            default: throw new Error();
        }
    }
}
