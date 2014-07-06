package ao.holdem.ai.abs.act;


import ao.holdem.engine.state.ActionState;
import ao.holdem.model.Round;

public interface ActionAbstraction
{
    int indexInRound(ActionState state);

    int count(Round round);
}
