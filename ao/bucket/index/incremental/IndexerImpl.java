package ao.bucket.index.incremental;

import ao.bucket.index.Indexer;
import ao.holdem.model.card.sequence.CardSequence;

/**
 * Date: Aug 16, 2008
 * Time: 2:48:07 PM
 */
public class IndexerImpl implements Indexer
{
    //--------------------------------------------------------------------
    private FlopIndexerImpl flopIndexer = new FlopIndexerImpl();


    //--------------------------------------------------------------------
    public int indexOf(CardSequence cards)
    {
        if (cards.community().isPreflop())
        {
            return cards.hole().suitIsomorphicIndex();
        }
        else
        {
            return flopIndexer.indexOf(
                    cards.hole(),
                    cards.community().flopA(),
                    cards.community().flopB(),
                    cards.community().flopC());
        }
    }
}