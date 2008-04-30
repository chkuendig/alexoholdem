package ao.regret.node;

import ao.holdem.model.act.AbstractAction;

import java.util.Map;

/**
 * 
 */
public class OpponentNode extends PlayerNode
{
    private Map<AbstractAction, InfoNode> kids;
}
