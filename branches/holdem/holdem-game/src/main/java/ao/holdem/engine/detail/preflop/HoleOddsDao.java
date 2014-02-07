package ao.holdem.engine.detail.preflop;

import ao.holdem.model.canon.hole.CanonHole;
import ao.holdem.engine.agglom.Odds;
import ao.util.io.Dirs;
import ao.util.persist.PersistentInts;
import ao.util.persist.PersistentLongs;

import java.io.File;
import java.util.Arrays;

/**
 * Date: Jan 28, 2009
 * Time: 10:56:03 AM
 *
 * Split into a separate class from HoleOdds so that
 *  the isMemoized check doesn't trigger computation.
 */
public class HoleOddsDao
{
    //--------------------------------------------------------------------
    private HoleOddsDao() {}


    //--------------------------------------------------------------------
    private static final File DIR =
            Dirs.get("lookup/canon/detail/hole_odds/");

    private static final File FLAG_FILE  = new File(DIR,   "flag.int");
    private static final File WIN_FILE   = new File(DIR,   "wins.long");
    private static final File LOSE_FILE  = new File(DIR, "losses.long");
    private static final File SPLIT_FILE = new File(DIR, "splits.long");

    private static final long    SENTINAL   = -1;
    private static final boolean isMemoized = isPreComputed();


    //--------------------------------------------------------------------
    private static long[] WIN;
    private static long[] LOSE;
    private static long[] SPLIT;


    //--------------------------------------------------------------------
    public static void init()
    {
        WIN   = oddsComponent();
        LOSE  = oddsComponent();
        SPLIT = oddsComponent();
    }

    private static long[] oddsComponent()
    {
        long[] component = new long[ CanonHole.CANONS];
        Arrays.fill( component, SENTINAL );
        return component;
    }


    //--------------------------------------------------------------------
    public static boolean retrieve()
    {
        WIN   = PersistentLongs.retrieve(WIN_FILE);
        LOSE  = PersistentLongs.retrieve(LOSE_FILE);
        SPLIT = PersistentLongs.retrieve(SPLIT_FILE);

        return WIN != null;
    }


    //--------------------------------------------------------------------
    public static void persist()
    {
        PersistentLongs.persist(WIN,   WIN_FILE);
        PersistentLongs.persist(LOSE,  LOSE_FILE);
        PersistentLongs.persist(SPLIT, SPLIT_FILE);
    }


    //--------------------------------------------------------------------
    public static boolean isMemoized()
    {
        return isMemoized;
    }

    public static boolean isPreComputed()
    {
        return FLAG_FILE.canRead();
    }

    public static void setPreComputed()
    {
        PersistentInts.persist(new int[]{1}, FLAG_FILE);
    }


    //--------------------------------------------------------------------
    public static boolean isSet(int canonHole)
    {
        return WIN[ canonHole ] != SENTINAL;
    }

    public static void set(int canonHole, Odds odds)
    {
        WIN   [ canonHole ] = odds.winOdds();
        LOSE  [ canonHole ] = odds.loseOdds();
        SPLIT [ canonHole ] = odds.splitOdds();
    }

    public static Odds get(int canonHoleIndex)
    {
        return new Odds( WIN   [ canonHoleIndex ],
                         LOSE  [ canonHoleIndex ],
                         SPLIT [ canonHoleIndex ] );
    }
}
