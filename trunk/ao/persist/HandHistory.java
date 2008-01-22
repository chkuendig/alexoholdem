package ao.persist;

import ao.holdem.model.BettingRound;
import ao.holdem.model.card.Card;
import ao.holdem.model.card.Community;
import ao.holdem.model.card.Hole;
import ao.holdem.model.Money;
import ao.holdem.model.act.RealAction;
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
            for (Event event : getEvents(player))
            {
                player.addEvent( event );
            }
        }
    }


    //--------------------------------------------------------------------
    private Map<PlayerHandle, Money> deltas =
            new HashMap<PlayerHandle, Money>();

    @SuppressWarnings({"JpaModelErrorInspection"})
    @CollectionOfElements(targetElement = Money.class)
    @MapKeyManyToMany(targetEntity = PlayerHandle.class)
    public Map<PlayerHandle, Money> getDeltas()
    {
        return deltas;
    }

    public void setDeltas(Map<PlayerHandle, Money> deltas)
    {
        this.deltas = deltas;
    }

    public void setDelta(PlayerHandle player, Money delta)
    {
        getDeltas().put(player, delta);
    }


    //--------------------------------------------------------------------
    private List<Event> events = new ArrayList<Event>();

    @OneToMany(
//            fetch = FetchType.EAGER,
            cascade={CascadeType.PERSIST, CascadeType.MERGE})
//    @JoinColumn(name="HAND_ID")
    @IndexColumn(name="EVENT_INDEX")
    public List<Event> getEvents()
    {
        return events;
    }
    public void setEvents(List<Event> events)
    {
        this.events = events;
    }
    public Event addEvent(PlayerHandle player,
                          BettingRound round,
                          RealAction   action)
    {
        Event e = new Event(player, round, action);
        addEvent( e );
        return e;
    }
    public void addEvent(Event event)
    {
        //event.getPlayer().addEvent( event );
        getEvents().add( event );
    }

    public List<Event> getEvents(PlayerHandle forPlayer)
    {
        List<Event> playerEvents = new ArrayList<Event>();
        for (Event e : getEvents())
        {
            if (e.getPlayer().equals( forPlayer ))
            {
                playerEvents.add( e );
            }
        }
        return playerEvents;
    }


    //--------------------------------------------------------------------
    private Map<PlayerHandle, Hole> holes =
            new HashMap<PlayerHandle, Hole>();

    @SuppressWarnings({"JpaModelErrorInspection"})
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
        if (hole == null || hole.incomplete()) return;
        getHoles().put(player, hole);
    }

    public boolean holesVisible(PlayerHandle forPlayer)
    {
        Hole hole = getHoles().get( forPlayer );
        return (hole != null && hole.bothCardsVisible());
    }


    //--------------------------------------------------------------------
    private Community community = new Community();

    public Community getCommunity()
    {
        return community;
    }
    public void setCommunity(Community community)
    {
        this.community =
                (community == null)
                ? new Community()
                : community;
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
//    public Snapshot snapshot()
//    {
//        return snapshot(null);
//    }
//    public Snapshot nextToActSnapshot()
//    {
//        return snapshot( getEvents().get(getEvents().size() - 1) );
//    }
//    public Snapshot snapshot(Event asOf)
//    {
//        List<Event> toCapture = new ArrayList<Event>();
//
//        if (asOf != null)
//        {
//            for (Event event : getEvents())
//            {
//                toCapture.add( event );
//                if (event.equals(asOf))
//                {
//                    break;
//                }
//            }
//        }
//
//        Snapshot s = new Snapshot(getPlayers());
//        return s.populate( toCapture )
//                ? s : null;
//    }


    //--------------------------------------------------------------------
//    public HoldemRetroSet casesFor(PlayerHandle p)
//    {
//        return new HandParser().casesFor(this, p);
//    }
//    public PredictionContext nextToActContext()
//    {
//        return new HandParser().nextToActContext(
//                this, nextToActSnapshot().nextToAct());
//    }


    //--------------------------------------------------------------------
    public String summary()
    {
        StringBuilder str = new StringBuilder();
        for (PlayerHandle player : getPlayers())
        {
            if (str.length() != 0) str.append('\n');

            BettingRound round = BettingRound.PREFLOP;
            str.append(player.getName()).append("\t");
            for (Event e : getEvents(player))
            {
                if (e.getRound() != round)
                {
                    str.append("\t");
                    round = e.getRound();
                }
                str.append( e.getAction().toString().substring(0, 2) );
            }
        }
        return str.toString();
    }

    public String toString()
    {
        return "History for: " + getPlayers().toString();
    }
}

