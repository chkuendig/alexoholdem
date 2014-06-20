package ao.holdem.engine.state.tree;

import ao.holdem.engine.state.ActionState;
import ao.holdem.engine.state.HeadsUpStatus;
import ao.holdem.model.Round;
import ao.holdem.model.act.AbstractAction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Date: Feb 1, 2009
 * Time: 3:28:32 PM
 */
public class StateTree
{
    //--------------------------------------------------------------------
    private static final Logger LOG =
            LoggerFactory.getLogger(StateTree.class);

    private StateTree() {}


    //--------------------------------------------------------------------
    private static char nextIntent[] = new char[ Round.COUNT ];

    private static char nextPreflopId          = 0;
    private static char nextPostflopTerminalId = 0;

    private static char[][] nextId =
            new char[ PathToFlop.VALUES.length ]
                    [ Round.VALUES.length - 1  ];

    private static int nextIndex = 0;


    private static final Node ROOT;

    static
    {
        LOG.debug("computing heads-up");
        ROOT = new Node(ActionState.autoBlindInstance(2));
        LOG.debug("tree size: {}", nextIndex);
    }
    

    //--------------------------------------------------------------------
    public static Node headsUpRoot()
    {
        return ROOT;
    }


    //--------------------------------------------------------------------
    public static Node fromState(ActionState state)
    {
        return fromState(state, Integer.MAX_VALUE);
    }
    public static Node fromState(ActionState state, int plyLeft) {
        return fromState(headsUpRoot(), state, plyLeft);
    }
    public static Node fromState(Node startingAt, ActionState state) {
        return fromState(startingAt, state, Integer.MAX_VALUE);
    }

    public static Node fromState(
            Node startingAt, ActionState state, int plyLeft)
    {
        if (plyLeft == 0) return null;
        if (startingAt.state().equals( state )) {
            return startingAt;
        }

        for (Node child : startingAt.acts().values()) {
            Node fromState = fromState(child, state, plyLeft - 1);
            if (fromState != null) return fromState;
        }
        return null;
    }


    //--------------------------------------------------------------------
    private static char nextIntent(Round prevRound)
    {
        return prevRound == null
               ? (char) -1
               : nextIntent[ prevRound.ordinal() ]++;
    }
    private static char nextId(PathToFlop path, Round round)
    {
        if (path  == null) return nextPreflopId++;
        if (round == null) return nextPostflopTerminalId++;
        return nextId[ path.ordinal()      ]
                     [ round.ordinal() - 1 ]++;
    }


    //--------------------------------------------------------------------
    public static int nodeCount() {
        return nextIndex;
    }

    public static char intentCount(Round intentRound)
    {
        return nextIntent[ intentRound.ordinal() ];
    }

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
        private final int           INDEX;
        private final char          ID;
        private final char          INTENT;
        private final boolean       CAN_RAISE;
        private final boolean       CAN_CHECK;
        private final boolean       DEALER_NEXT;
        private final PathToFlop    PATH;
        private final Round         ROUND;
        private final int           STAKES;
        private final int           DEALER_COMMIT;
        private final int           DEALEE_COMMIT;
        private final HeadsUpStatus STATUS;
        private final ActionState STATE;

        private final EnumMap<AbstractAction, Node> KIDS;
        private final Node                          KID_NODES[];
        private final int                           F_INTENT;
        private final int                           C_INTENT;
        private final int                           R_INTENT;


        //----------------------------------------------------------------
        private Node(ActionState state) {
            this(null, state, null,
                 new ArrayList<AbstractAction>(), null);
        }
        private Node(AbstractAction       prevAct,
                     ActionState state,
                     Round                prevRound,
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

            //nextIntent

            INDEX         = nextIndex++;
            ID            = nextId(PATH, state.round());
            INTENT        = nextIntent(prevRound);
            CAN_RAISE     = state.canRaise();
            CAN_CHECK     = state.canCheck();
            STATUS        = state.headsUpStatus();
            ROUND         = state.round();
            STAKES        = state.stakes().smallBlinds();
            DEALER_COMMIT = state.seats(1).commitment().smallBlinds();
            DEALEE_COMMIT = state.seats(0).commitment().smallBlinds();
            DEALER_NEXT   = state.dealerIsNext();
            STATE         = state;

            KIDS = new EnumMap<>(AbstractAction.class);
            for (Map.Entry<AbstractAction, ActionState> act :
                    state.actions(false).entrySet())
//                    state.viableActions().entrySet())
            {
                List<AbstractAction> nextPath =
                        (PATH == null)
                        ? new ArrayList<>(path) : null;

//                if (! act.getValue().atEndOfHand()) {
                    KIDS.put(act.getKey(),
                             new Node(
                                     act.getKey(),
                                     act.getValue(),
                                     ROUND,
                                     nextPath,
                                     PATH));
//                }
            }

            // intents
            F_INTENT = intent( AbstractAction.QUIT_FOLD  );
            C_INTENT = intent( AbstractAction.CHECK_CALL );
            R_INTENT = intent( AbstractAction.BET_RAISE  );

            KID_NODES = new Node[]{
                    KIDS.get( AbstractAction.QUIT_FOLD  ),
                    KIDS.get( AbstractAction.CHECK_CALL ) ,
                    KIDS.get( AbstractAction.BET_RAISE  )};
        }
        private int intent(AbstractAction forAction) {
            return !KIDS.containsKey( forAction )
                   ? -1 : KIDS.get( forAction ).intent();
        }


        //----------------------------------------------------------------
        public HeadsUpStatus status() {
            return STATUS;
        }
        public ActionState state() {
            return STATE;
        }

        public boolean canRaise() {
            return CAN_RAISE;
        }
        public boolean canCheck() {
            return CAN_CHECK;
        }
        public boolean dealerIsNext() {
            return DEALER_NEXT;
        }

        public int stakes() {
            return STAKES;
        }
        public int dealerCommit() {
            return DEALER_COMMIT;
        }
        public int dealeeCommit() {
            return DEALEE_COMMIT;
        }

        public Round round() {
            return ROUND;
        }
        public PathToFlop pathToFlop() {
            return PATH;
        }

        public int index() {
            return INDEX;
        }

        // sequential for (round, pathToFlop) combination
        public char roundPathId() {
            // never happens:
//            if (PATH != null &&
//                    ROUND == null) {
//                // posflop terminal
//                System.out.println("Oh!");
//            }

            return ID;
        }
        public char intent() {
            return INTENT;
        }

        public Map<AbstractAction, Node> acts() {
            return KIDS;
        }
        public Node kid(AbstractAction act) {
            return KID_NODES[ act.ordinal() ];
        }

        public int foldIntent() {
            return F_INTENT;
        }
        public int callIntent() {
            return C_INTENT;
        }
        public int raiseIntent() {
            return R_INTENT;
        }

//        public InfoMatrix.InfoSet infoSet(
//                int bucket, InfoPart from) {
//            return infoSet(bucket, from.infoMatrix(ROUND));
//        }
//        public InfoMatrix.InfoSet infoSet(
//                int bucket, InfoMatrix from) {
//            return from.infoSet(bucket, F_INTENT, C_INTENT, R_INTENT);
//        }


        //----------------------------------------------------------------
        public int countSub() {
            if (status() != HeadsUpStatus.IN_PROGRESS) return 1;

            int c = 1;
            for (Node k : acts().values()) {
                c += k.countSub();
            }
            return c;
        }
    }
}
