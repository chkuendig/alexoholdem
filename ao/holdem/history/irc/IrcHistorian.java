package ao.holdem.history.irc;

import ao.holdem.def.model.cards.Hole;
import ao.holdem.def.state.domain.BettingRound;
import ao.holdem.def.state.env.TakenAction;
import ao.holdem.history.Event;
import ao.holdem.history.HandHistory;
import ao.holdem.history.PlayerHandle;
import ao.holdem.history.Snapshot;
import ao.holdem.history.persist.PlayerHandleLookup;
import ao.holdem.history_game.Dealer;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.wideplay.warp.persist.Transactional;
import org.hibernate.Session;

import java.io.File;
import java.util.*;

/**
 *
 */
public class IrcHistorian
{
    //--------------------------------------------------------------------
    @Inject PlayerHandleLookup players;
    @Inject Provider<Session>  session;

    @Inject Provider<Dealer> dealerProvider;


    //--------------------------------------------------------------------
    public List<HandHistory> fromSnapshot(String dirName)
    {
        IrcReader r = new IrcReader(new File(dirName));

        Map<PlayerHandle, List<IrcAction>> players = groupPlayers(r);
        Map<Long, IrcRoster>               rosters = groupRosters(r);

        int count = 0;
        List<HandHistory> histories = new ArrayList<HandHistory>();
        for (IrcHand hand : r.hands())
        {
            if (hand.numberOfPlayers() <= 2 ||
                    hand.numberOfPlayers() > 10) continue;

            count++;
            HandHistory hist = toHistory(hand, players, rosters, false);
            if (hist != null)
            {
                histories.add( hist );
            }
            else
            {
                System.out.println(count);
                toHistory(hand, players, rosters, true);
            }
        }
        return histories;
    }

    private HandHistory toHistory(
            IrcHand                            hand,
            Map<PlayerHandle, List<IrcAction>> playerActions,
            Map<Long, IrcRoster>               rosters,
            boolean                            forDisplay)
    {
        IrcRoster          roster  = rosters.get(hand.timestamp());
//        System.out.println(roster);
        List<PlayerHandle> handles = handHandles(roster);
        if (handles == null) return null;
        List<IrcAction> action =
                gameAction(playerActions, handles, hand.timestamp());
        if (action == null ||
            handles.size() != action.size()) return null;

        sortByPosition(handles, action);
        try
        {
            if (forDisplay)
            {
                displayHand(hand, action);
                return null;
            }
            else
            {
                return addHistory(hand, handles, action);
            }
        }
        catch (BadHandException e)
        {
            return null;
        }
    }

    private void sortByPosition(
            List<PlayerHandle> handles,
            List<IrcAction>    action)
    {
        List<IrcAction> sortedAction = new ArrayList<IrcAction>(action);
        Collections.sort(sortedAction, new Comparator<IrcAction>() {
            public int compare(IrcAction a, IrcAction b) {
                return Double.compare(a.position(), b.position());
            }
        });

        for (int i = 0; i < action.size(); i++)
        {
            int originalIndex = action.indexOf( sortedAction.get(i) );

            Collections.swap(action,  i, originalIndex);
            Collections.swap(handles, i, originalIndex);
        }
    }


    //--------------------------------------------------------------------
    @Transactional(rollbackOn = BadHandException.class)
    protected HandHistory addHistory(
            IrcHand            hand,
            List<PlayerHandle> handles,
            List<IrcAction>    action) throws BadHandException
    {
        for (PlayerHandle p : handles)
        {
            session.get().saveOrUpdate( p );
        }

        HandHistory hist = new HandHistory(handles);
        session.get().saveOrUpdate( hist );

        addHoles(hist, handles, action);
        session.get().saveOrUpdate( hist );

        hist.setCommunity( hand.community() );
        session.get().saveOrUpdate(hist);

        Snapshot s = addEvents(hist, action);
        if (s == null) throw new BadHandException();
        if (!Dealer.assignDeltas(hist, s)) throw new BadHandException();

        hist.commitHandToPlayers();
        session.get().saveOrUpdate(hist);

        return hist;
    }

    private void displayHand(
            IrcHand         hand,
            List<IrcAction> action)
    {
        System.out.println(hand);
        for (IrcAction act : action)
        {
            System.out.println(act);
        }
    }


    private Snapshot addEvents(
            HandHistory     hist,
            List<IrcAction> action)
    {
        List<Map<BettingRound, List<TakenAction>>> actionStack =
                stackActions(hist, action);

        PlayerHandle firstToAct =
                hist.snapshot().nextToAct();
        int fistToActIndex =
                hist.getPlayers().indexOf( firstToAct );

        for (BettingRound round : BettingRound.values())
        {
            int firstOfRound =
                    (round == BettingRound.PREFLOP)
                    ? fistToActIndex
                    : 0;

            boolean actionPerformed;
            do
            {
                actionPerformed = false;
                for (int i = firstOfRound; i < action.size(); i++)
                {
                    List<TakenAction> roundActions =
                            actionStack.get(i).get(round);
                    if (roundActions != null &&
                            !roundActions.isEmpty())
                    {
                        Event e = hist.addEvent(
                                    hist.getPlayers().get(i),
                                    round,
                                    roundActions.remove(0));
                        session.get().save(e);
                        actionPerformed = true;
                    }
                }
                firstOfRound = 0;
            }
            while (actionPerformed);
        }

        // varify
        return hist.snapshot(
                hist.getEvents().get(
                        hist.getEvents().size() - 1));
    }

    private List<Map<BettingRound, List<TakenAction>>> stackActions(
            HandHistory     hist,
            List<IrcAction> action)
    {
        List<Map<BettingRound, List<TakenAction>>> actionStack =
                new ArrayList<Map<BettingRound, List<TakenAction>>>();

        for (int i = 0; i < hist.getPlayers().size(); i++)
        {
            Map<BettingRound, List<TakenAction>> stack =
                    new HashMap<BettingRound, List<TakenAction>>();
            actionStack.add(stack);

            IrcAction toStack = action.get(i);
            betting_round:
            for (BettingRound round : BettingRound.values())
            {
                stack.put(round, new ArrayList<TakenAction>());
                for (TakenAction act : toStack.action(round))
                {
                    stack.get(round).add(act);
                    if (act == TakenAction.FOLD)
                    {
                        break betting_round;
                    }
                }
            }
        }
        return actionStack;
    }

    private void addHoles(HandHistory        hist,
                          List<PlayerHandle> handles,
                          List<IrcAction>    action)
    {
        for (int i = 0; i < handles.size(); i++)
        {
            PlayerHandle handle = handles.get(i);
            IrcAction    act    = action.get(i);
            Hole         hole   = act.holes();

            hist.addHole(handle, hole);
        }
    }

    private List<IrcAction> gameAction(
            Map<PlayerHandle, List<IrcAction>> playerActions,
            List<PlayerHandle>                 handles,
            long                               timestamp)
    {
        List<IrcAction> action = new ArrayList<IrcAction>();

        roster_handles:
        for (PlayerHandle handle : handles)
        {
            List<IrcAction> playerAction =
                    playerActions.get( handle );
            if (playerAction == null) return null;

            for (IrcAction singleAction : playerAction)
            {
                if (singleAction.timestamp() == timestamp)
                {
                    action.add( singleAction );
                    continue roster_handles;
                }
            }
            return null;
        }

        return action;
    }
    private List<PlayerHandle> handHandles(IrcRoster roster)
    {
        List<PlayerHandle> handles = new ArrayList<PlayerHandle>();
        for (String name : roster.names())
        {
            PlayerHandle handle = ircPlayer(name);
            if (handle == null) return null;
            handles.add( handle );
        }
        return handles;
    }


    
    //--------------------------------------------------------------------
    private Map<PlayerHandle, List<IrcAction>> groupPlayers(
            IrcReader r)
    {
        Map<PlayerHandle, List<IrcAction>> players =
                new HashMap<PlayerHandle, List<IrcAction>>();

        Map<String, PlayerHandle> playerCache =
                new HashMap<String, PlayerHandle>();
        for (IrcAction playerAction : r.actions())
        {
            PlayerHandle handle = playerCache.get(playerAction.name());
            if (handle == null)
            {
                handle = ircPlayer( playerAction.name() );
                playerCache.put(playerAction.name(), handle);
            }
            
            retrieveOrCreate(players, handle).add( playerAction );
        }

        return players;
    }

    private List<IrcAction> retrieveOrCreate(
            Map<PlayerHandle, List<IrcAction>> map,
            PlayerHandle                       key)
    {
        List<IrcAction> values = map.get(key);
        if (values == null)
        {
            values = new ArrayList<IrcAction>();
            map.put(key, values);
        }
        return values;
    }

    private PlayerHandle ircPlayer(String name)
    {
        return players.lookup("irc", name);
    }


    //--------------------------------------------------------------------
    private Map<Long, IrcRoster> groupRosters(
            IrcReader r)
    {
        Map<Long, IrcRoster> rosters =
                new HashMap<Long, IrcRoster>();

        for (IrcRoster roster : r.roster())
        {
            Long timestamp = roster.timestamp();
            rosters.put(timestamp, roster);
        }

        return rosters;
    }
}
