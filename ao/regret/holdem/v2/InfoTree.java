package ao.regret.holdem.v2;

import ao.holdem.engine.state.tree.PathToFlop;
import ao.holdem.engine.state.tree.StateTree;
import ao.holdem.model.Round;

/**
 * User: shalom
 * Date: Feb 26, 2009
 * Time: 12:21:56 PM
 */
public class InfoTree
{
    //--------------------------------------------------------------------
    private final InfoBranch preflop;
    private final InfoBranch postflop[][];


    //--------------------------------------------------------------------
    public InfoTree(byte nHoleBuckets,
                    char nFlopBuckets,
                    char nTurnBuckets,
                    char nRiverBuckets)
    {
        char nBuckets[] = {nFlopBuckets, nTurnBuckets, nRiverBuckets};

        preflop = new InfoBranch((char) nHoleBuckets,
                                 StateTree.preflopCount());

        postflop = new InfoBranch[ PathToFlop.VALUES.length ]
                                 [ Round.VALUES.length - 1  ];
        for (PathToFlop path : PathToFlop.VALUES)
        {
            for (int round = 0; round < Round.VALUES.length - 1; round++)
            {
                postflop[ path.ordinal() ]
                        [ round          ] = new InfoBranch(
                            nBuckets[round],
                            StateTree.nodeCount(
                                    path, Round.VALUES[round + 1]));
            }
        }
    }


    //--------------------------------------------------------------------
    public InfoBranch preflopInfo()
    {
        return preflop;
    }
    public InfoBranch info(PathToFlop path,
                           Round      round)
    {
        if (path == null) return preflopInfo();

        return postflop[  path.ordinal()     ]
                       [ round.ordinal() - 1 ];
    }


    //--------------------------------------------------------------------
    public void displayFirstAct()
    {
        preflop.displaySequence((char) 1);
    }
}
