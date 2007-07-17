package ao.holdem.history;

import ao.holdem.history.persist.Base;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


/**
 *
 */
@Entity
public class PlayerHandle extends Base
{
    //----------------------------------------------------------
    public PlayerHandle() {}
    public PlayerHandle(String name)
    {
        setName( name );
    }


    //----------------------------------------------------------
    private String name;

    public String getName()            { return name;      }
    public void   setName(String name) { this.name = name; }


    //----------------------------------------------------------
    private List<HandHistory> hands =
            new ArrayList<HandHistory>();

    @ManyToMany(
        targetEntity=HandHistory.class,
        cascade={CascadeType.PERSIST, CascadeType.MERGE}
    )
    @JoinTable(
        name="PLAYER_HAND",
        joinColumns={@JoinColumn(name="PLAYER_ID")},
        inverseJoinColumns={@JoinColumn(name="HAND_ID")}
    )
    public List<HandHistory> getHands()
    {
        return hands;
    }

    public void setHands(List<HandHistory> hands)
    {
        this.hands = hands;
    }
    
    public void addHand(HandHistory hand)
    {
        getHands().add( hand );
        hand.getPlayers().add( this );
    }


    //----------------------------------------------------------
    private List<HandHistory> handsWon =
            new ArrayList<HandHistory>();

    @ManyToMany(
        targetEntity=HandHistory.class,
        cascade={CascadeType.PERSIST, CascadeType.MERGE}
    )
    @JoinTable(
        name="PLAYER_HAND_WIN",
        joinColumns={@JoinColumn(name="PLAYER_ID")},
        inverseJoinColumns={@JoinColumn(name="HAND_ID")}
    )
    public List<HandHistory> getHandsWon()
    {
        return handsWon;
    }

    public void setHandsWon(List<HandHistory> handsWon)
    {
        this.handsWon = handsWon;
    }

    public void addHandWon(HandHistory hand)
    {
        getHandsWon().add( hand );
        hand.getPlayers().add( this );
    }
}
