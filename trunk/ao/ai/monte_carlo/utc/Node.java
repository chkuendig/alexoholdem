package ao.ai.monte_carlo.utc;

import ao.holdem.engine.state.StateManager;
import ao.holdem.model.act.SimpleAction;
import ao.util.rand.Rand;

import java.util.LinkedList;

/**
 *
 */
public class Node
{
    //--------------------------------------------------------------------
    private int    visits;
    private Reward rewardSum;
    private Reward rewardSquareSum;

    private SimpleAction act;
    private StateManager state;

    private Node child;
    private Node sibling;


    //--------------------------------------------------------------------
    public Node(SimpleAction act,
                StateManager state)
    {
        this.act   = act;
        this.state = state;

        visits          = 0;
        rewardSum       = new Reward();
        rewardSquareSum = new Reward();
    }
    public Node(StateManager state)
    {
        this(null, state);
    }


    //--------------------------------------------------------------------
    public Node childWithState(StateManager state)
    {
        for (Node nextChild  = child;
                  nextChild != null;
                  nextChild  = nextChild.sibling)
        {
//            System.out.println("child.state\n" + nextChild.state);
            if (nextChild.state.equals( state ))
            {
//                System.out.println("equality!!");
                return nextChild;
            }
        }
        return null;
    }
    public StateManager state()
    {
        return state;
    }


    //--------------------------------------------------------------------
    public SimpleAction act()
    {
        return act;
    }

    public int visits()
    {
        return visits;
    }


    //--------------------------------------------------------------------
    public SimpleAction optimize()
    {
        Node best = optimizeInternal();
        return best.act;
    }
    public Node optimizeInternal()
    {
        Node   optimal       = null;
        double optimalReward = Long.MIN_VALUE;
        for (Node nextChild  = child;
                  nextChild != null;
                  nextChild  = nextChild.sibling)
        {
            double reward = nextChild.averageReward();

            if (reward > optimalReward)
            {
                optimal       = nextChild;
                optimalReward = reward;
            }
        }
        return (optimal == null) ? this : optimal;
    }

    private double averageReward()
    {
        return rewardSum.averagedOver( visits + 1 );
    }


    //--------------------------------------------------------------------
    public void playSimulation()
    {
//        LinkedList<Node> path = new LinkedList<Node>();
//        path.add(this);
//
//        while (! path.getLast().unvisited())
//        {
//            Node selectedChild =
//                    path.getLast().descendByUCB1();
//            if (selectedChild == null) break;
//
//            path.add( selectedChild );
//        }
//        propagateValue(path, path.getLast().monteCarloValue());
    }

    private void propagateValue(LinkedList<Node> path, Reward reward)
    {
//        Reward maxiMax = reward.compliment();
//        for (int i = path.size() - 1; i >= 0; i--)
//        {
//            Node step = path.get(i);
//
//            step.rewardSum = step.rewardSum.plus(maxiMax);
//            step.rewardSquareSum =
//                    step.rewardSquareSum.plus(
//                            maxiMax.square());
//            step.visits++;
//
//            maxiMax = maxiMax.compliment();
//        }
    }

//    private Reward monteCarloValue()
//    {
//        Rollout r = state.rollout();
//
//        if (child == null)
//        {
//            List<Node> children = state.generateNodes();
//
//            if (! children.isEmpty())
//            {
//                child = children.get(0);
//                for (int i = 0; i < children.size() - 1; i++)
//                {
//                    Node childSib = children.get(i);
//                    childSib.sibling = children.get(i + 1);
//                }
//            }
//        }
//
//        double isWin = r.winProbability();
//
//
//
//
//        return ( r.isTie()             ? new Reward(0.5) :
//                 r.nextToActIsWinner() ? new Reward(1.0) :
//                                         new Reward(0.0) );
//    }


    //--------------------------------------------------------------------
    public Node descendByUCB1()
    {
        double greatestUtc   = Long.MIN_VALUE;
        Node   greatestChild = null;
        for (Node nextChild  = child;
                  nextChild != null;
                  nextChild  = nextChild.sibling)
        {
            double utcValue;
            if (nextChild.unvisited())
            {
                utcValue = 10000 + Rand.nextDouble(1000);
//                utcValue =
//                        optimize
//                        ? 1.0 + Rand.nextDouble()/10000
//                        : 10000 + 1000 * Rand.nextDouble();
//                utcValue = 1.0;
//                utcValue = 1.0 + Rand.nextDouble()/10;
            }
            else
            {
                utcValue =
                        nextChild.averageReward() +
                        Math.sqrt(Math.log(visits) /
                                    (5 * nextChild.visits));
            }

            if (utcValue > greatestUtc)
            {
                greatestUtc   = utcValue;
                greatestChild = nextChild;
            }
        }

        return greatestChild;
    }

    private boolean unvisited()
    {
        return visits == 0;
    }


    //--------------------------------------------------------------------
    public String toString()
    {
        return toString(0);
    }
    public String toString(int indent)
    {
        if (visits == 0) return "";

        StringBuilder str = new StringBuilder();
        str.append('\n');

        str.append( indent(indent)  )
           //.append( "============"  )
           //.append( "\t"            )
            .append( "  "            )
           .append( visits          )
           .append( "  "            )
           .append( rewardSum       )
           .append( "  "            )
           .append( averageReward() )
           .append( "  "            )
           .append( act             );

        for (Node nextChild  = child;
                  nextChild != null;
                  nextChild  = nextChild.sibling)
        {
            str.append( nextChild.toString(indent + 2) );
        }

        return str.toString();
    }

    private String indent(int size)
    {
        StringBuilder indent = new StringBuilder(size);
        for (int i = 0; i < size; i++)
        {
            indent.append(' ');
        }
        return indent.toString();
    }
}
