package ao.holdem.history;

import ao.holdem.history.persist.Base;
import org.hibernate.annotations.Index;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;


/**
 *
 */
@Entity
public class PlayerHandle extends Base
{
    //--------------------------------------------------------------------
    public PlayerHandle() {}
    public PlayerHandle(String domain, String name)
    {
        setDomain( domain );
        setName(   name   );
    }


    //--------------------------------------------------------------------
    private String name;

    @Index(name="name_index")
    public String getName()            { return name;      }
    public void   setName(String name) { this.name = name; }


    //--------------------------------------------------------------------
    private String domain;

    @Index(name="domain_index")
    public String getDomain()              { return domain;        }
    public void   setDomain(String domain) { this.domain = domain; }


    //--------------------------------------------------------------------
    private List<Event> events = new ArrayList<Event>();

    @OneToMany(mappedBy = "player",
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


    //--------------------------------------------------------------------
    private List<HandHistory> hands =
            new ArrayList<HandHistory>();

    @ManyToMany(
        targetEntity=HandHistory.class,
        mappedBy = "players",
        fetch    = FetchType.LAZY)
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

    
    //--------------------------------------------------------------------
    @Override
    public String toString()
    {
        return name;
    }
}
