package ao.holdem.abs;

import ao.ai.cfr.abs.AbstractionDomain;
import ao.ai.cfr.abs.AbstractionDomainBuilder;
import ao.ai.cfr.abs.impl.DigestDomain;
import ao.ai.cfr.abs.impl.DigestDomainBuilder;
import ao.ai.cfr.abs.impl.DigestDomains;
import ao.holdem.ai.abs.act.ActionAbstraction;
import ao.holdem.ai.abs.act.ActionStateUniverse;
import ao.holdem.ai.abs.act.ActionView;
import ao.holdem.engine.state.ActionState;
import ao.holdem.model.Round;

import java.io.*;

/**
 *
 */
public class ViewActionAbstraction<T extends Serializable> implements ActionAbstraction
{
    public static <T extends Serializable> ViewActionAbstraction loadOrBuildAndSave(
            File file, ActionView<T> actionView) throws IOException
    {
        if (file.exists()) {
            FileInputStream out = new FileInputStream(file);
            try {
                return load(actionView, out);
            } finally {
                out.close();
            }
        }

        ViewActionAbstraction abstraction = build(actionView);

        FileOutputStream out = new FileOutputStream(file);
        try {
            save(abstraction, out);
        } finally {
            out.close();
        }

        // load it from what was just saved
        return loadOrBuildAndSave(file, actionView);
    }

    public static <T extends Serializable> ViewActionAbstraction build(ActionView<T> actionView) {
        DigestDomainBuilder<T> preflopBuilder = new DigestDomainBuilder<>();
        DigestDomainBuilder<T> flopBuilder = new DigestDomainBuilder<>();
        DigestDomainBuilder<T> turnBuilder = new DigestDomainBuilder<>();
        DigestDomainBuilder<T> riverBuilder = new DigestDomainBuilder<>();

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

        DigestDomain<T> preflopDomain = preflopBuilder.build();
        DigestDomain<T> flopDomain = flopBuilder.build();
        DigestDomain<T> turnDomain = turnBuilder.build();
        DigestDomain<T> riverDomain = riverBuilder.build();

        return new ViewActionAbstraction<>(
                actionView, preflopDomain, flopDomain, turnDomain, riverDomain);
    }

    public static void save(ViewActionAbstraction<?> abstraction, OutputStream out) {
        DigestDomains.write(abstraction.preflopDomain, out);
        DigestDomains.write(abstraction.flopDomain, out);
        DigestDomains.write(abstraction.turnDomain, out);
        DigestDomains.write(abstraction.riverDomain, out);
    }
    public static <T extends Serializable> ViewActionAbstraction<T> load(ActionView<T> view, InputStream in) {
        DigestDomain<T> preflopDomain = DigestDomains.read(in);
        DigestDomain<T> flopDomain = DigestDomains.read(in);
        DigestDomain<T> turnDomain = DigestDomains.read(in);
        DigestDomain<T> riverDomain = DigestDomains.read(in);
        return new ViewActionAbstraction<>(view, preflopDomain, flopDomain, turnDomain, riverDomain);
    }


    private final ActionView<T> actionView;
    private final DigestDomain<T> preflopDomain;
    private final DigestDomain<T> flopDomain;
    private final DigestDomain<T> turnDomain;
    private final DigestDomain<T> riverDomain;


    public ViewActionAbstraction(
            ActionView<T> actionView,
            DigestDomain<T> preflopDomain,
            DigestDomain<T> flopDomain,
            DigestDomain<T> turnDomain,
            DigestDomain<T> riverDomain)
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
