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

        final int[]              count    = {0};
        final PreciseHeadsUpOdds oddsCalc = new PreciseHeadsUpOdds();
        new CanonTraverser().traverseTurns(
                null, new Traverser<Turn>() {
            public void traverse(Turn turn) {
                if (WIN[ turn.canonIndex() ] != -1) return;

                Odds odds = oddsCalc.compute(
                        turn.hole(), turn.community());

                int wins   = asInt(odds.winOdds());
                int losses = asInt(odds.loseOdds());
                int splits = asInt(odds.splitOdds());

                WIN  [ turn.canonIndex() ] = wins;
                LOSE [ turn.canonIndex() ] = losses;
                SPLIT[ turn.canonIndex() ] = splits;

                detectOverflow(turn.canonIndex(), odds,
                               wins, losses, splits);

                checkpoint( count[0]++ );
            }
        });
    }

    private static void checkpoint(int count)
    {
        if ( count % 100 == 0)
            System.out.print(".");

        if ((count + 1) % 5000 == 0)
            System.out.println(" " + count);

        if ((count + 1) % 100000 == 0)
            flush();
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
