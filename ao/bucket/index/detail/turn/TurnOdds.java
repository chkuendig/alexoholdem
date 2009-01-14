package ao.bucket.index.detail.turn;

import ao.bucket.index.detail.enumeration.CanonTraverser;
import ao.bucket.index.turn.Turn;
import ao.bucket.index.turn.TurnLookup;
import ao.odds.agglom.Odds;
import ao.odds.agglom.impl.PreciseHeadsUpOdds;
import ao.util.misc.Traverser;
import ao.util.persist.PersistentInts;
import org.apache.log4j.Logger;

import java.io.File;

/**
 * Date: Jan 14, 2009
 * Time: 1:37:12 PM
 */
public class TurnOdds
{
    //--------------------------------------------------------------------
    private static final Logger LOG =
            Logger.getLogger(TurnOdds.class);


    //--------------------------------------------------------------------
    private static final String DIR = "lookup/canon/detail/turn_odds/";
    static { //noinspection ResultOfMethodCallIgnored
        new File(DIR).mkdirs(); }

    private static final String WIN_FILE   = DIR + "wins.int";
    private static final String LOSE_FILE  = DIR + "losses.int";
    private static final String SPLIT_FILE = DIR + "splits.int";


    //--------------------------------------------------------------------
    private TurnOdds() {}


    //--------------------------------------------------------------------
    private static final int[] WIN;
    private static final int[] LOSE;
    private static final int[] SPLIT;


    //--------------------------------------------------------------------
    static
    {
        LOG.debug("initializing TurnOdds");

        int[] wins = PersistentInts.retrieve(WIN_FILE);
        if (wins == null)
        {
            WIN   = new int[ TurnLookup.CANONICAL_COUNT ];
            LOSE  = new int[ TurnLookup.CANONICAL_COUNT ];
            SPLIT = new int[ TurnLookup.CANONICAL_COUNT ];

            computeOdds();

            PersistentInts.persist(WIN,   WIN_FILE);
            PersistentInts.persist(LOSE,  LOSE_FILE);
            PersistentInts.persist(SPLIT, SPLIT_FILE);
        }
        else
        {
            WIN   = wins;
            LOSE  = PersistentInts.retrieve(LOSE_FILE);
            SPLIT = PersistentInts.retrieve(SPLIT_FILE);
        }
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

                if ( count[0]      % 100  == 0) System.out.print(".");
                if ((count[0] + 1) % 5000 == 0) System.out.println();
                count[0]++;
            }
        });
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
    public static void main(String[] args)
    {
        System.out.println( lookup(0) );
    }
}
