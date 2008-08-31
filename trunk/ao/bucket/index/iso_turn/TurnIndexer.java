package ao.bucket.index.iso_turn;

/**
 * Date: Aug 29, 2008
 * Time: 11:34:21 PM
 */
public enum TurnIndexer
{
//    ZERO(0, 13) { public int subIndex(
//                ,
//                int flopA, int flopB, int flopC, int turn) {
//            return turn;
//        }},
//    ONE(1, 13) { public int subIndex(
//                int holeA, int holeB,
//                int flopA, int flopB, int flopC, int turn) {
//            return turn;
//        }},

    ;

    //--------------------------------------------------------------------
    private final int INDEX;
    private final int SIZE;


    //--------------------------------------------------------------------
    private TurnIndexer(int turnSuitDupeCount, int size)
    {
        INDEX = turnSuitDupeCount;
        SIZE  = size;
    }


    //--------------------------------------------------------------------
//    public abstract int subIndex(Hole hole,
//                                 Card flopA, Card flopB, Card flopC,
//                                 Card turn);
}
