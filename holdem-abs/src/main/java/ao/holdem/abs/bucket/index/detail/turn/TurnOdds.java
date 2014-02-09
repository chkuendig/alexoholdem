package ao.holdem.abs.bucket.index.detail.turn;

import ao.Infrastructure;
import ao.holdem.canon.turn.Turn;
import ao.holdem.canon.enumeration.HandEnum;
import ao.holdem.abs.odds.agglom.Odds;
import ao.holdem.abs.odds.agglom.impl.PreciseHeadsUpOdds;
import ao.util.io.Dirs;
import ao.util.pass.Traverser;
import ao.util.persist.PersistentChars;
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
        long minWins = Integer.MAX_VALUE;
        long maxWins = Integer.MIN_VALUE;

        long minLose = Integer.MAX_VALUE;
        long maxLose = Integer.MIN_VALUE;

        long minTies = Integer.MAX_VALUE;
        long maxTies = Integer.MIN_VALUE;

        for (int i = 0; i < Turn.CANONS; i++)
        {
            Odds odds = lookup(i);

            minWins = Math.min(minWins, odds.winOdds());
            maxWins = Math.max(maxWins, odds.winOdds());

            minLose = Math.min(minLose, odds.loseOdds());
            maxLose = Math.max(maxLose, odds.loseOdds());

            minTies = Math.min(minTies, odds.splitOdds());
            maxTies = Math.max(maxTies, odds.splitOdds());
        }

        LOG.info("minWins = " + minWins);
        LOG.info("maxWins = " + maxWins);

        LOG.info("minLose = " + minLose);
        LOG.info("maxLose = " + maxLose);

        LOG.info("minTies = " + minTies);
        LOG.info("maxTies = " + maxTies);
    }


    //--------------------------------------------------------------------
    private static final Logger LOG =
            Logger.getLogger(TurnOdds.class);

    private static final char SENTINAL = Character.MAX_VALUE;


    //--------------------------------------------------------------------
    private static final File DIR =
            Dirs.get(Infrastructure.path(
                    "lookup/canon/detail/turn_odds/"));

    private static final File FLAG_FILE  = new File(DIR,   "flag.int");
    private static final File WIN_FILE   = new File(DIR,   "wins.char");
    private static final File LOSE_FILE  = new File(DIR, "losses.char");
    private static final File SPLIT_FILE = new File(DIR, "splits.char");


    //--------------------------------------------------------------------
    private TurnOdds() {}


    //--------------------------------------------------------------------
    private static final char[] WIN;
    private static final char[] LOSE;
    private static final char[] SPLIT;


    //--------------------------------------------------------------------
    static
    {
        LOG.debug("initializing TurnOdds");

        char[] wins = PersistentChars.retrieve(WIN_FILE);
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
            LOSE  = PersistentChars.retrieve(LOSE_FILE);
            SPLIT = PersistentChars.retrieve(SPLIT_FILE);
        }

        if (! getIsPreComputed())
        {
            LOG.debug("resuming computation");

            computeOdds();
            flush();
            setIsPreComputed();
        }
    }

    private static char[] oddsComponent()
    {
        char[] component = new char[ Turn.CANONS ];
        Arrays.fill( component, SENTINAL );
        return component;
    }


    //--------------------------------------------------------------------
    private static void computeOdds()
    {
        LOG.debug("computing odds");

        final int [] skipCount      = {0};
        final int [] calcCount      = {0};
        final long[] milestoneStart = {0};

        HandEnum.uniqueTurns(
                new Traverser<Turn>() {
            public void traverse(Turn turn) {
                if (WIN[ turn.canonIndex() ] != SENTINAL) {
                    skipCount[0]++;
                    return;
                } else if (skipCount[0] != 0) {
                    LOG.info("skipped " + skipCount[0]);
                    skipCount[0] = 0;
                }

                Odds odds = PreciseHeadsUpOdds.INSTANCE.compute(
                        turn.hole().reify(), turn.community());

                char wins   = (char) odds.winOdds();
                char losses = (char) odds.loseOdds();
                char splits = (char) odds.splitOdds();

                WIN  [ turn.canonIndex() ] = wins;
                LOSE [ turn.canonIndex() ] = losses;
                SPLIT[ turn.canonIndex() ] = splits;

                checkpoint( calcCount[0]++, milestoneStart );
            }});
    }

    private static void checkpoint(
            int count, long[] milestoneStart)
    {
        if (milestoneStart[0] == 0)
            milestoneStart[0] = System.currentTimeMillis();

        if (count % 1000 == 0)
            System.out.print(".");

        boolean milesoneReached = ((count + 1) % 50000 == 0);
        if (milesoneReached) {
            System.out.println(
                    " " + (count + 1) + ", completed 50,000 in " +
                    (System.currentTimeMillis() - milestoneStart[0]));
        }

        if ((count + 1) % 1000000 == 0) flush();

        if (milesoneReached)
            milestoneStart[0] = System.currentTimeMillis();
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
    private synchronized static void flush()
    {
        long start = System.currentTimeMillis();

        PersistentChars.persist(WIN,   WIN_FILE);
        PersistentChars.persist(LOSE,  LOSE_FILE);
        PersistentChars.persist(SPLIT, SPLIT_FILE);

        System.out.println("\nflushed checkpoint, took " +
                            (System.currentTimeMillis() - start));
    }
}