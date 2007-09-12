package ao.holdem.history.state;

import ao.holdem.def.state.env.RealAction;
import ao.holdem.history.PlayerHandle;

import java.util.List;

/**
 *
 */
public class CumulativeState
{
    //--------------------------------------------------------------------
    private HoldemState endState;
    private EventStream events = new EventStream();
    private CardSource  cards  = new DeckCardSource();


    //--------------------------------------------------------------------
    public CumulativeState(
            List<PlayerHandle> clockwiseDealerLast)
    {
        endState = new HoldemState(clockwiseDealerLast);
    }
    public CumulativeState(
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
    public CumulativeState prototype()
    {
        return null;
    }
}
