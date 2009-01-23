package ao.bucket.index.detail.flop;

import ao.bucket.index.detail.CanonDetail;
import ao.bucket.index.flop.Flop;
import ao.bucket.index.flop.FlopLookup;
import ao.holdem.model.card.Card;
import ao.holdem.model.card.Community;
import ao.odds.agglom.Odds;
import ao.util.persist.PersistentBytes;
import ao.util.persist.PersistentFloats;
import ao.util.persist.PersistentInts;

import java.io.File;

/**
 * Date: Jan 23, 2009
 * Time: 1:42:51 PM
 */
public class FlopDetailFlyweight
{
    //--------------------------------------------------------------------
    private static final String F_EXAMPLE_A   = "ex1.byte";
    private static final String F_EXAMPLE_B   = "ex2.byte";
    private static final String F_EXAMPLE_C   = "ex3.byte";

//    private static final String F_REPRESENTS  = "rep.byte";
    private static final String F_STRENGTH    = "str.float";
    private static final String F_FIRST_RIVER = "rfst.int";
    private static final String F_TURN_COUNT  = "rcnt.byte";


    //--------------------------------------------------------------------
    public static void persist(FlopDetailFlyweight fw, File toDir)
    {
        File fExampleA   = new File(toDir, F_EXAMPLE_A);
        File fExampleB   = new File(toDir, F_EXAMPLE_B);
        File fExampleC   = new File(toDir, F_EXAMPLE_C);
//        File fRepresent  = new File(toDir, F_REPRESENTS);
        File fStrength  = new File(toDir, F_STRENGTH);
        File fFirstTurn = new File(toDir, F_FIRST_RIVER);
        File fTurnCount = new File(toDir, F_TURN_COUNT);

        PersistentBytes.persist(fw.EXAMPLE_A  , fExampleA);
        PersistentBytes.persist(fw.EXAMPLE_B  , fExampleB);
        PersistentBytes.persist(fw.EXAMPLE_C  , fExampleC);
//        PersistentBytes .persist(fw.REPRESENT  , fRepresent);
        PersistentFloats.persist(fw.STRENGTH   , fStrength);
        PersistentInts  .persist(fw.FIRST_TURN, fFirstTurn);
        PersistentBytes .persist(fw.TURN_COUNT, fTurnCount);
    }

    public static FlopDetailFlyweight retrieve(File fromDir)
    {
        File fExampleA   = new File(fromDir, F_EXAMPLE_A);
        File fExampleB   = new File(fromDir, F_EXAMPLE_B);
        File fExampleC   = new File(fromDir, F_EXAMPLE_C);
//        File fRepresent  = new File(fromDir, F_REPRESENTS);
        File fStrength  = new File(fromDir, F_STRENGTH);
        File fFirstTurn = new File(fromDir, F_FIRST_RIVER);
        File fTurnCount = new File(fromDir, F_TURN_COUNT);

        if (! fTurnCount.canRead()) return null;

        return new FlopDetailFlyweight(
                PersistentBytes .retrieve(fExampleA),
                PersistentBytes .retrieve(fExampleB),
                PersistentBytes .retrieve(fExampleC),
//                PersistentBytes .retrieve(fRepresent),
                PersistentFloats.retrieve(fStrength),
                PersistentInts  .retrieve(fFirstTurn),
                PersistentBytes .retrieve(fTurnCount));
    }


    //--------------------------------------------------------------------
    private final byte[]  EXAMPLE_A;
    private final byte[]  EXAMPLE_B;
    private final byte[]  EXAMPLE_C;
//    private final byte[]  REPRESENT;
    private final float[] STRENGTH;
    private final int[] FIRST_TURN;
    private final byte[] TURN_COUNT;


    //--------------------------------------------------------------------
    public FlopDetailFlyweight()
    {
        EXAMPLE_A   = new byte [ FlopLookup.CANONS ];
        EXAMPLE_B   = new byte [ FlopLookup.CANONS ];
        EXAMPLE_C   = new byte [ FlopLookup.CANONS ];
//        REPRESENT   = new byte [ FlopLookup.CANONS ];
        STRENGTH    = new float[ FlopLookup.CANONS ];
        FIRST_TURN = new int  [ FlopLookup.CANONS ];
        TURN_COUNT = new byte [ FlopLookup.CANONS ];
    }
    private FlopDetailFlyweight(
            byte[]  exampleA,
            byte[]  exampleB,
            byte[]  exampleC,
//            byte[]  represent,
            float[] strength,
            int[]   firstRiver,
            byte[]  riverCount)
    {
        EXAMPLE_A   = exampleA;
        EXAMPLE_B   = exampleB;
        EXAMPLE_C   = exampleC;
//        REPRESENT   = represent;
        STRENGTH    = strength;
        FIRST_TURN = firstRiver;
        TURN_COUNT = riverCount;
    }


    //--------------------------------------------------------------------
    public CanonFlopDetail get(int canonIndex)
    {
        return new CanonFlopDetail(canonIndex);
    }

//    public boolean isInitiated(int canonIndex)
//    {
//        return REPRESENT[ canonIndex ] == 0;
//    }


    //--------------------------------------------------------------------
    public void init(Flop flop, Odds odds)
    {
        int       canon = flop.canonIndex();
        Community comm  = flop.community();

        EXAMPLE_A[ canon ] = (byte) comm.flopA().ordinal();
        EXAMPLE_B[ canon ] = (byte) comm.flopB().ordinal();
        EXAMPLE_C[ canon ] = (byte) comm.flopC().ordinal();
        STRENGTH [ canon ] = (float) odds.strengthVsRandom();
    }

    public void setTurnInfo(
            int  canonIndex,
            int  firstTurn,
            byte turnCount)
    {
        FIRST_TURN[ canonIndex ] = firstTurn;
        TURN_COUNT[ canonIndex ] = turnCount;
    }

//    public void incrementRepresentation(int canonIndex)
//    {
//        REPRESENT[ canonIndex ]++;
//    }


    //--------------------------------------------------------------------
    public class CanonFlopDetail implements CanonDetail
    {
        //----------------------------------------------------------------
        private final int CANON_INDEX;


        //----------------------------------------------------------------
        public CanonFlopDetail(int canonIndex)
        {
            CANON_INDEX = canonIndex;
        }


        //----------------------------------------------------------------
        public long canonIndex()
        {
            return CANON_INDEX;
        }
        public Card exampleA()
        {
            return Card.VALUES[EXAMPLE_A[ CANON_INDEX ]];
        }
        public Card exampleB()
        {
            return Card.VALUES[EXAMPLE_B[ CANON_INDEX ]];
        }
        public Card exampleC()
        {
            return Card.VALUES[EXAMPLE_C[ CANON_INDEX ]];
        }
//        public byte represents()
//        {
//            return REPRESENT[ CANON_INDEX ];
//        }
        public double strengthVsRandom()
        {
            return STRENGTH[ CANON_INDEX ];
        }
        public int firstCanonTurn()
        {
            return FIRST_TURN[CANON_INDEX];
        }
        public byte canonTurnCount()
        {
            return TURN_COUNT[ CANON_INDEX ];
        }


        //----------------------------------------------------------------
        @Override public String toString()
        {
            return String.valueOf(CANON_INDEX);
        }
        @Override public boolean equals(Object o)
        {
            return !(o == null || getClass() != o.getClass()) &&
                   CANON_INDEX == ((CanonFlopDetail) o).CANON_INDEX;
        }
        @Override public int hashCode()
        {
            return CANON_INDEX;
        }
    }
}
