package ao.bucket.index.incremental;

import ao.bucket.index.Indexer;
import ao.holdem.model.card.Hole;
import ao.holdem.model.card.sequence.CardSequence;

/**
 * Date: Aug 16, 2008
 * Time: 2:48:07 PM
 */
public class IndexerImpl implements Indexer
{
    public int indexOf(CardSequence cards)
    {
        if (cards.community().isPreflop())
        {
            return cards.hole().suitIsomorphicIndex();
        }

        Hole    hole = cards.hole();
//        IsoFlop flop = isoHole.flop(hole,
//                                    cards.community().flopA(),
//                                    cards.community().flopB(),
//                                    cards.community().flopC());
//
//        int offset    = 0;
//        int holeIndex = isoHole.holeCase().subIndex();
//
//        FlopSubIndexer subIndexers[] =
//                (isoHole.holeCase().type() == Type.PAIR
//                 ? PostPairFlop.values()
//                 : isoHole.holeCase().type() == Type.SUITED
//                   ? PostSuitedFlop.values()
//                   : PostUnsuitedFlop.values());
//
//        for (FlopSubIndexer subIndexer : subIndexers)
//        {
//            if (subIndexer.caseEquals(flop.flopCase()))
//            {
//                return offset +
//                       subIndexer.subIndex( flop ) *
//                            isoHole.holeCase().type().members() +
//                       holeIndex;
//
////                return subIndexer.subIndex( flop ) *
////                            isoHole.holeCase().type().members() +
////                       holeIndex;
//
////                return subIndexer.subIndex( flop );
//            }
//            offset += subIndexer.size() *
//                        isoHole.holeCase().type().members();
//        }

        return -1;
    }




}
