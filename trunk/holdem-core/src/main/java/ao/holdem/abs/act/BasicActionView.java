package ao.holdem.abs.act;


import ao.holdem.engine.state.ActionState;
import ao.holdem.model.Round;

import java.io.Serializable;

public class BasicActionView implements Serializable
{
    private static final long serialVersionUID = 20140701L;


    public static ActionView<BasicActionView> VIEW = new ActionView<BasicActionView>() {
        @Override public BasicActionView view(ActionState actionState) {
            return new BasicActionView(actionState);
        }};



    public final Round round;
    public final int nextToAct;
    public final double callPrice;
    public final double raisePrice;
//    public final ActionState state;


    public BasicActionView(ActionState state)
    {
        round = state.round();
        nextToAct = state.nextToActIndex();

        callPrice = (double) state.toCall().smallBlinds() / state.stakes().smallBlinds();

        if (state.canRaise()) {
            raisePrice = (double) state.betSize().bigBlinds() / state.stakes().smallBlinds();
        } else {
            raisePrice = -1;
        }

//        this.state = state;
    }
}
