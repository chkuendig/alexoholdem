package ao.holdem.bot.regret;

import ao.holdem.engine.state.tree.StateTree;
import ao.holdem.model.Round;
import ao.util.io.Dirs;
import org.apache.log4j.Logger;

import java.io.File;

/**
 * User: alex
 * Date: 20-Apr-2009
 * Time: 10:07:51 PM
 *
 * Information partition, made up of
 */
public class InfoPart
{
    //--------------------------------------------------------------------
    private static final Logger LOG =
            Logger.getLogger(InfoPart.class);


    //--------------------------------------------------------------------
    private static final String  HOLE_DIR = "hole";
    private static final String  FLOP_DIR = "flop";
    private static final String  TURN_DIR = "turn";
    private static final String RIVER_DIR = "river";


    //--------------------------------------------------------------------
    public static boolean exists(File dir)
    {
        return exists(dir,  HOLE_DIR) &&
               exists(dir,  FLOP_DIR) &&
               exists(dir,  TURN_DIR) &&
               exists(dir, RIVER_DIR);
    }

    private static boolean exists(
            File dir, String subDir) {
        return InfoMatrix.exists(Dirs.get(dir, subDir));
    }


    //--------------------------------------------------------------------
    public static InfoPart retrieveOrCreate(
            File    dir,
            int     nHoleBuckets,
            char    nFlopBuckets,
            char    nTurnBuckets,
            char    nRiverBuckets,
            boolean readOnly,
            boolean doublePrecision)
    {
        LOG.debug("loading (or creating) " +
                  (readOnly ? "read only" : "in-memory") );

        return new InfoPart(
                get(dir,  HOLE_DIR,  nHoleBuckets, Round.PREFLOP,
                        readOnly, true),
                get(dir,  FLOP_DIR,  nFlopBuckets, Round.FLOP   ,
                        readOnly, true),
                get(dir,  TURN_DIR,  nTurnBuckets, Round.TURN   ,
                        readOnly, true),
                get(dir, RIVER_DIR, nRiverBuckets, Round.RIVER  ,
                        readOnly, doublePrecision),
                    dir);
    }
    private static InfoMatrix get(
            File dir, String subDir,
            int nBuckets, Round intentRound,
            boolean readOnly, boolean doublePrecision) {
        return InfoMatrix.retrieveOrCreate(
                 Dirs.get(dir, subDir),
                 nBuckets, StateTree.intentCount(intentRound),
                 readOnly, doublePrecision);
    }

    private static void persist(File dir, InfoPart part)
    {
        LOG.debug("persisting...");
        long before = System.currentTimeMillis();

        InfoMatrix.persist(Dirs.get(dir,  HOLE_DIR), part.hole);
        InfoMatrix.persist(Dirs.get(dir,  FLOP_DIR), part.flop);
        InfoMatrix.persist(Dirs.get(dir,  TURN_DIR), part.turn);
        InfoMatrix.persist(Dirs.get(dir, RIVER_DIR), part.river);

        LOG.debug("done persisting, took " +
                    (System.currentTimeMillis() - before) / 1000);
    }


    //--------------------------------------------------------------------
    private final File       dir;
    private final InfoMatrix hole;
    private final InfoMatrix flop;
    private final InfoMatrix turn;
    private final InfoMatrix river;


    //--------------------------------------------------------------------
//    public InfoPart(byte    nHoleBuckets,
//                    char    nFlopBuckets,
//                    char    nTurnBuckets,
//                    char    nRiverBuckets,
//                    File    inDir,
//                    boolean doublePrecision)
//    {
//        dir   = inDir;
//
//        hole  = InfoMatrix.newInstance(
//                    nHoleBuckets,
//                    StateTree.intentCount(Round.PREFLOP),
//                    doublePrecision);
//
//        flop  = InfoMatrix.newInstance(
//                    nFlopBuckets,
//                    StateTree.intentCount(Round.FLOP),
//                    doublePrecision);
//
//        turn  = InfoMatrix.newInstance(
//                    nTurnBuckets,
//                    StateTree.intentCount(Round.TURN),
//                    doublePrecision);
//
//        river = InfoMatrix.newInstance(
//                    nRiverBuckets,
//                    StateTree.intentCount(Round.RIVER),
//                    doublePrecision);
//    }

    private InfoPart(InfoMatrix copyHole,
                     InfoMatrix copyFlop,
                     InfoMatrix copyTurn,
                     InfoMatrix copyRiver,
                     File       inDir)
    {
        dir   = inDir;
        hole  = copyHole;
        flop  = copyFlop;
        turn  = copyTurn;
        river = copyRiver;
    }


    //--------------------------------------------------------------------
    public InfoMatrix infoMatrix(Round intentRound)
    {
        switch (intentRound) {
            case PREFLOP: return hole;
            case FLOP:    return flop;
            case TURN:    return turn;
            case RIVER:   return river;
        }
        throw new IllegalArgumentException();
    }

    public InfoMatrix.InfoSet infoSet(
            Round intentRound,
            int   bucket,
            int   foldIntent,
            int   callIntent,
            int   raiseIntent)
    {
        return infoMatrix(intentRound).infoSet(
                bucket, foldIntent, callIntent, raiseIntent);
    }

    public InfoMatrix.InfoSet infoSet(
            StateTree.Node node, int bucket)
    {
        return infoSet(node.round(), bucket, node.foldIntent(), node.callIntent(), node.raiseIntent());
    }



    //--------------------------------------------------------------------
    public void displayHeadsUpRoots()
    {
        hole .displayHeadsUpRoot();
//        flop .displayHeadsUpRoot();
//        turn .displayHeadsUpRoot();
//        river.displayHeadsUpRoot();
    }

    public boolean isReadOnly()
    {
        return hole.isReadOnly();
    }


    //--------------------------------------------------------------------
    public void flush()
    {
        persist(dir, this);
    }
}
