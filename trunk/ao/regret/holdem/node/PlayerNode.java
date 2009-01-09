package ao.regret.holdem.node;

import ao.holdem.model.act.AbstractAction;
import ao.regret.InfoNode;

/**
 * Date: Jan 8, 2009
 * Time: 3:22:59 PM
 */
public interface PlayerNode extends InfoNode
{
    public InfoNode child(AbstractAction forAction);
}
