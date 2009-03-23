package ao.holdem.engine.state.tree;

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
//        LOG.debug(((int) nextPreflopId));
    }
    

    //--------------------------------------------------------------------
    public static Node headsUpRoot()
    {
        return ROOT;
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
        private final char                          ID;
        private final PathToFlop                    PATH;
        private final State                         STATE;
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

//                if (state.round() != Round.PREFLOP &&
//                        PATH == null) {
//                    System.out.println("WTF??");
//                }
            }

            ID    = nextId(PATH, state.round());
            STATE = state;
            KIDS  = new EnumMap<AbstractAction, Node>(
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
        public State state()
        {
            return STATE;
        }

        public Round round()
        {
            return STATE.round();
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
