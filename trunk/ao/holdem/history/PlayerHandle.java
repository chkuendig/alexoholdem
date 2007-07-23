package ao.holdem.history;

import ao.holdem.history.persist.Base;

import javax.persistence.*;
import java.util.ArrayList;
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
    private List<Event> events = new ArrayList<Event>();

    @OneToMany(cascade  = CascadeType.ALL,
               mappedBy = "player",
               fetch    = FetchType.LAZY)
    public List<Event> getEvents()
    {
        return events;
    }
    public void setEvents(List<Event> events)
    {
        this.events = events;
    }
    public void addEvent(Event event)
    {
        getEvents().add( event );
        event.setPlayer( this );
    }

    

    //----------------------------------------------------------
    private List<HandHistory> hands =
            new ArrayList<HandHistory>();

    @ManyToMany(
        targetEntity=HandHistory.class,
        mappedBy = "players"
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
}
