package ao.holdem.ai.abs.act;


import ao.holdem.engine.state.ActionState;
import ao.holdem.model.Round;

import java.io.Serializable;

public class SmallActionView implements Serializable
{
    private static final long serialVersionUID = 20140724L;


    public static ActionView<SmallActionView> VIEW = new ActionView<SmallActionView>() {
        @Override public SmallActionView view(ActionState actionState) {
            return new SmallActionView(actionState);
        }};



    public final Round round;
    public final int nextToAct;
    public final boolean canCheck;
    public final boolean canRaise;


    public SmallActionView(ActionState state)
    {
        round = state.round();
        nextToAct = state.nextToActIndex();
        canCheck = state.canCheck();
        canRaise = state.canRaise();
    }
}
