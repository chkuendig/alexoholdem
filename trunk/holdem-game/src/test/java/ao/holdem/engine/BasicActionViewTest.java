package ao.holdem.engine;


import ao.ai.cfr.abs.AbstractionDomain;
import ao.ai.cfr.abs.AbstractionDomainBuilder;
import ao.ai.cfr.abs.impl.DigestDomainBuilder;
import ao.holdem.abs.act.BasicActionView;
import ao.holdem.engine.state.ActionState;
import ao.holdem.engine.state.tree.StateTree;
import org.junit.Test;

public class BasicActionViewTest
{
    @Test
    public void stateTreeCounted()
    {
        AbstractionDomainBuilder<BasicActionView> domainBuilder = new DigestDomainBuilder<>();

        add(domainBuilder, StateTree.headsUpRoot());

        AbstractionDomain<BasicActionView> domain = domainBuilder.build();

        int index = domain.indexOf(new BasicActionView(StateTree.headsUpRoot().state()));
        System.out.println(index);
    }

    private void add(AbstractionDomainBuilder<BasicActionView> domainBuilder, StateTree.Node node) {
        ActionState state = node.state();
        BasicActionView view = new BasicActionView(state);

        domainBuilder.add(view);

        for (StateTree.Node sub : node.acts().values()) {
            add(domainBuilder, sub);
        }
    }
}
