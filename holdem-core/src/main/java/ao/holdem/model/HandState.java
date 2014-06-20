package ao.holdem.model;


import ao.holdem.engine.state.ActionState;
import ao.holdem.engine.state.HandStateUtils;
import ao.holdem.model.card.CardState;

import java.util.List;

public class HandState
{
    private final ActionState actionState;
    private final CardState cardState;


    public HandState(ActionState actionState, CardState cardState) {
        if (actionState == null) {
            throw new NullPointerException("ActionState must not be null");
        }
        if (cardState == null) {
            throw new NullPointerException("CardState must not be null");
        }

        this.actionState = actionState;
        this.cardState = cardState;
    }


    public ActionState actionState() {
        return actionState;
    }

    public CardState cardState() {
        return cardState;
    }


    public HandState withCardState(CardState cardState) {
        return new HandState(actionState, cardState);
    }


    public List<ChipStack> terminalOutcome() {
        return HandStateUtils.terminalOutcome(
                actionState, cardState);
    }


    @Override
    public String toString() {
        return actionState + " | " + cardState;
    }
}
