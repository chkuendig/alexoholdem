package ao.bucket.index.detail.turn;

import ao.bucket.index.detail.CanonDetail;
import ao.bucket.index.turn.Turn;
import ao.bucket.index.turn.TurnLookup;
import ao.holdem.model.card.Card;
import ao.odds.agglom.Odds;
import ao.util.persist.PersistentBytes;
import ao.util.persist.PersistentChars;

import java.io.File;

/**
 * Date: Jan 22, 2009
 * Time: 3:03:33 PM
 */
public class TurnDetailFlyweight
{
    //--------------------------------------------------------------------
    private static final String F_EXAMPLE     = "ex.byte";
//    private static final String F_REPRESENTS  = "rep.byte";
    private static final String F_STRENGTH    = "str.char";
//    private static final String F_FIRST_RIVER = "rfst.int";
//    private static final String F_RIVER_COUNT = "rcnt.byte";


    //--------------------------------------------------------------------
    public static void persist(TurnDetailFlyweight fw, File toDir)
    {
        File fExample    = new File(toDir, F_EXAMPLE);
//        File fRepresent  = new File(toDir, F_REPRESENTS);
        File fStrength   = new File(toDir, F_STRENGTH);
//        File fFirstRiver = new File(toDir, F_FIRST_RIVER);
//        File fRiverCount = new File(toDir, F_RIVER_COUNT);

        PersistentBytes .persist(fw.EXAMPLE    , fExample);
//        PersistentBytes .persist(fw.REPRESENT  , fRepresent);
        PersistentChars.persist(fw.STRENGTH   , fStrength);
//        PersistentInts .persist(fw.FIRST_RIVER, fFirstRiver);
//        PersistentBytes.persist(fw.RIVER_COUNT, fRiverCount);
    }

    public static TurnDetailFlyweight retrieve(File fromDir)
    {
        File fExample    = new File(fromDir, F_EXAMPLE);
//        File fRepresent  = new File(fromDir, F_REPRESENTS);
        File fStrength   = new File(fromDir, F_STRENGTH);
//        File fFirstRiver = new File(fromDir, F_FIRST_RIVER);
//        File fRiverCount = new File(fromDir, F_RIVER_COUNT);

        if (! fStrength.canRead()) return null;

        return new TurnDetailFlyweight(
                PersistentBytes .retrieve(fExample),
//                PersistentBytes .retrieve(fRepresent),
                PersistentChars.retrieve(fStrength)//,
//                PersistentInts .retrieve(fFirstRiver),
//                PersistentBytes.retrieve(fRiverCount)
        );
    }


    //--------------------------------------------------------------------
    private final byte[]  EXAMPLE;
//    private final byte[]  REPRESENT;
    private final char[] STRENGTH;
//    private final int[]  FIRST_RIVER;
//    private final byte[] RIVER_COUNT;


    //--------------------------------------------------------------------
    public TurnDetailFlyweight()
    {
        EXAMPLE     = new byte [ TurnLookup.CANONS];
//        REPRESENT   = new byte [ TurnLookup.CANONS];
        STRENGTH    = new char[ TurnLookup.CANONS ];
//        FIRST_RIVER = new int [ TurnLookup.CANONS ];
//        RIVER_COUNT = new byte[ TurnLookup.CANONS ];
    }
    private TurnDetailFlyweight(
            byte[]  example,
//            byte[]  represent,
            char[] strength//,
//            int[]   firstRiver,
//            byte[]  riverCount
            )
    {
        EXAMPLE     = example;
//        REPRESENT   = represent;
        STRENGTH    = strength;
//        FIRST_RIVER = firstRiver;
//        RIVER_COUNT = riverCount;
    }


    //--------------------------------------------------------------------
    public CanonTurnDetail get(int canonIndex)
    {
        return new CanonTurnDetail(canonIndex);
    }

//    public boolean isInitiated(int canonIndex)
//    {
//        return STRENGTH[ canonIndex ] == 0;
//    }


    //--------------------------------------------------------------------
    public void init(Turn turn, Odds odds)
    {
        int canonIndex = turn.canonIndex();

        EXAMPLE [ canonIndex ] = (byte) turn.community().turn().ordinal();

        STRENGTH[ canonIndex ] =
                strengthFromDouble(odds.strengthVsRandom());
    }

//    public void setRiverInfo(
//            int  canonIndex,
//            long firstRiver,
//            byte riverCount)
//    {
//        FIRST_RIVER[ canonIndex ] = (int) firstRiver;
//        RIVER_COUNT[ canonIndex ] = riverCount;
//    }

//    public void incrementRepresentation(int canonIndex)
//    {
//        REPRESENT[ canonIndex ]++;
//    }


    //--------------------------------------------------------------------
    private static char strengthFromDouble(double strength)
    {
        return (char)(Character.MAX_VALUE * strength);
    }
    private static double strengthToDouble(char strength)
    {
        return (double) strength / Character.MAX_VALUE;
    }



    //--------------------------------------------------------------------
    public class CanonTurnDetail implements CanonDetail
    {
        //----------------------------------------------------------------
        private final int CANON_INDEX;


        //----------------------------------------------------------------
        public CanonTurnDetail(int canonIndex)
        {
            CANON_INDEX = canonIndex;
        }


        //----------------------------------------------------------------
        public long canonIndex()
        {
            return CANON_INDEX;
        }
        public Card example()
        {
            return Card.VALUES[EXAMPLE[ CANON_INDEX ]];
        }
//        public byte represents()
//        {
//            return REPRESENT[ CANON_INDEX ];
//        }
        public double strength()
        {
            return strengthToDouble(STRENGTH[ CANON_INDEX ]);
        }
//        public long firstCanonRiver()
//        {
//            return Calc.unsigned( FIRST_RIVER[CANON_INDEX] );
//        }
//        public byte canonRiverCount()
//        {
//            return RIVER_COUNT[ CANON_INDEX ];
//        }


        //----------------------------------------------------------------
        @Override public String toString()
        {
            return String.valueOf(CANON_INDEX);
        }
        @Override public boolean equals(Object o)
        {
            return !(o == null || getClass() != o.getClass()) &&
                   CANON_INDEX == ((CanonTurnDetail) o).CANON_INDEX;
        }
        @Override public int hashCode()
        {
            return CANON_INDEX;
        }
    }

}
