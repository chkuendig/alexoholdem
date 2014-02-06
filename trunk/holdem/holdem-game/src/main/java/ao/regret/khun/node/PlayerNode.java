package ao.regret.khun.node;

import ao.regret.InfoNode;
import ao.simple.kuhn.KuhnAction;

/**
 * 
 */
public interface PlayerNode extends InfoNode
{
    public InfoNode child(KuhnAction forAction);
}
