package ao.holdem.history.state;

import ao.holdem.def.state.env.RealAction;
import ao.holdem.history.PlayerHandle;

import java.util.List;

/**
 *
 */
public class RunningState
{
    //--------------------------------------------------------------------
    private HoldemState endState;
    private EventStream events = new EventStream();
    private CardSource  cards  = new DeckCardSource();


    //--------------------------------------------------------------------
    private RunningState() {}

    public RunningState(
            List<PlayerHandle> clockwiseDealerLast)
    {
        endState = new HoldemState(clockwiseDealerLast);
    }
    public RunningState(
            List<PlayerHandle> clockwiseDealerLast,
            PlayerHandle       smallBlind,
            PlayerHandle       bigBlind)
    {
        endState = new HoldemState(clockwiseDealerLast,
                                   smallBlind, bigBlind);
    }


    //--------------------------------------------------------------------
    public CardSource cards()
    {
        return cards;
    }


    //--------------------------------------------------------------------
    public void advance(RealAction act)
    {
        endState = endState.advance(act);

        RealEvent event = new RealEvent();
    }


    //--------------------------------------------------------------------
    public HoldemState head()
    {
        return endState;
    }


    //--------------------------------------------------------------------
    public RunningState prototype()
    {
        RunningState proto = new RunningState();
        proto.endState = endState;
        proto.events   = events;
        proto.cards    = cards.prototype();
        return proto;
    }
}
