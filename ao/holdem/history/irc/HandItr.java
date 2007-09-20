package ao.holdem.history.irc;

import ao.persist.HandHistory;
import ao.persist.PlayerHandle;
import ao.persist.dao.PlayerHandleLookup;
import ao.holdem.history.state.HoldemRuleBreach;
import ao.holdem.history.state.RunningState;
import ao.holdem.history.state.StatePlayer;
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
        this.playerLookup = playerLookup;
        init(players, rosters, hands);
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
        nextHistory = null;
        while (nextHandIndex < hands.size())
        {
            System.out.println("nextHandIndex " + nextHandIndex);
            nextHistory = computeHistory(hands.get( nextHandIndex++ ));
            if (nextHistory != null) break;
        }
    }

    private HandHistory computeHistory(IrcHand hand)
    {
        IrcRoster roster = rosters.get( hand.timestamp() );

        List<String>    names  = Arrays.asList(roster.names());
        List<IrcAction> action = handAction(names, hand.timestamp());
        if (action == null) return null;
        assert roster.size() == action.size();

        sortByPosition(names, action);
        sizeUpBlinds(action);
//        displayHand(hand, action);

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
        try
        {
            RunningState out    = dealer.playOutHand();
//            System.out.println("winners: " +
//                           Arrays.deepToString(out.winners().toArray()));
            return out.toHistory();
        }
        catch (HoldemRuleBreach e)
        {
            System.out.println(e.getMessage());
            displayHand(hand, action);
            return null;
        }
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


    //--------------------------------------------------------------------
    private void sizeUpBlinds(List<IrcAction> action)
    {
        int firstBlindIndex  = -1;
        int secondBlindIndex = -1;
        for (int i = 0; i < action.size(); i++)
        {
            if (action.get(i).preFlop()[0].isBlind())
            {
                if (firstBlindIndex == -1)
                {
                    firstBlindIndex = i;
                }
                else
                {
                    secondBlindIndex = i;
                    break;
                }
            }
        }

        if (secondBlindIndex == -1)
        {
            action.get( firstBlindIndex ).growBlind();
        }
        else
        {
            action.get( firstBlindIndex ).shrinkBlind();
            action.get( secondBlindIndex ).growBlind();
        }
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
//                throw new Error("can't find " + name +
//                                " among " + players.keySet());

            for (IrcAction singleAction : playerAction)
            {
                if (singleAction.timestamp() == timestamp)
                {
                    action.add( singleAction );
                    continue roster_names;
                }
            }

            throw new Error("can't find timestamp " + timestamp +
                            " among " + playerAction);
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
