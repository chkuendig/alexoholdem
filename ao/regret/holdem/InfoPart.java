package ao.regret.holdem;

import ao.holdem.engine.state.tree.StateTree;
import ao.holdem.model.Round;
import ao.util.io.Dir;
import org.apache.log4j.Logger;

import java.io.File;

/**
 * User: alex
 * Date: 20-Apr-2009
 * Time: 10:07:51 PM
 */
public class InfoPart
{
    //--------------------------------------------------------------------
    private static final Logger LOG =
            Logger.getLogger(InfoPart.class);


    //--------------------------------------------------------------------
    private static final String  HOLE_DIR = "info/hole";
    private static final String  FLOP_DIR = "info/flop";
    private static final String  TURN_DIR = "info/turn";
    private static final String RIVER_DIR = "info/river";


    //--------------------------------------------------------------------
    public static InfoPart retrieveOrCreate(
            File    dir,
            byte    nHoleBuckets,
            char    nFlopBuckets,
            char    nTurnBuckets,
            char    nRiverBuckets,
            boolean stored)
    {
        LOG.debug("loading (or creating)");

        return new InfoPart(
                get(dir,  HOLE_DIR,  nHoleBuckets, Round.PREFLOP, stored),
                get(dir,  FLOP_DIR,  nFlopBuckets, Round.FLOP   , stored),
                get(dir,  TURN_DIR,  nTurnBuckets, Round.TURN   , stored),
                get(dir, RIVER_DIR, nRiverBuckets, Round.RIVER  , stored),
                    dir);
    }
    private static InfoMatrix get(
            File dir, String subDir,
            int nBuckets, Round intentRound, boolean stored) {
        return InfoMatrix.retrieveOrCreate(
                 Dir.get(dir, subDir),
                 nBuckets, StateTree.intentCount(intentRound), stored);
    }

    private static void persist(File dir, InfoPart part)
    {
        LOG.debug("persisting...");
        long before = System.currentTimeMillis();

        InfoMatrix.persist(Dir.get(dir,  HOLE_DIR), part.hole);
        InfoMatrix.persist(Dir.get(dir,  FLOP_DIR), part.flop);
        InfoMatrix.persist(Dir.get(dir,  TURN_DIR), part.turn);
        InfoMatrix.persist(Dir.get(dir, RIVER_DIR), part.river);

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
    public InfoPart(byte nHoleBuckets,
                    char nFlopBuckets,
                    char nTurnBuckets,
                    char nRiverBuckets,
                    File inDir)
    {
        dir   = inDir;
        hole  = InfoMatrix.newInstance(
                    nHoleBuckets,  StateTree.intentCount(Round.PREFLOP));
        flop  = InfoMatrix.newInstance(
                    nFlopBuckets,  StateTree.intentCount(Round.FLOP));
        turn  = InfoMatrix.newInstance(
                    nTurnBuckets,  StateTree.intentCount(Round.TURN));
        river = InfoMatrix.newInstance(
                    nRiverBuckets, StateTree.intentCount(Round.RIVER));
    }

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



    //--------------------------------------------------------------------
    public void displayHeadsUpRoots()
    {
        hole .displayHeadsUpRoot();
//        flop .displayHeadsUpRoot();
//        turn .displayHeadsUpRoot();
//        river.displayHeadsUpRoot();
    }

    public boolean isStored()
    {
        return hole.isStored();
    }


    //--------------------------------------------------------------------
    public void flush()
    {
        persist(dir, this);
    }
}
