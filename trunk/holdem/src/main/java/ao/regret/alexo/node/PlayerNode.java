package ao.regret.alexo.node;

import ao.regret.InfoNode;
import ao.simple.alexo.AlexoAction;

/**
 *
 */
public interface PlayerNode extends InfoNode
{
    public InfoNode child(AlexoAction forAction);
}