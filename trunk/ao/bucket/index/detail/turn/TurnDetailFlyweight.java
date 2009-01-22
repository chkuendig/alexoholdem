package ao.bucket.index.detail.turn;

import ao.bucket.index.turn.Turn;
import ao.bucket.index.turn.TurnLookup;
import ao.holdem.model.card.Card;
import ao.odds.agglom.Odds;
import ao.util.persist.PersistentBytes;
import ao.util.persist.PersistentFloats;
import ao.util.persist.PersistentInts;

import java.io.File;

/**
 * Date: Jan 22, 2009
 * Time: 3:03:33 PM
 */
public class TurnDetailFlyweight
{
    //--------------------------------------------------------------------
    private static final String F_EXAMPLE     = "ex.byte";
    private static final String F_REPRESENTS  = "rep.byte";
    private static final String F_STRENGTH    = "str.float";
    private static final String F_FIRST_RIVER = "rfst.int";
    private static final String F_RIVER_COUNT = "rcnt.byte";


    //--------------------------------------------------------------------
    public static void persist(TurnDetailFlyweight fw, File toDir)
    {
        File fExample    = new File(toDir, F_EXAMPLE);
        File fRepresent  = new File(toDir, F_REPRESENTS);
        File fStrength   = new File(toDir, F_STRENGTH);
        File fFirstRiver = new File(toDir, F_FIRST_RIVER);
        File fRiverCount = new File(toDir, F_RIVER_COUNT);

        PersistentBytes .persist(fw.EXAMPLE    , fExample);
        PersistentBytes .persist(fw.REPRESENT  , fRepresent);
        PersistentFloats.persist(fw.STRENGTH   , fStrength);
        PersistentInts  .persist(fw.FIRST_RIVER, fFirstRiver);
        PersistentBytes .persist(fw.RIVER_COUNT, fRiverCount);
    }

    public static TurnDetailFlyweight retrieve(File fromDir)
    {
        File fExample    = new File(fromDir, F_EXAMPLE);
        File fRepresent  = new File(fromDir, F_REPRESENTS);
        File fStrength   = new File(fromDir, F_STRENGTH);
        File fFirstRiver = new File(fromDir, F_FIRST_RIVER);
        File fRiverCount = new File(fromDir, F_RIVER_COUNT);

        if (! fExample.canRead()) return null;

        return new TurnDetailFlyweight(
                PersistentBytes .retrieve(fExample),
                PersistentBytes .retrieve(fRepresent),
                PersistentFloats.retrieve(fStrength),
                PersistentInts  .retrieve(fFirstRiver),
                PersistentBytes .retrieve(fRiverCount));
    }


    //--------------------------------------------------------------------
    private final byte[]  EXAMPLE;
    private final byte[]  REPRESENT;
    private final float[] STRENGTH;
    private final int[]   FIRST_RIVER;
    private final byte[]  RIVER_COUNT;


    //--------------------------------------------------------------------
    public TurnDetailFlyweight()
    {
        EXAMPLE     = new byte [ TurnLookup.CANONICAL_COUNT ];
        REPRESENT   = new byte [ TurnLookup.CANONICAL_COUNT ];
        STRENGTH    = new float[ TurnLookup.CANONICAL_COUNT ];
        FIRST_RIVER = new int  [ TurnLookup.CANONICAL_COUNT ];
        RIVER_COUNT = new byte [ TurnLookup.CANONICAL_COUNT ];
    }
    private TurnDetailFlyweight(
            byte[]  example,
            byte[]  represent,
            float[] strength,
            int[]   firstRiver,
            byte[]  riverCount)
    {
        EXAMPLE     = example;
        REPRESENT   = represent;
        STRENGTH    = strength;
        FIRST_RIVER = firstRiver;
        RIVER_COUNT = riverCount;
    }


    //--------------------------------------------------------------------
    public CanonTurnDetail get(int canonIndex)
    {
        return new CanonTurnDetail(
                                     canonIndex,
                Card.VALUES[EXAMPLE[ canonIndex ]],
                REPRESENT          [ canonIndex ],
                STRENGTH           [ canonIndex ],
                FIRST_RIVER        [ canonIndex ],
                RIVER_COUNT        [ canonIndex ]);
    }

    public boolean isInitiated(int canonIndex)
    {
        return REPRESENT[ canonIndex ] == 0;
    }


    //--------------------------------------------------------------------
    public void init(Turn turn, Odds odds)
    {
        int canonIndex = turn.canonIndex();

        EXAMPLE [ canonIndex ] = (byte) turn.community().turn().ordinal();
        STRENGTH[ canonIndex ] = (float) odds.strengthVsRandom();
    }

    public void setRiverInfo(
            int  canonIndex,
            long firstRiver,
            byte riverCount)
    {
        FIRST_RIVER[ canonIndex ] = (int) firstRiver;
        RIVER_COUNT[ canonIndex ] = riverCount;
    }

    public void incrementRepresentation(int canonIndex)
    {
        REPRESENT[ canonIndex ]++;
    }
}
