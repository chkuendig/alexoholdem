package ao.holdem.history.irc;

import ao.holdem.history.HandHistory;
import ao.holdem.history.PlayerHandle;
import ao.holdem.history.persist.PlayerHandleLookup;
import ao.holdem.history.state.StatePlayer;
import ao.holdem.history.state.RunningState;
import ao.holdem.history_game.RealDealer;

import java.util.*;

/**
 * Hand History Iterator
 */
public class HandItr implements Iterable<HandHistory>
{
    //--------------------------------------------------------------------
    private Map<String, List<IrcAction>> players;
    private Map<Long, IrcRoster>         rosters;
    private List<IrcHand>                hands;

    private HandHistory nextHistory;
    private int         nextHandIndex;

    private PlayerHandleLookup playerLookup;



    //--------------------------------------------------------------------
    public HandItr(Map<String, List<IrcAction>> players,
                   Map<Long, IrcRoster>         rosters,
                   List<IrcHand>                hands,
                   PlayerHandleLookup           playerLookup)
    {
        init(players, rosters, hands);
        this.playerLookup = playerLookup;
    }


    //--------------------------------------------------------------------
    private void init(Map<String, List<IrcAction>> players,
                      Map<Long, IrcRoster>         rosters,
                      List<IrcHand>                hands)
    {
        nextHandIndex = 0;
        this.players = players;
        this.rosters = rosters;
        this.hands   = hands;

        computeNextHistory();
    }


    //--------------------------------------------------------------------
    private void computeNextHistory()
    {
        for (; nextHandIndex < hands.size();
               nextHandIndex++)
        {
            nextHistory = computeHistory(hands.get( nextHandIndex ));
            if (nextHistory != null) break;
        }
    }

    private HandHistory computeHistory(IrcHand hand)
    {
        IrcRoster roster = rosters.get( hand.timestamp() );

        List<String>    names  = Arrays.asList(roster.names());
        List<IrcAction> action = handAction(names, hand.timestamp());
        assert action != null;
        assert roster.size() == action.size();

        sortByPosition(names, action);

        LiteralCardSource cards = new LiteralCardSource();
        cards.setCommunity( hand.community() );

        List<PlayerHandle> playerHandles = new ArrayList<PlayerHandle>();
        Map<PlayerHandle, StatePlayer> brains =
                new HashMap<PlayerHandle, StatePlayer>();
        for (int i = 0; i < names.size(); i++)
        {
            String       name   = names.get(i);
            PlayerHandle handle = ircPlayer(name);
            IrcAction    acts   = action.get(i);

            playerHandles.add(handle);
            brains.put(handle, new IrcPlayer(acts));
            cards.putHole(handle, acts.holes());
        }

        RunningState start  = new RunningState(playerHandles, cards);
        RealDealer   dealer = new RealDealer(start, brains);
        return dealer.playOutHand().toHistory();
    }


    //--------------------------------------------------------------------
    private void sortByPosition(
            List<String> names, List<IrcAction> action)
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
            Collections.swap(names,   i, originalIndex);
        }
    }


    //--------------------------------------------------------------------
    private List<IrcAction> handAction(
            List<String> names, long timestamp)
    {
        List<IrcAction> action = new ArrayList<IrcAction>();

        roster_names:
        for (String name : names)
        {
            List<IrcAction> playerAction = players.get( name );
            if (playerAction == null) return null;

            for (IrcAction singleAction : playerAction)
            {
                if (singleAction.timestamp() == timestamp)
                {
                    action.add( singleAction );
                    continue roster_names;
                }
            }
            return null;
        }

        return action;
    }


    //--------------------------------------------------------------------
    public Iterator<HandHistory> iterator()
    {
        return new Iterator<HandHistory>() {
            public boolean hasNext() {
                return (nextHistory != null);
            }

            public HandHistory next() {
                HandHistory next = nextHistory;
                computeNextHistory();
                return next;
            }

            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }


    //--------------------------------------------------------------------
    private PlayerHandle ircPlayer(String name)
    {
        return playerLookup.lookup("irc", name);
    }
}
