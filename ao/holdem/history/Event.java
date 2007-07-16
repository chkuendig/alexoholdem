package ao.holdem.history;

import ao.holdem.def.state.domain.BettingRound;
import ao.holdem.def.state.env.TakenAction;
import ao.holdem.history.persist.Base;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

/**
 *
 */
@Entity
public class Event extends Base
{
    //----------------------------------------------------------
    public Event() {}

    public Event(PlayerHandle player,
                 BettingRound round,
                 TakenAction action)
    {
        setPlayer( player );
        setRound(  round  );
        setAction( action );
    }


    //----------------------------------------------------------
    private PlayerHandle player;

    public PlayerHandle getPlayer()
    {
        return player;
    }
    
    public void setPlayer(PlayerHandle player)
    {
        this.player = player;
    }


    //----------------------------------------------------------
    private TakenAction action;

    public TakenAction getAction()
    {
        return action;
    }

    public void setAction(TakenAction action)
    {
        this.action = action;
    }


    //----------------------------------------------------------
    private BettingRound round;

    public BettingRound getRound()
    {
        return round;
    }

    public void setRound(BettingRound round)
    {
        this.round = round;
    }


    //----------------------------------------------------------
    private HandHistory hand;

    @ManyToOne
    @JoinColumn(name="hand_fk")
    public HandHistory getHand()
    {
        return hand;
    }

    public void setHand(HandHistory hand)
    {
        this.hand = hand;
    }
}
