package ao.holdem.model.replay;

import ao.holdem.engine.state.ActionStateFlow;
import ao.holdem.model.Avatar;
import ao.holdem.model.AvatarBinding;
import ao.holdem.model.Round;
import ao.holdem.model.act.Action;
import ao.holdem.model.act.ActionBinding;
import ao.holdem.model.card.Community;
import ao.holdem.model.card.CommunityBinding;
import ao.holdem.model.card.Hole;
import ao.holdem.model.card.HoleBinding;
import ao.holdem.model.card.chance.ChanceCards;
import ao.holdem.model.card.chance.LiteralCards;
import ao.holdem.persist.UniqueId;
import com.sleepycat.bind.tuple.TupleInput;
import com.sleepycat.bind.tuple.TupleOutput;
import com.sleepycat.bind.tuple.TupleTupleBinding;

import java.util.*;

/**
 *
 */
public class Replay
{
    //--------------------------------------------------------------------
    private final UniqueId                  id;
    private final List<Avatar>              players;
    private final Map<Avatar, List<Action>> action;
    private final Community                 community;
    private final Map<Avatar, Hole>         holes;


    //--------------------------------------------------------------------
    public static Replay fromFlow(List<Avatar> clockwiseDealerLast, ChanceCards cards, ActionStateFlow stateFlow)
    {
        Map<Avatar, List<Action>> playerAction = new HashMap<>();
        for (int i = 0; i < stateFlow.playerCount(); i++) {
            playerAction.put(clockwiseDealerLast.get(i), stateFlow.actions().get(i));
        }

        return new Replay(clockwiseDealerLast, cards, stateFlow.lastActRound(), playerAction);
    }


    public Replay(List<Avatar>              clockwiseDealerLast,
                  ChanceCards               cards,
                  Round                     asOf,
                  Map<Avatar, List<Action>> playerAction)
    {
        this(UniqueId.nextInstance(),
             clockwiseDealerLast,
             playerAction,
             cards.community(asOf),
             initHoles());

        for (Avatar avatar : clockwiseDealerLast)
        {
            addHole(avatar, cards.hole(clockwiseDealerLast.indexOf(avatar)));
        }
    }

    public Replay(
            List<Avatar> clockwiseDealerLast,
            ChanceCards  cards,
            Round        asOf)
    {
        this(clockwiseDealerLast,
             cards,
             asOf,
             initPlayerAction(clockwiseDealerLast));
    }

    private Replay(
            UniqueId handId,
            List<Avatar>              clockwiseDealerLast,
            Map<Avatar, List<Action>> playerAction,
            Community                 communityCards,
            Map<Avatar, Hole>         holeCards)
    {
        id        = handId;
        players   = clockwiseDealerLast;
        action    = playerAction;
        community = communityCards;
        holes     = holeCards;
    }

    private static Map<Avatar, Hole> initHoles()
    {
        return new HashMap<Avatar, Hole>();
    }
    private static Map<Avatar, List<Action>>
            initPlayerAction(List<Avatar> players)
    {
        Map<Avatar, List<Action>> playerAction =
                new LinkedHashMap<Avatar, List<Action>>();
        for (Avatar actor : players)
        {
            playerAction.put(actor, new ArrayList<Action>());
        }
        return playerAction;
    }


    //--------------------------------------------------------------------
    public void addAction(Avatar player, Action act)
    {
        action.get( player ).add( act );
    }

    private void addHole(Avatar player, Hole hole)
    {
        Hole previous = holes.put(player, hole);
        assert previous == null || previous.equals( hole );
    }


    //--------------------------------------------------------------------
    public UniqueId id()
    {
        return id;
    }

    public Iterable<Action> action(Avatar forPlayer)
    {
        return Collections.unmodifiableList(
                action.get( forPlayer ));
    }

    // clockwise, dealer being last.
    public List<Avatar> players()
    {
        return players;
    }

    public ChanceCards cards()
    {
        List<Hole> inOrder = new ArrayList<>();
        for (Avatar player : players) {
            inOrder.add(holes.get(player));
        }

        return new LiteralCards(community, inOrder);
    }


    //--------------------------------------------------------------------
    public String toString()
    {
        return id        .toString() + ":\n" +
               community .toString() +  "\n" +
               holes     .toString() +  "\n" +
               action    .toString();
    }

    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null ||
            getClass() != o.getClass()) return false;

        Replay hand = (Replay) o;
        return id.equals(hand.id);
    }

    public int hashCode()
    {
        return id.hashCode();
    }


    //--------------------------------------------------------------------
    // key: UniqueId
    //
    // data:
    // (community)
    // (number of players)
    // for each player
    //  (avatar)
    //  (hole)
    //  (number of actions)
    //  (actions) x n
    public static final Binding BINDING = new Binding();
    public static class Binding extends TupleTupleBinding<Replay>
    {
        public void objectToKey(Replay object, TupleOutput output)
        {
            Replay hand = (Replay) object;
            UniqueId.BINDING.objectToEntry(
                    hand.id, output);
        }

        public void objectToData(Replay hand, TupleOutput output)
        {
            CommunityBinding.INSTANCE.objectToEntry(hand.community, output);
            output.writeShort( hand.players().size() );

            for (Avatar player : hand.players())
            {
                AvatarBinding.INSTANCE.objectToEntry(player, output);
                HoleBinding.INSTANCE.objectToEntry(
                        hand.holes.get(player), output);

                List<Action> acts = hand.action.get(player);
                output.writeShort( acts.size() );

                for (Action act : acts)
                {
                    ActionBinding.INSTANCE.objectToEntry(act, output);
                }
            }
        }

        public Replay entryToObject(TupleInput keyInput,
                                  TupleInput dataInput)
        {
            UniqueId id = UniqueId.BINDING.entryToObject(keyInput);

            Community community =
                    CommunityBinding.INSTANCE.entryToObject(dataInput);
            short numPlayers = dataInput.readShort();

            List<Avatar> clockwiseDealerLast =
                    new ArrayList<Avatar>(numPlayers);
            Map<Avatar, List<Action>> playerAction =
                    new HashMap<Avatar, List<Action>>();
            Map<Avatar, Hole> holes =
                    new HashMap<Avatar, Hole>();

            for (short player = 0; player < numPlayers; player++)
            {
                Avatar avatar =
                        AvatarBinding.INSTANCE.entryToObject(dataInput);
                clockwiseDealerLast.add( avatar );

                Hole hole = HoleBinding.INSTANCE.entryToObject(dataInput);
                holes.put(avatar, hole);

                short        numActions = dataInput.readShort();
                List<Action> actions    =
                        new ArrayList<Action>(numActions);
                playerAction.put(avatar, actions);

                for (short action = 0; action < numActions; action++)
                {
                    Action act =
                            ActionBinding.INSTANCE.entryToObject(dataInput);
                    actions.add( act );
                }
            }

            return new Replay(id,
                            clockwiseDealerLast,
                            playerAction,
                            community,
                            holes);
        }
    }
}
