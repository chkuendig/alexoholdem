package ao.regret.holdem.v2;

import ao.holdem.engine.state.tree.StateTree;
import ao.holdem.model.Round;
import ao.regret.holdem.InfoTree;
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
            Logger.getLogger(InfoTree.class);


    //--------------------------------------------------------------------
    private static final String  HOLE_DIR = "info/hole";
    private static final String  FLOP_DIR = "info/flop";
    private static final String  TURN_DIR = "info/turn";
    private static final String RIVER_DIR = "info/river";


    //--------------------------------------------------------------------
    public static InfoPart retrieveOrCreate(
            File dir,
            byte nHoleBuckets,
            char nFlopBuckets,
            char nTurnBuckets,
            char nRiverBuckets)
    {
        LOG.debug("loading (or creating)");

        return new InfoPart(
                get(dir,  HOLE_DIR,  nHoleBuckets, Round.PREFLOP),
                get(dir,  FLOP_DIR,  nFlopBuckets, Round.FLOP),
                get(dir,  TURN_DIR,  nTurnBuckets, Round.TURN),
                get(dir, RIVER_DIR, nRiverBuckets, Round.RIVER),
                    dir);
    }
    private static InfoMatrix get(
            File dir, String subDir, int nBuckets, Round intentRound) {
        return InfoMatrix.retrieveOrCreate(
                 Dir.get(dir, subDir),
                 nBuckets, StateTree.intentCount(intentRound));
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


    //--------------------------------------------------------------------
    public void flush()
    {
        persist(dir, this);
    }
}
