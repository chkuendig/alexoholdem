package ao.regret.holdem;

import ao.holdem.engine.state.tree.PathToFlop;
import ao.holdem.engine.state.tree.StateTree;
import ao.holdem.model.Round;
import ao.util.io.Dir;

import java.io.File;

/**
 * User: shalom
 * Date: Feb 26, 2009
 * Time: 12:21:56 PM
 */
public class InfoTree
{
    //--------------------------------------------------------------------
    private static final String  PREFLOP_DIR = "info/preflop";
    private static final String POSTFLOP_DIR = "info/posflop";


    //--------------------------------------------------------------------
    public static InfoTree retrieveOrCreate(
            File dir,
            byte nHoleBuckets,
            char nFlopBuckets,
            char nTurnBuckets,
            char nRiverBuckets)
    {
        InfoBranch preflop = InfoBranch.retrieveOrCreate(
                                Dir.get(dir, PREFLOP_DIR),
                                (char) nHoleBuckets,
                                StateTree.preflopCount());

        char nBuckets[] = {nFlopBuckets, nTurnBuckets, nRiverBuckets};
        InfoBranch postflop[][] =
                new InfoBranch[ PathToFlop.VALUES.length ]
                              [ Round.VALUES.length - 1  ];
        for (PathToFlop path : PathToFlop.VALUES)
        {
            for (int round = 0; round < Round.VALUES.length - 1; round++)
            {
                postflop[ path.ordinal() ]
                        [ round          ] =
                            InfoBranch.retrieveOrCreate(
                                    postflopDir(dir, path, round),
                                    nBuckets[round],
                                    StateTree.nodeCount(
                                        path, Round.VALUES[round + 1])
                            );
            }
        }

        return new InfoTree(preflop, postflop);
    }

    private static void persist(File dir, InfoTree tree)
    {
        InfoBranch.persist(Dir.get(dir, PREFLOP_DIR), tree.preflop);

        for (PathToFlop path : PathToFlop.VALUES)
        {
            for (int round = 0; round < Round.VALUES.length - 1; round++)
            {
                InfoBranch.persist(
                        postflopDir(dir, path, round),
                        tree.postflop[ path.ordinal() ]
                                     [ round          ]);
            }
        }
    }

    private static File postflopDir(File       dir,
                                    PathToFlop path,
                                    int        round)
    {
        return Dir.get(dir,
                POSTFLOP_DIR + "/" + path.ordinal()
                             + "/" + round);
    }



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

    private InfoTree(InfoBranch copyPreflop,
                     InfoBranch copyPostflop[][])
    {
        preflop  = copyPreflop;
        postflop = copyPostflop;
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
        preflop.displaySequence((char) 0);
    }


    //--------------------------------------------------------------------
    public void flush(File dir)
    {
        persist(dir, this);
    }
}
