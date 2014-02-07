package ao.holdem.ai.ai.regret.khun.node;

import ao.holdem.ai.ai.regret.InfoNode;
import ao.holdem.ai.ai.simple.kuhn.KuhnAction;

/**
 * 
 */
public interface PlayerNode extends InfoNode
{
    public InfoNode child(KuhnAction forAction);
}
