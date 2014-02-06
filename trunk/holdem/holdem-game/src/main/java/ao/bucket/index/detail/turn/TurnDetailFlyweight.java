package ao.bucket.index.detail.turn;

import ao.bucket.index.canon.turn.Turn;
import ao.bucket.index.detail.CanonDetail;
import ao.bucket.index.detail.example.ExampleLookup;
import ao.bucket.index.detail.range.CanonRange;
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
//    private static final String F_EXAMPLE     = "ex.byte";
    private static final String F_REPRESENTS  = "rep.byte";
    private static final String F_STRENGTH    = "str.char";
//    private static final String F_FIRST_RIVER = "first_river.int";
//    private static final String F_RIVER_COUNT = "rcnt.byte";


    //--------------------------------------------------------------------
    public static void persist(TurnDetailFlyweight fw, File toDir)
    {
//        File fExample    = new File(toDir, F_EXAMPLE);
        File fRepresent  = new File(toDir, F_REPRESENTS);
        File fStrength   = new File(toDir, F_STRENGTH);
//        File fFirstRiver = new File(toDir, F_FIRST_RIVER);

//        PersistentBytes .persist(fw.EXAMPLE    , fExample);
        PersistentBytes.persist(fw.REPRESENT  , fRepresent);
        PersistentChars.persist(fw.STRENGTH   , fStrength);
//        PersistentInts .persist(fw.FIRST_RIVER, fFirstRiver);
    }

    public static TurnDetailFlyweight retrieve(File fromDir)
    {
//        File fExample    = new File(fromDir, F_EXAMPLE);
        File fRepresent  = new File(fromDir, F_REPRESENTS);
        File fStrength   = new File(fromDir, F_STRENGTH);
//        File fFirstRiver = new File(fromDir, F_FIRST_RIVER);

        if (! fStrength.canRead()) return null;

        return new TurnDetailFlyweight(
//                PersistentBytes .retrieve(fExample),
                PersistentBytes .retrieve(fRepresent),
                PersistentChars.retrieve(fStrength)//,
//                PersistentInts .retrieve(fFirstRiver)
        );
    }


    //--------------------------------------------------------------------
//    private final byte[]  EXAMPLE;
    private final byte[] REPRESENT;
    private final char[] STRENGTH;
//    private final int [] FIRST_RIVER;


    //--------------------------------------------------------------------
    public TurnDetailFlyweight()
    {
//        EXAMPLE     = new byte [ TurnLookup.CANONS];
        REPRESENT   = new byte [ Turn.CANONS];
        STRENGTH    = new char[ Turn.CANONS ];
//        FIRST_RIVER = new int [ TurnLookup.CANONS ];
    }
    private TurnDetailFlyweight(
//            byte[]  example,
            byte[]  represent,
            char[] strength//,
//            int [] firstRiver
            )
    {
//        EXAMPLE     = example;
        REPRESENT   = represent;
        STRENGTH    = strength;
//        FIRST_RIVER = firstRiver;
    }


    //--------------------------------------------------------------------
    public CanonTurnDetail get(int canonIndex)
    {
        return new CanonTurnDetail(canonIndex);
    }

    // false positives are OK
    public boolean isInitiated(int canonIndex)
    {
        return STRENGTH[ canonIndex ] != 0;
    }


    //--------------------------------------------------------------------
    public void initiate(Turn turn, Odds odds)
    {
        int canonIndex = turn.canonIndex();

//        EXAMPLE [ canonIndex ] = (byte) turn.community().turn().ordinal();

        STRENGTH[ canonIndex ] =
                strengthFromDouble(odds.strengthVsRandom());
    }

//    public void setRiverInfo(
//            int  canonIndex,
//            long firstRiver)
//    {
//        FIRST_RIVER[ canonIndex ] = (int) firstRiver;
//    }

    public void incrementRepresentation(int canonIndex)
    {
        REPRESENT[ canonIndex ]++;
    }


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
            return ExampleLookup.turn( CANON_INDEX );
//            return Card.VALUES[EXAMPLE[ CANON_INDEX ]];
        }
        public byte represents()
        {
            return REPRESENT[ CANON_INDEX ];
        }
        public double strength()
        {
            return strengthToDouble(STRENGTH [ CANON_INDEX ]);
        }


        //----------------------------------------------------------------
        public long firstCanonRiver()
        {
//            return Calc.unsigned( FIRST_RIVER[ CANON_INDEX ]);
            return TurnRivers.firstRiverOf(    CANON_INDEX );
        }
        public long lastCanonRiver()
        {
//            return (CANON_INDEX == (TurnLookup.CANONS - 1))
//                    ? RiverLookup.CANONS - 1
//                    : Calc.unsigned(
//                            FIRST_RIVER[ CANON_INDEX + 1 ]) - 1;
            return TurnRivers.lastRiverOf(     CANON_INDEX );
        }
        public byte canonRiverCount()
        {
//            return (byte)(lastCanonRiver() - firstCanonRiver() + 1);
            return TurnRivers.canonRiverCount( CANON_INDEX );
        }
        public CanonRange range()
        {
//            return new CanonRange(
//                    firstCanonRiver(),
//                    canonRiverCount());
            return TurnRivers.rangeOf(         CANON_INDEX );
        }


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
