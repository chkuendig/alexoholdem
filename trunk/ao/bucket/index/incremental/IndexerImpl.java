package ao.bucket.index.incremental;

import ao.bucket.index.Indexer;
import ao.bucket.index.iso_flop.IsoFlop;
import ao.holdem.model.card.Card;
import ao.holdem.model.card.sequence.CardSequence;

/**
 * Date: Aug 16, 2008
 * Time: 2:48:07 PM
 */
public class IndexerImpl implements Indexer
{
    //--------------------------------------------------------------------
    private FlopIndexer flopIndexer = new FlopIndexer();
    private TurnIndexer turnIndexer = new TurnIndexer();


    //--------------------------------------------------------------------
    public int indexOf(CardSequence cards)
    {
        if (cards.community().isPreflop())
        {
            return cards.hole().suitIsomorphicIndex();
        }
        else
        {
            Card flopCards[] = {cards.community().flopA(),
                                cards.community().flopB(),
                                cards.community().flopC()};
            IsoFlop isoFlop = cards.hole().isoFlop(flopCards);
            int flopIndex   = flopIndexer.indexOf(cards.hole(), isoFlop);

            if (! cards.community().hasTurn())
            {
                return flopIndex;
            }
            else
            {
                return turnIndexer.indexOf(
                        cards.hole(), flopCards, isoFlop, flopIndex,
                        cards.community().turn());
            }
        }
    }
}