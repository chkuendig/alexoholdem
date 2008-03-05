package ao.holdem.v3.model.hand;

import ao.holdem.v3.model.Avatar;
import ao.holdem.v3.model.act.descrete.Action;
import com.sleepycat.bind.tuple.TupleTupleBinding;
import com.sleepycat.bind.tuple.TupleInput;
import com.sleepycat.bind.tuple.TupleOutput;

import java.io.Serializable;
import java.util.*;

/**
 *
 */
public class Hand implements Serializable
{
    //--------------------------------------------------------------------
    private final HandId                    id;
    private final List<Avatar>              players;
    private final Map<Avatar, List<Action>> action;


    //--------------------------------------------------------------------
    public Hand(List<Avatar> clockwiseDealerLast)
    {
        this(HandId.nextInstance(),
             clockwiseDealerLast,
             initPlayerAction( clockwiseDealerLast ));
    }
    private Hand(HandId                    handId,
                 List<Avatar>              clockwiseDealerLast,
                 Map<Avatar, List<Action>> playerAction)
    {
        id      = handId;
        players = clockwiseDealerLast;
        action  = playerAction;
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


    //--------------------------------------------------------------------
    public HandId id()
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


    //--------------------------------------------------------------------
    public String toString()
    {
        return id.toString() + ":\n" +
               action.toString();
    }

    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null ||
            getClass() != o.getClass()) return false;

        Hand hand = (Hand) o;
        return id.equals(hand.id);
    }

    public int hashCode()
    {
        return id.hashCode();
    }


    //--------------------------------------------------------------------
    // key: HandId
    //
    // data:
    // (number of players)
    // for each player
    //  (avatar)
    //  (number of actions)
    //  (actions) x n
    public static final Binding BINDING = new Binding();
    public static class Binding extends TupleTupleBinding
    {
        public void objectToKey(Object object, TupleOutput output)
        {
            Hand hand = (Hand) object;
            HandId.BINDING.objectToEntry(
                    hand.id(), output);
        }

        public void objectToData(Object object, TupleOutput output)
        {
            Hand hand = (Hand) object;
            output.writeShort( hand.players().size() );

            for (Avatar player : hand.players())
            {
                Avatar.BINDING.objectToEntry(player, output);

                List<Action> acts = hand.action.get(player);
                output.writeShort( acts.size() );

                for (Action act : acts)
                {
                    Action.BINDING.objectToEntry(act, output);
                }
            }
        }

        public Hand entryToObject(TupleInput keyInput,
                                  TupleInput dataInput)
        {
            HandId id = HandId.BINDING.entryToObject(keyInput);
            short numPlayers = dataInput.readShort();

            List<Avatar> clockwiseDealerLast =
                    new ArrayList<Avatar>(numPlayers);
            Map<Avatar, List<Action>> playerAction =
                    new HashMap<Avatar, List<Action>>();

            for (short player = 0; player < numPlayers; player++)
            {
                Avatar avatar =
                        Avatar.BINDING.entryToObject(dataInput);
                clockwiseDealerLast.add( avatar );

                short        numActions = dataInput.readShort();
                List<Action> actions    =
                        new ArrayList<Action>(numActions);
                playerAction.put(avatar, actions);

                for (short action = 0; action < numActions; action++)
                {
                    Action act =
                            Action.BINDING.entryToObject(dataInput);
                    actions.add( act );
                }
            }

            return new Hand(id,
                            clockwiseDealerLast,
                            playerAction);
        }
    }
}
