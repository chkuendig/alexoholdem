package ao.irc;

import ao.holdem.engine.Player;
import ao.holdem.engine.dealer.Dealer;
import ao.holdem.model.Avatar;
import ao.holdem.model.act.Action;
import ao.holdem.model.card.Hole;
import ao.holdem.model.card.chance.LiteralCards;
import ao.holdem.model.replay.Replay;
import ao.holdem.model.replay.StackedReplay;
import org.apache.log4j.Logger;

import java.util.*;

/**
 * Hand History Iterator
 */
public class IrcHandItr implements Iterable<Replay>
{
    //--------------------------------------------------------------------
    private static final Logger LOG =
            Logger.getLogger(IrcHandItr.class);


    //--------------------------------------------------------------------
    private Map<String, List<IrcAction>> players;
    private Map<Long, IrcRoster>         rosters;
    private List<IrcHand>                hands;

    private Replay nextHistory;
    private int    nextHandIndex;


    //--------------------------------------------------------------------
    public IrcHandItr(Map<String, List<IrcAction>> players,
                      Map<Long, IrcRoster> rosters,
                      List<IrcHand> hands)
    {
        init(players, rosters, hands);
    }


    //--------------------------------------------------------------------
    private void init(Map<String, List<IrcAction>> players,
                      Map<Long, IrcRoster>         rosters,
                      List<IrcHand>                hands)
    {
        nextHandIndex = 0;
        this.players  = players;
        this.rosters  = rosters;
        this.hands    = hands;

        computeNextHistory();
    }


    //--------------------------------------------------------------------
    private void computeNextHistory()
    {
        nextHistory = null;
        while (nextHandIndex < hands.size())
        {
//            System.out.println("nextHandIndex " + nextHandIndex);
            nextHistory = computeHistory(hands.get( nextHandIndex++ ));
            if (nextHistory != null) break;
        }
    }

    private Replay computeHistory(IrcHand hand)
    {
        IrcRoster roster = rosters.get( hand.timestamp() );
        if (roster == null) return null;

        List<String>    names  = Arrays.asList(roster.names());
        List<IrcAction> action = handAction(names, hand.timestamp());
        if (action == null) return null;
        assert roster.size() == action.size();
//        if (! hasQuitters(action)) return null;

        sortByPosition(names, action);
        sizeUpBlinds(action);
//        System.out.println("nextHandIndex " + (nextHandIndex-1));
//        displayHand(hand, action);


        Map<Avatar, Hole>   holes         = new HashMap<Avatar, Hole>();
        List<Avatar>        playerHandles = new ArrayList<Avatar>();
        Map<Avatar, Player> brains        = new HashMap<Avatar, Player>();
        for (int i = 0; i < names.size(); i++)
        {
            String    name   = names.get(i);
            Avatar    handle = ircPlayer(name);
            IrcAction acts   = action.get(i);

            playerHandles.add(handle);
            brains.put(handle, new IrcPlayer(acts));

            Hole hole = acts.hole();
            if (hole != null)
            {
                holes.put(handle, acts.hole());
            }
        }

        Dealer dealer = new Dealer(false, brains);
        try
        {
            List<Hole> holesInOrder = new ArrayList<>();
            for (Avatar player : playerHandles) {
                holesInOrder.add(holes.get(player));
            }

            StackedReplay out = dealer.play(
                    playerHandles, new LiteralCards(hand.community(), holesInOrder));

            return out.replay();
        }
        catch (Throwable e)
        {
            if (e.getMessage() == null)
            {
                LOG.error("nextHandIndex " + (nextHandIndex-1), e);
                displayHand(hand, action);
            }
            else if (! e.getMessage()
                        .contains("round betting cap exceeded"))
            {
                LOG.error("nextHandIndex " + (nextHandIndex-1), e);
                displayHand(hand, action);
            }
            return null;
        }
    }

    private void displayHand(
            IrcHand         hand,
            List<IrcAction> action)
    {
        LOG.info(hand);
        for (IrcAction act : action)
        {
            LOG.info(act);
        }
    }

    private boolean hasQuitters(List<IrcAction> actions)
    {
        for (IrcAction acts : actions)
        {
            int quitIndex = Arrays.asList( acts.preFlop() )
                                .indexOf( Action.QUIT );
            if (quitIndex != -1) return true;
        }
        return false;
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

            return null;
//            throw new Error("can't find timestamp " + timestamp +
//                            " among " + playerAction);
        }

        return action;
    }


    //--------------------------------------------------------------------
    public Iterator<Replay> iterator()
    {
        return new Iterator<Replay>() {
            public boolean hasNext() {
                return (nextHistory != null);
            }

            public Replay next() {
                Replay next = nextHistory;
                computeNextHistory();
                return next;
            }

            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }


    //--------------------------------------------------------------------
    private Avatar ircPlayer(String name)
    {
        return new Avatar("irc", name);
    }
}
