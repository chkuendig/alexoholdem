package ao.bucket.index.incremental;

import ao.bucket.index.Indexer;
import ao.bucket.index.flop.FlopSubIndexer;
import ao.bucket.index.flop.PostPairFlop;
import ao.bucket.index.flop.PostSuitedFlop;
import ao.bucket.index.flop.PostUnsuitedFlop;
import ao.bucket.index.iso_cards.IsoFlop;
import ao.bucket.index.iso_cards.IsoHole;
import ao.bucket.index.iso_case.HoleCase.Type;
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
            return new HoleIndexer()
                        .indexOf(cards.hole());
        }

        Hole       hole = cards.hole();
        IsoHole isoHole = hole.isomorphism();

        IsoFlop flop = isoHole.flop(hole,
                                    cards.community().flopA(),
                                    cards.community().flopB(),
                                    cards.community().flopC());

        int offset    = 0;
        int holeIndex = isoHole.holeCase().subIndex();

        FlopSubIndexer subIndexers[] =
                (isoHole.holeCase().type() == Type.PAIR
                 ? PostPairFlop.values()
                 : isoHole.holeCase().type() == Type.SUITED
                   ? PostSuitedFlop.values()
                   : PostUnsuitedFlop.values());

        for (FlopSubIndexer subIndexer : subIndexers)
        {
            if (subIndexer.caseEquals(flop.flopCase()))
            {
                return offset +
                       subIndexer.subIndex( flop ) *
                            isoHole.holeCase().type().members() +
                       holeIndex;

//                return subIndexer.subIndex( flop ) *
//                            isoHole.holeCase().type().members() +
//                       holeIndex;

//                return subIndexer.subIndex( flop );
            }
            offset += subIndexer.size() *
                        isoHole.holeCase().type().members();
        }

        return -1;
    }




}
