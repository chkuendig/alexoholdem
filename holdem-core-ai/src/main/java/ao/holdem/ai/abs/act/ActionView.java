package ao.holdem.ai.abs.act;

import ao.holdem.engine.state.ActionState;

import java.io.Serializable;

public interface ActionView<T extends Serializable>
{
    T view(ActionState actionState);
}
