package ao.holdem.ai.bucket.index.detail.flop;

import ao.holdem.model.canon.flop.Flop;
import ao.holdem.ai.bucket.index.detail.CanonDetail;
import ao.holdem.ai.bucket.index.detail.example.ExampleLookup;
import ao.holdem.ai.bucket.index.detail.preflop.CanonHoleDetail;
import ao.holdem.ai.bucket.index.detail.preflop.HoleDetails;
import ao.holdem.ai.bucket.index.detail.range.CanonRange;
import ao.holdem.model.card.Card;
import ao.holdem.ai.ai.odds.agglom.Odds;
import ao.util.math.Calc;
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
    private static final String F_HOLE       = "hole.byte";
    private static final String F_STRENGTH   = "str.float";
    private static final String F_FIRST_TURN = "rfst.int";
    private static final String F_TURN_COUNT = "rcnt.byte";
    private static final String F_REPRESENTS = "rep.byte";


    //--------------------------------------------------------------------
    public static void persist(FlopDetailFlyweight fw, File toDir)
    {
        File fHole       = new File(toDir, F_HOLE);
        File fStrength   = new File(toDir, F_STRENGTH);
        File fFirstTurn  = new File(toDir, F_FIRST_TURN);
        File fTurnCount  = new File(toDir, F_TURN_COUNT);
        File fRepresents = new File(toDir, F_REPRESENTS);

        PersistentBytes .persist(fw.HOLE      , fHole);
        PersistentFloats.persist(fw.STRENGTH  , fStrength);
        PersistentInts  .persist(fw.FIRST_TURN, fFirstTurn);
        PersistentBytes .persist(fw.TURN_COUNT, fTurnCount);
        PersistentBytes .persist(fw.REPRESENTS, fRepresents);
    }

    public static FlopDetailFlyweight retrieve(File fromDir)
    {
        File fHole       = new File(fromDir, F_HOLE);
        File fStrength   = new File(fromDir, F_STRENGTH);
        File fFirstTurn  = new File(fromDir, F_FIRST_TURN);
        File fTurnCount  = new File(fromDir, F_TURN_COUNT);
        File fRepresents = new File(fromDir, F_REPRESENTS);

        if (! fTurnCount.canRead()) return null;

        return new FlopDetailFlyweight(
                PersistentBytes .retrieve(fHole),
                PersistentFloats.retrieve(fStrength),
                PersistentInts  .retrieve(fFirstTurn),
                PersistentBytes .retrieve(fTurnCount),
                PersistentBytes .retrieve(fRepresents));
    }


    //--------------------------------------------------------------------
    private final byte  HOLE      [];
    private final float STRENGTH  [];
    private final int   FIRST_TURN[];
    private final byte  TURN_COUNT[];
    private final byte  REPRESENTS[];


    //--------------------------------------------------------------------
    public FlopDetailFlyweight()
    {
        HOLE       = new byte [ Flop.CANONS ];
        STRENGTH   = new float[ Flop.CANONS ];
        FIRST_TURN = new int  [ Flop.CANONS ];
        TURN_COUNT = new byte [ Flop.CANONS ];
        REPRESENTS = new byte [ Flop.CANONS ];
    }
    private FlopDetailFlyweight(
            byte  hole      [],
            float strength  [],
            int   firstRiver[],
            byte  turnCount [],
            byte  represents[])
    {
        HOLE       = hole;
        STRENGTH   = strength;
        FIRST_TURN = firstRiver;
        TURN_COUNT = turnCount;
        REPRESENTS = represents;
    }


    //--------------------------------------------------------------------
    public CanonFlopDetail get(int canonIndex)
    {
        return new CanonFlopDetail(canonIndex);
    }

    public CanonRange getTurnRange(int canonIndex)
    {
        return CanonRange.newFromCount(
                FIRST_TURN[ canonIndex ],
                (char) TURN_COUNT[ canonIndex ]);
    }


    //--------------------------------------------------------------------
    public void initiate(Flop flop, Odds odds)
    {
        int         canon   =         flop.canonIndex();
        HOLE      [ canon ] = (byte)  flop.hole().canonIndex();
        STRENGTH  [ canon ] = (float) odds.strengthVsRandom();
        REPRESENTS[ canon ] = 1;
    }

    public void setTurnInfo(
            int  canonIndex,
            int  firstTurn,
            byte turnCount)
    {
        FIRST_TURN[ canonIndex ] = firstTurn;
        TURN_COUNT[ canonIndex ] = turnCount;
    }

    public void incrementRepresentation(int canonIndex)
    {
        REPRESENTS[ canonIndex ]++;
    }


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
        public CanonHoleDetail holeDetail()
        {
            return HoleDetails.lookup( (char)
                    Calc.unsigned( HOLE[CANON_INDEX] ));
        }
        public Card a()
        {
            return ExampleLookup.flopA( CANON_INDEX );
        }
        public Card b()
        {
            return ExampleLookup.flopB( CANON_INDEX );
        }
        public Card c()
        {
            return ExampleLookup.flopC( CANON_INDEX );
        }

        public double strength()
        {
            return STRENGTH  [ CANON_INDEX ];
        }
        public int firstCanonTurn()
        {
            return FIRST_TURN[ CANON_INDEX ];
        }
        public byte canonTurnCount()
        {
            return TURN_COUNT[ CANON_INDEX ];
        }
        public byte represents()
        {
            return REPRESENTS[ CANON_INDEX ];
        }

        public CanonRange turns()
        {
            return CanonRange.newFromCount(
                    firstCanonTurn(),
                    canonTurnCount());
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
