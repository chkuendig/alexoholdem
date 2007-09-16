package ao.holdem.history;

import ao.holdem.def.state.domain.BettingRound;
import ao.holdem.def.state.env.RealAction;
import ao.holdem.def.state.env.TakenAction;
import ao.holdem.history.persist.Base;

import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;

/**
 *
 */
@Entity
public class Event extends Base
{
    //--------------------------------------------------------------------
    public Event() {}

    public Event(PlayerHandle player,
                 BettingRound round,
                 RealAction   action)
    {
        setPlayer( player );
        setRound(  round  );
        setAction( action );
    }


    //--------------------------------------------------------------------
    private PlayerHandle player;

    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name="player_id")
    public PlayerHandle getPlayer()
    {
        return player;
    }
    public void setPlayer(PlayerHandle player)
    {
        this.player = player;
    }


    //--------------------------------------------------------------------
//    private HandHistory hand;
//
//    @ManyToOne(fetch = FetchType.LAZY)
//    public HandHistory getHand()
//    {
//        return hand;
//    }
//    public void setHand(HandHistory hand)
//    {
//        this.hand = hand;
//    }


    //--------------------------------------------------------------------
    private RealAction action;

    @Enumerated
    public RealAction getAction()
    {
        return action;
    }
    public void setAction(RealAction action)
    {
        this.action = action;
    }

    public TakenAction takenAction()
    {
        return getAction().toTakenAction();
    }


    //--------------------------------------------------------------------
    private BettingRound round;

    @Enumerated
    public BettingRound getRound()
    {
        return round;
    }
    public void setRound(BettingRound round)
    {
        this.round = round;
    }


    //--------------------------------------------------------------------
    @Override
    public String toString()
    {
        return getPlayer() + "\t" +
               getAction() + "\t" +
               getRound();
    }
}
