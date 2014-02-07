package ao.holdem.ai.ai.regret.alexo.node;

import ao.holdem.ai.ai.regret.InfoNode;
import ao.holdem.ai.ai.simple.alexo.AlexoAction;

/**
 *
 */
public interface PlayerNode extends InfoNode
{
    public InfoNode child(AlexoAction forAction);
}