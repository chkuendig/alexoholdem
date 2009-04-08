package ao.holdem.engine.state.tree;

import ao.holdem.engine.state.HeadsUpStatus;
import ao.holdem.engine.state.State;
import ao.holdem.model.Avatar;
import ao.holdem.model.Round;
import ao.holdem.model.act.AbstractAction;
import org.apache.log4j.Logger;

import java.util.*;

/**
 * Date: Feb 1, 2009
 * Time: 3:28:32 PM
 */
public class StateTree
{
    //--------------------------------------------------------------------
    private static final Logger LOG =
            Logger.getLogger(StateTree.class);

    private StateTree() {}


    //--------------------------------------------------------------------
    private static char nextPreflopId          = 0;
    private static char nextPostflopTerminalId = 0;

    @SuppressWarnings({"MismatchedReadAndWriteOfArray"})
    private static char nextId[][]             =
            new char[ PathToFlop.VALUES.length ]
                    [ Round.VALUES.length - 1  ];


    private static final Node ROOT;

    static
    {
        LOG.debug("computing heads-up");
        ROOT = new Node(State.autoBlindInstance(Arrays.asList(
            Avatar.local("dealee"), Avatar.local("dealer"))));
    }
    

    //--------------------------------------------------------------------
    public static Node headsUpRoot()
    {
        return ROOT;
    }

    public static Node fromState(State state)
    {
        return fromState(headsUpRoot(), state);
    }
    private static Node fromState(Node startingAt, State state)
    {
        if (startingAt.state().equals( state )) {
            return startingAt;
        }

        for (Node child : startingAt.acts().values()) {
            Node fromState = fromState(child, state);
            if (fromState != null) return fromState;
        }
        return null;
    }


    //--------------------------------------------------------------------
    private static char nextId(PathToFlop path, Round round)
    {
        if (path  == null) return nextPreflopId++;
        if (round == null) return nextPostflopTerminalId++;
        return nextId[  path.ordinal()     ]
                     [ round.ordinal() - 1 ]++;
    }


    //--------------------------------------------------------------------
    public static char preflopCount()
    {
        return nextPreflopId;
    }

    public static char nodeCount(PathToFlop path, Round round)
    {
        return nextId[ path.ordinal() ][ round.ordinal() - 1 ];
    }


    //--------------------------------------------------------------------
    public static class Node
    {
        //----------------------------------------------------------------
        private final char          ID;
        private final boolean       CAN_RAISE;
        private final boolean       CAN_CHECK;
        private final boolean       DEALER_NEXT;
        private final PathToFlop    PATH;
        private final Round         ROUND;
        private final int           STAKES;
        private final int           DEALER_COMMIT;
        private final int           DEALEE_COMMIT;
        private final HeadsUpStatus STATUS;
        private final State         STATE;

        private final EnumMap<AbstractAction, Node> KIDS;


        //----------------------------------------------------------------
        private Node(State state)
        {
            this(null, state, new ArrayList<AbstractAction>(), null);
        }
        private Node(AbstractAction       prevAct,
                     State                state,
                     List<AbstractAction> path,
                     PathToFlop           pathToFlop)
        {
            if (pathToFlop != null) {
                PATH = pathToFlop;
            } else {
                if (prevAct != null) {
                    path.add( prevAct );
                }
                PATH = PathToFlop.matching( path );
            }

            ID            = nextId(PATH, state.round());
            CAN_RAISE     = state.canRaise();
            CAN_CHECK     = state.canCheck();
            STATUS        = state.headsUpStatus();
            ROUND         = state.round();
            STAKES        = state.stakes().smallBlinds();
            DEALER_COMMIT = state.seats(1).commitment().smallBlinds();
            DEALEE_COMMIT = state.seats(0).commitment().smallBlinds();
            DEALER_NEXT   = state.dealerIsNext();
            STATE         = state;

            KIDS   = new EnumMap<AbstractAction, Node>(
                          AbstractAction.class);
            for (Map.Entry<AbstractAction, State> act :
                    state.viableActions().entrySet())
            {
                List<AbstractAction> nextPath =
                        (PATH == null)
                        ? new ArrayList<AbstractAction>(path) : null;

//                if (! act.getValue().atEndOfHand()) {
                    KIDS.put(act.getKey(),
                             new Node(
                                     act.getKey(),
                                     act.getValue(),
                                     nextPath,
                                     PATH));
//                }
            }
        }


        //----------------------------------------------------------------
        public HeadsUpStatus status()
        {
            return STATUS;
        }
        public State state()
        {
            return STATE;
        }

        public boolean canRaise()
        {
            return CAN_RAISE;
        }
        public boolean canCheck()
        {
            return CAN_CHECK;
        }
        public boolean dealerIsNext()
        {
            return DEALER_NEXT;
        }

        public int stakes()
        {
            return STAKES;
        }
        public int dealerCommit()
        {
            return DEALER_COMMIT;
        }
        public int dealeeCommit()
        {
            return DEALEE_COMMIT;
        }

        public Round round()
        {
            return ROUND;
        }
        public PathToFlop pathToFlop()
        {
            return PATH;
        }

        // sequential for (round, pathToFlop) combination
        public char roundPathId()
        {
            return ID;
        }

        public Map<AbstractAction, Node> acts()
        {
            return KIDS;
        }
    }
}
