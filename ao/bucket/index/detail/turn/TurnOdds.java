package ao.bucket.index.detail.turn;

import ao.bucket.index.detail.enumeration.CanonTraverser;
import ao.bucket.index.turn.Turn;
import ao.bucket.index.turn.TurnLookup;
import ao.odds.agglom.Odds;
import ao.odds.agglom.impl.PreciseHeadsUpOdds;
import ao.util.io.Dir;
import ao.util.misc.Traverser;
import ao.util.persist.PersistentInts;
import org.apache.log4j.Logger;

import java.io.File;
import java.util.Arrays;

/**
 * Date: Jan 14, 2009
 * Time: 1:37:12 PM
 */
public class TurnOdds
{
    //--------------------------------------------------------------------
    public static void main(String[] args)
    {
        System.out.println( lookup(0) );
    }


    //--------------------------------------------------------------------
    private static final Logger LOG =
            Logger.getLogger(TurnOdds.class);


    //--------------------------------------------------------------------
    private static final File DIR =
            Dir.get("lookup/canon/detail/turn_odds/");

    private static final File FLAG_FILE  = new File(DIR,   "flag.int");
    private static final File WIN_FILE   = new File(DIR,   "wins.int");
    private static final File LOSE_FILE  = new File(DIR, "losses.int");
    private static final File SPLIT_FILE = new File(DIR, "splits.int");


    //--------------------------------------------------------------------
    private TurnOdds() {}


    //--------------------------------------------------------------------
    private static final int[] WIN;
    private static final int[] LOSE;
    private static final int[] SPLIT;


    //--------------------------------------------------------------------
    static //void retrieveOrComputeOdds()
    {
        LOG.debug("initializing TurnOdds");

        int[] wins = PersistentInts.retrieve(WIN_FILE);
        if (wins == null)
        {
            LOG.debug("starting from scratch");

            WIN   = oddsComponent();
            LOSE  = oddsComponent();
            SPLIT = oddsComponent();
        }
        else
        {
            WIN   = wins;
            LOSE  = PersistentInts.retrieve(LOSE_FILE);
            SPLIT = PersistentInts.retrieve(SPLIT_FILE);
        }

        if (! getIsPreComputed())
        {
            LOG.debug("resuming computation");

            computeOdds();
            flush();
            setIsPreComputed();
        }
    }

    private static int[] oddsComponent()
    {
        int[] component = new int[ TurnLookup.CANONICAL_COUNT ];
        Arrays.fill( component, -1 );
        return component;
    }


    //--------------------------------------------------------------------
    private static void computeOdds()
    {
        LOG.debug("computing odds");

        final int[]  skinCount      = {0};
        final int[]  calcCount      = {0};
        final long[] milestoneStart = {0};

        new CanonTraverser().traverseTurns(
                null, new Traverser<Turn>() {
            public void traverse(Turn turn) {
                if (WIN[ turn.canonIndex() ] != -1) {
                    skinCount[0]++;
                    return;
                } else if (skinCount[0] != 0) {
                    System.out.println("skipped " + skinCount[0]);
                    skinCount[0] = 0;
                }

                Odds odds = PreciseHeadsUpOdds.INSTANCE.compute(
                        turn.hole(), turn.community());

                int wins   = asInt(odds.winOdds());
                int losses = asInt(odds.loseOdds());
                int splits = asInt(odds.splitOdds());

                WIN  [ turn.canonIndex() ] = wins;
                LOSE [ turn.canonIndex() ] = losses;
                SPLIT[ turn.canonIndex() ] = splits;

                detectOverflow(turn.canonIndex(), odds,
                               wins, losses, splits);

                checkpoint( calcCount[0]++, milestoneStart );
            }
        });
    }

    private static void checkpoint(
            int count, long[] milestoneStart)
    {
        if (milestoneStart[0] == 0)
            milestoneStart[0] = System.currentTimeMillis();

        if (count % 100 == 0)
            System.out.print(".");

        boolean milesoneReached = ((count + 1) % 5000 == 0);
        if (milesoneReached) {
            System.out.println(
                    " " + (count + 1) + ", completed 5000 in " +
                    (System.currentTimeMillis() - milestoneStart[0]));
        }

        if ((count + 1) % 1000000 == 0) {
            long start = System.currentTimeMillis();
            flush();
            System.out.println(
                    "flushed checkpoint, took " +
                      (System.currentTimeMillis() - start));
        }

        if (milesoneReached)
            milestoneStart[0] = System.currentTimeMillis();
    }

    private static int asInt(long odds)
    {
        return (int) Math.min(odds, Integer.MAX_VALUE);
    }

    private static void detectOverflow(
            int canonIndex, Odds odds,
            int wins, int losses, int splits)
    {
        if (wins   != odds.winOdds()  ||
            losses != odds.loseOdds() ||
            splits != odds.splitOdds()) {

            LOG.error("odds overflow for turn" +
                        canonIndex);
        }
    }


    //--------------------------------------------------------------------
    public static Odds lookup(int canonTurnIndex)
    {
        return new Odds( WIN   [ canonTurnIndex ],
                         LOSE  [ canonTurnIndex ],
                         SPLIT [ canonTurnIndex ] );
    }


    //--------------------------------------------------------------------
    private static boolean getIsPreComputed()
    {
        return PersistentInts.retrieve(FLAG_FILE) != null;
    }

    private static void setIsPreComputed()
    {
        PersistentInts.persist(new int[]{1}, FLAG_FILE);
    }
    

    //--------------------------------------------------------------------
    private static void flush()
    {
        PersistentInts.persist(WIN,   WIN_FILE);
        PersistentInts.persist(LOSE,  LOSE_FILE);
        PersistentInts.persist(SPLIT, SPLIT_FILE);
    }
}
