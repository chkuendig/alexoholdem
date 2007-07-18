package ao.holdem.history;

import ao.holdem.def.model.card.Card;
import ao.holdem.def.model.cards.Hole;
import ao.holdem.def.model.cards.Community;
import ao.holdem.history.persist.Base;
import org.hibernate.annotations.CollectionOfElements;
import org.hibernate.annotations.IndexColumn;
import org.hibernate.annotations.MapKeyManyToMany;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import java.util.*;

/**
 *
 */
@Entity
public class HandHistory extends Base
{
    //--------------------------------------------------------------------
    private transient List<PlayerHandle> tempPlayers =
            new ArrayList<PlayerHandle>();


    //--------------------------------------------------------------------
    public HandHistory() {}
//    public HandHistory(List<BotHandle> botHandles)
//    {
//        for (BotHandle botHandle : botHandles)
//        {
//            addPlayer( botHandle.handle() );
//        }
//    }
    public HandHistory(Collection<PlayerHandle> players)
    {
        for (PlayerHandle playerHandle : players)
        {
            addPlayer( playerHandle );
        }
    }


    //--------------------------------------------------------------------
    private List<PlayerHandle> players =
            new ArrayList<PlayerHandle>();

    @ManyToMany(
        cascade={CascadeType.PERSIST, CascadeType.MERGE},
//        mappedBy="hands",
        targetEntity = PlayerHandle.class)
    @IndexColumn(name = "position", base = 0)
    public List<PlayerHandle> getPlayers()
    {
        return (tempPlayers.isEmpty())
                ? players
                : tempPlayers;
    }

    public void setPlayers(List<PlayerHandle> players)
    {
        this.players = players;
    }

    //in order of cards received
    public void addPlayer(PlayerHandle player)
    {
        tempPlayers.add( player );
    }
    
    public void commitHandToPlayers()
    {
        List<PlayerHandle> tempPlayerBuffer =
                new ArrayList<PlayerHandle>(tempPlayers);
        tempPlayers.clear();
        for (PlayerHandle player : tempPlayerBuffer)
        {
            player.addHand( this );
        }
    }


    //--------------------------------------------------------------------
    private List<PlayerHandle> winners =
            new ArrayList<PlayerHandle>();

    @ManyToMany(
        cascade={CascadeType.PERSIST, CascadeType.MERGE},
        mappedBy="handsWon",
        targetEntity=PlayerHandle.class)
    public List<PlayerHandle> getWinners()
    {
        return winners;
    }

    public void setWinners(List<PlayerHandle> winners)
    {
        this.winners = winners;
    }

    public void addWinner(PlayerHandle winner)
    {
        winner.addHandWon( this );
    }


    //--------------------------------------------------------------------
    private List<Event> events = new ArrayList<Event>();

    @OneToMany(mappedBy = "hand")
    @IndexColumn(name   = "hand_index")
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
        event.setHand( this );
    }


    //--------------------------------------------------------------------
    private Map<PlayerHandle, Hole> holes =
            new HashMap<PlayerHandle, Hole>();

    @CollectionOfElements(targetElement = Hole.class)
    @MapKeyManyToMany(targetEntity = PlayerHandle.class)
    public Map<PlayerHandle, Hole> getHoles()
    {
        return holes;
    }

    public void setHoles(Map<PlayerHandle, Hole> holes)
    {
        this.holes = holes;
    }

    public void addHole(PlayerHandle player, Hole hole)
    {
        getHoles().put(player, hole);
    }


    //--------------------------------------------------------------------
    private Community community;

    public Community getCommunity()
    {
        return community;
    }
    public void setCommunity(Community community)
    {
        this.community = community;
    }

    public void dealFlop(Community flop)
    {
        setCommunity(flop);
    }
    public void dealTurn(Card turn)
    {
        setCommunity(getCommunity().addTurn(turn));
    }
    public void dealRiver(Card river)
    {
        setCommunity(getCommunity().addRiver(river));
    }


    //--------------------------------------------------------------------
    public Snapshot snapshot(Event asOf)
    {
        List<Event> toCapture = new ArrayList<Event>();

        for (Event event : getEvents())
        {
            toCapture.add( event );
            if (event.equals(asOf))
            {
                break;
            }
        }

        return new Snapshot(getPlayers(), toCapture);
    }
}

