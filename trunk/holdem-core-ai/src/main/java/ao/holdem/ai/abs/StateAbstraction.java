package ao.holdem.ai.abs;


import ao.holdem.engine.state.ActionState;
import ao.holdem.model.card.sequence.CardSequence;

public interface StateAbstraction
{
    int indexOf(ActionState actionState, CardSequence cardState);

    int size();
}
