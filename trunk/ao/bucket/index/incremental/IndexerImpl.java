package ao.bucket.index.incremental;

import ao.bucket.index.Indexer;
import ao.bucket.index.iso_flop.IsoFlop;
import ao.bucket.index.iso_turn.IsoTurn;
import ao.holdem.model.card.Card;
import ao.holdem.model.card.sequence.CardSequence;

/**
 * Date: Aug 16, 2008
 * Time: 2:48:07 PM
 */
public class IndexerImpl implements Indexer
{
    //--------------------------------------------------------------------
    private  FlopIndexer  flopIndexer = new  FlopIndexer();
    private  TurnIndexer  turnIndexer = new  TurnIndexer();
    private RiverIndexer riverIndexer = new RiverIndexer();


    //--------------------------------------------------------------------
    public long indexOf(CardSequence cards)
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
                Card    turnCard = cards.community().turn();
                IsoTurn isoTurn  =
                    isoFlop.isoTurn(
                            cards.hole().asArray(), flopCards, turnCard);

                int turnIndex = turnIndexer.indexOf(flopIndex, isoTurn);
                if (! cards.community().hasRiver())
                {
                    return turnIndex;
                }
                else
                {
                    return riverIndexer.indexOf(
                            cards.hole(), flopCards,
                            turnCard, isoTurn, turnIndex,
                            cards.community().river());
                }
            }
        }
    }
}