package ao.holdem.ai.ai.monte_carlo.uct;

/**
 *
 */
public class Node
{
//    //--------------------------------------------------------------------
//    private RewardSet rewardSum;
//    private int       visits;
//
//    private PlayerHandle     mainActor;
//    private SimpleAction     act;
//    private PlayerHandle     actor;
//    private double           actProb;
//    private StateManager     afterAct;
//    private RewardNormalizer normaliezr;
//
//    private Node child;
//    private Node sibling;
//
//
//    //--------------------------------------------------------------------
//    private Node(PlayerHandle     mainActor,
//                 PlayerHandle     actor,
//                 SimpleAction     act,
//                 double           actProb,
//                 StateManager     stateAfterAct,
//                 RewardNormalizer normaliezr)
//    {
//        this.mainActor  = mainActor;
//        this.act        = act;
//        this.actProb    = actProb;
//        this.actor      = actor;
//        this.afterAct   = stateAfterAct;
//        this.normaliezr = normaliezr;
//
//        rewardSum       = new RewardSet();
//    }
//    public Node(StateManager state)
//    {
//        this(state.head().nextToAct().handle(),
//             null, null, Double.NaN,
//             state, new RewardNormalizer());
//    }
//
//    public Node advance(StateManager beforeAct,
//                        SimpleAction act)
//    {
//        StateManager afterAct = beforeAct.prototype( true );
//        afterAct.advance( act.toRealAction(beforeAct.head()) );
//
//        // xxx todo: -1 is a filler
//        return new Node(
//                    mainActor,
//                    beforeAct.head().nextToAct().handle(),
//                    act, -1, afterAct, normaliezr);
//    }
//
//
//    //--------------------------------------------------------------------
//    public Node childWithState(StateManager state)
//    {
//        for (Node nextChild  = child;
//                  nextChild != null;
//                  nextChild  = nextChild.sibling)
//        {
////            System.out.println("child.afterAct\n" + nextChild.afterAct);
//            if (nextChild.afterAct.equals( state ))
//            {
////                System.out.println("equality!!");
//                return nextChild;
//            }
//        }
//        return null;
//    }
//    public StateManager state()
//    {
//        return afterAct;
//    }
//
//
//    //--------------------------------------------------------------------
//    public SimpleAction act()
//    {
//        return act;
//    }
//
//
//    //--------------------------------------------------------------------
//    public SimpleAction optimize()
//    {
//        Node best = optimizeInternal();
//        return best.act;
//    }
//    private Node optimizeInternal()
//    {
//        Node   optimal       = null;
//        double optimalReward = Long.MIN_VALUE;
//        for (Node nextChild  = child;
//                  nextChild != null;
//                  nextChild  = nextChild.sibling)
//        {
//            double reward = nextChild.aggregateReward();
//
//            if (reward > optimalReward)
//            {
//                optimal       = nextChild;
//                optimalReward = reward;
//            }
//        }
//        return (optimal == null) ? this : optimal;
//    }
//
//    private double aggregateReward()
//    {
//        return rewardSum.aggregateFor( actor );
//    }
//
//
//    //--------------------------------------------------------------------
//    public void playSimulation(PredictorService predictor)
//    {
//        LinkedList<Node> path = new LinkedList<Node>();
//        path.add( this );
//
//        while (! path.getLast().unvisited())
//        {
//            Node selectedChild =
//                    path.getLast().descendByUCB1();
//            if (selectedChild == null) break;
//
//            path.add( selectedChild );
//        }
//        propagateValue(
//                path,
//                path.getLast()
//                        .monteCarloValue( predictor ));
//    }
//
//    private void propagateValue(
//            LinkedList<Node> path, RewardSet reward)
//    {
//        for (Node n : path)
//        {
//            n.cumulate( reward );
//        }
//    }
//    private void cumulate(RewardSet reward)
//    {
//        rewardSum.add( reward );
//        visits++;
//    }
//
//
//    private RewardSet monteCarloValue(
//            PredictorService predictor)
//    {
//        initChildren(predictor);
//
//        RewardSet rewardSet = new RewardSet();
//        if (act == SimpleAction.FOLD)
//        {
//            rewardSet.add(actor,
//                    new Reward( -afterAct.head().stakes().smallBets() ));
//        }
//        else
//        {
//            Simulator sim = new Simulator(predictor, afterAct);
//
//            if (afterAct.atEndOfHand())
//            {
//                rewardSet.add(actor, sim.expectedAtShowdown(actor));
//            }
//            else
//            {
//                ProbableRollout r      = sim.rollout();
//                double          reward = r.expectedReward();
//                rewardSet.add(actor, new Reward(reward));
//            }
//        }
//        return rewardSet;
//    }
//
//    private void initChildren(PredictorService predictor)
//    {
//        if (child != null) return;
//
//        //predictor.predictAction(actor, afterAct.head().);
//
//        List<Node> children = afterAct.generateActionNodes(this);
//
//        if (! children.isEmpty())
//        {
//            child = children.get(0);
//            for (int i = 0; i < children.size() - 1; i++)
//            {
//                Node childSib = children.get(i);
//                childSib.sibling = children.get(i + 1);
//            }
//        }
//    }
//
//
//
//    //--------------------------------------------------------------------
//    public Node descendByUCB1()
//    {
//        double greatestUtc   = Long.MIN_VALUE;
//        Node   greatestChild = null;
//        for (Node nextChild  = child;
//                  nextChild != null;
//                  nextChild  = nextChild.sibling)
//        {
//            double utcValue;
//            if (nextChild.unvisited())
//            {
//                utcValue = 10000 + Rand.nextDouble(1000);
////                utcValue =
////                        optimize
////                        ? 1.0 + Rand.nextDouble()/10000
////                        : 10000 + 1000 * Rand.nextDouble();
////                utcValue = 1.0;
////                utcValue = 1.0 + Rand.nextDouble()/10;
//            }
//            else
//            {
//                utcValue =
//                        normaliezr.normalize(
//                                nextChild.aggregateReward()) +
//                        Math.sqrt(Math.log(visits) /
//                                    (5 * nextChild.visits));
//            }
//
//            if (utcValue > greatestUtc)
//            {
//                greatestUtc   = utcValue;
//                greatestChild = nextChild;
//            }
//        }
//
//        return greatestChild;
//    }
//
//    private boolean unvisited()
//    {
//        return visits == 0;
//    }
//
//
//    //--------------------------------------------------------------------
//    public String toString()
//    {
//        return toString(0);
//    }
//    public String toString(int indent)
//    {
//        if (visits == 0) return "";
//
//        StringBuilder str = new StringBuilder();
//        str.append('\n');
//
//        str.append( indent(indent)  )
//           //.append( "============"  )
//           //.append( "\t"            )
//            .append( "  "            )
//           .append( visits          )
//           .append( "  "            )
//           .append( rewardSum       )
//           .append( "  "            )
//           .append( aggregateReward() )
//           .append( "  "            )
//           .append( act             );
//
//        for (Node nextChild  = child;
//                  nextChild != null;
//                  nextChild  = nextChild.sibling)
//        {
//            str.append( nextChild.toString(indent + 2) );
//        }
//
//        return str.toString();
//    }
//
//    private String indent(int size)
//    {
//        StringBuilder indent = new StringBuilder(size);
//        for (int i = 0; i < size; i++)
//        {
//            indent.append(' ');
//        }
//        return indent.toString();
//    }
}
